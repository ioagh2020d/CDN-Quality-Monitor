package pl.edu.agh.cqm.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.model.Url;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;
import pl.edu.agh.cqm.service.MonitorService;
import pl.edu.agh.cqm.service.ParameterService;
import pl.edu.agh.cqm.service.ThroughputService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ThroughputServiceImpl implements ThroughputService {

    private final PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS;
    private final ThroughputSampleRepository dataRepository;
    private final Logger logger = LogManager.getLogger(ThroughputServiceImpl.class);
    private final int snapLen;
    private final int timeout;
    private final ParameterService parameterService;
    private final MonitorService monitorService;
    private int measurementTime;
    private final int sessionBreakTime;
    private final String interfaceName;
    private String myIP;
    private PcapNetworkInterface nif;
    private List<UrlData> urls;

    public ThroughputServiceImpl(CqmConfiguration configuration,
                                 ParameterService parameterService,
                                 ThroughputSampleRepository dataRepository,
                                 MonitorService monitorService) throws PcapNativeException {
        this.dataRepository = dataRepository;
        this.parameterService = parameterService;
        this.monitorService = monitorService;
        snapLen = configuration.getPcapMaxPacketLength();
        timeout = configuration.getPcapTimeout();
        sessionBreakTime = configuration.getPcapSessionBreak();
        interfaceName = configuration.getInterfaceName();

        getNIF();

    }


    private void getNIF() throws PcapNativeException {

        this.nif = Pcaps.getDevByName(interfaceName);
        if (nif == null) {
            throw new IllegalArgumentException("Could not initialize NIF. Invalid interface name.");
        }
        myIP = nif.getAddresses().get(0).getAddress().getHostAddress();
        logger.info("Local IP address: " + myIP);
    }

    public void doMeasurement() {
        measurementTime = 1000 * 60 * parameterService.getPassiveSamplingRate();
        urls = new ArrayList<>();
        for (Url url : parameterService.getActiveUrls()) {
            UrlData urlsData = new UrlData();
            urlsData.url = url;
            urls.add(urlsData);
        }
        logger.debug("throughput doMeasurement start");
        try {
            this.measureThroughput(urls, myIP);
            logger.info("measurement done");
            this.calcOutput(urls);
            urls.forEach(c -> {
                try {
                    ThroughputSample sample = ThroughputSample.builder()
                        .throughput((long) c.throughput)
                        .timestamp(Instant.now())
                        .url(c.url)
                        .monitor(monitorService.getLocalMonitor())
                        .build();
                    dataRepository.save(sample);
                } catch (NullPointerException e) {
                    logger.warn("empty throughput session");
                } catch (IndexOutOfBoundsException e) {
                    logger.warn("empty throughput session (index)");
                }

            });

        } catch (PcapNativeException e) {
            logger.error("PCAP native exception");
        } catch (NotOpenException e) {
            logger.error("Internet card not opened");
        }
    }


    private boolean parseDNS(DnsPacket dnsPacket) {
        if (!dnsPacket.getHeader().isResponse()) {
            return false;
        }
        for (UrlData urlData : urls) {
            if (!dnsPacket.getHeader().getQuestions().get(0).getQName().toString().contains(urlData.url.getAddress())) {
                continue;
            }
            List<DnsResourceRecord> dnsRecords = dnsPacket
                    .getHeader()
                    .getAnswers()
                    .stream()
                    .filter(r -> r.getDataType().valueAsString().equals("1"))
                    .collect(Collectors.toList());
            if (dnsRecords.size() == 0) {
                return false;
            }
            dnsRecords.stream()
                    .map(r -> ((DnsRDataA) r.getRData()).getAddress().getHostAddress())
                    .forEach(urlData::tryAdd);
            logger.debug("found dns response for url {} : {} : {}", urlData.url.getAddress(), dnsPacket.getHeader().getQuestions().get(0).getQName().toString(), urlData.ips.get(0));
            return true;

        }
        return false;
    }

    private void measureThroughput(List<UrlData> cdns, String myAddr) throws PcapNativeException, NotOpenException {

        List<String> ips;
        Map<String, UrlData> ipMap = new HashMap<>();


        PcapHandle handle = nif.openLive(snapLen, mode, timeout);

        long startTime = System.currentTimeMillis();
        long stopTime = startTime + measurementTime;
        boolean filterChangedFlag;

        logger.debug("starttime:{} stoptime:{}", startTime, stopTime);
        while (System.currentTimeMillis() < stopTime) {
            filterChangedFlag = false;
            ips = cdns.stream().flatMap(c -> c.ips.stream()).collect(Collectors.toList());
            StringBuilder filterBuilder = new StringBuilder();
            filterBuilder.append("dst ");
            filterBuilder.append(myAddr);
            filterBuilder.append(" or src ");
            filterBuilder.append(myAddr);


            for (String ip : ips) {
                filterBuilder.append(" or src ");
                filterBuilder.append(ip);

            }

            for (UrlData c : cdns) {
                c.ips.forEach(ip -> ipMap.put(ip, c));
            }
            logger.debug("filter: {}", filterBuilder.toString());

            handle.setFilter(filterBuilder.toString(), BpfProgram.BpfCompileMode.OPTIMIZE);
            while (!filterChangedFlag) {
                if (System.currentTimeMillis() > stopTime) {
                    logger.debug("closing");
                    break;
                }

                Packet packet = handle.getNextPacket();
                if (packet == null) continue;
                IpV4Packet ipv4packet = packet.get(IpV4Packet.class);
                if (ipv4packet == null) continue;
                TcpPacket tcpPacket = ipv4packet.get(TcpPacket.class);

                if (tcpPacket == null) {
                    UdpPacket udpPacket = ipv4packet.get(UdpPacket.class);
                    if (udpPacket != null) {
                        DnsPacket dnsPacket = udpPacket.get(DnsPacket.class);
                        if (dnsPacket != null) {
                            filterChangedFlag = parseDNS(dnsPacket);
                        }
                    }
                    continue;
                }
                UrlData urlData = ipMap.get(ipv4packet.getHeader().getSrcAddr().getHostAddress());
                if (urlData == null) {
                    urlData = ipMap.get(ipv4packet.getHeader().getDstAddr().getHostAddress());

                    if (urlData != null) {

                        if (urlData.lastACKFlag) {
                            if (System.currentTimeMillis() - urlData.lastACKTime >= sessionBreakTime) {
                                urlData.currentSession = new LinkedList<>();
                                urlData.sessions.add(urlData.currentSession);
                                logger.debug("new session for {}", urlData.url.getAddress());
                                urlData.lastACKFlag = false;

                            }
                        } else if (tcpPacket.getHeader().getAck() && ipv4packet.getHeader().getSrcAddr().getHostAddress().equals(myAddr)) {
                            urlData.lastACKFlag = true;
                            urlData.lastACKTime = System.currentTimeMillis();
                        }
                    }
                    continue;
                }
                urlData.currentSession.add(Pair.of(handle.getTimestamp(), packet.length()));
                //            logger.debug(ipv4packet.getHeader().getSrcAddr().getHostAddress());
                urlData.lastACKFlag = false;
            }
        }
        handle.close();

    }


    private void calcOutput(List<UrlData> cdns) {

        for (UrlData c : cdns) {
            double timeSum = 0;
            double byteSum = 0;
            for (List<Pair<Timestamp, Integer>> s : c.sessions) {
                if (s.size() <= 1) continue;
                double timeDelta = s.get(s.size() - 1).getFirst().getTime() - s.get(0).getFirst().getTime();
                timeSum += timeDelta / 1000.0;
                double bytes = s.stream()
                        .map(Pair::getSecond)
                        .reduce(0, Integer::sum);
                byteSum += bytes;

            }
            if (timeSum == 0) {
                c.throughput = 0;
            } else {
                c.throughput = byteSum * 8 / timeSum;
            }
            logger.info("URL: address: {} ; time sum: {} ; bit sum: {}; throughput: {}; sessions {}", c.url.getAddress(), timeSum, byteSum * 8, c.throughput, c.sessions.size());
        }


    }

    private static class UrlData {
        public Url url;
        public List<List<Pair<Timestamp, Integer>>> sessions;
        public List<String> ips;
        public Set<String> ipsSet;
        public List<Pair<Timestamp, Integer>> currentSession;
        public boolean lastACKFlag = false;
        public long lastACKTime = 0;
        public double throughput;

        UrlData() {
            ips = new LinkedList<>();
            sessions = new LinkedList<>();
            currentSession = new LinkedList<>();
            sessions.add(currentSession);
            ipsSet = new HashSet<>();
        }

        public boolean tryAdd(String ip) {
            if (ipsSet.contains(ip)) {
                return false;
            }
            ipsSet.add(ip);
            ips.add(ip);
            return true;
        }
    }
}
