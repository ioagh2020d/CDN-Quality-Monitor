package pl.edu.agh.cqm.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.model.ConfigCdn;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ThroughputServiceImpl implements ThroughputService {

    private final PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS;
    private final ThroughputSampleRepository dataRepository;
    private final Logger logger;
    private final int snapLen;
    private final int timeout;
    private final ParameterService parameterService;
    private int measurementTime;
    private final int sessionBreakTime;
    private final String interfaceName;
    private String myIP;
    private PcapNetworkInterface nif;
    private List<CDNsData> cdns;

    public ThroughputServiceImpl(CqmConfiguration configuration,ParameterService parameterService, ThroughputSampleRepository dataRepository) throws PcapNativeException {
        logger = LogManager.getLogger("ThroughputServiceImpl");
        this.dataRepository = dataRepository;
        this.parameterService = parameterService;
        snapLen = configuration.getPcapMaxPacketLength();
        timeout = configuration.getPcapTimeout();
        measurementTime = 1000 * 60 * parameterService.getPassiveSamplingRate();
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
        cdns = new ArrayList<>(parameterService.getCdns().size());
        for (String cdn : parameterService.getCdns()) {
            CDNsData cdnsData = new CDNsData();
            cdnsData.name = cdn;
            cdns.add(cdnsData);
        }
        logger.debug("throughput doMeasurement start");
        try {
            this.measureThroughput(cdns, myIP);
            logger.info("measurement done");
            this.calcOutput(cdns);
            cdns.forEach(c -> {
                try {
                    ThroughputSample sample = new ThroughputSample();
                    sample.setThroughput((long) c.throughput);
                    sample.setTimestamp(Instant.now());
                    sample.setAddress(c.name);
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
        for (CDNsData cdn : cdns) {
            if (!dnsPacket.getHeader().getQuestions().get(0).getQName().toString().contains(cdn.name)) {
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
                    .forEach(cdn::tryAdd);
            logger.debug("found dns response for cdn {} : {} : {}", cdn.name, dnsPacket.getHeader().getQuestions().get(0).getQName().toString(), cdn.ips.get(0));
            return true;

        }
        return false;
    }

    private void measureThroughput(List<CDNsData> cdns, String myAddr) throws PcapNativeException, NotOpenException {

        List<String> ips;
        Map<String, CDNsData> ipMap = new HashMap<>();


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

            for (CDNsData c : cdns) {
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
                CDNsData cdn = ipMap.get(ipv4packet.getHeader().getSrcAddr().getHostAddress());
                if (cdn == null) {
                    cdn = ipMap.get(ipv4packet.getHeader().getDstAddr().getHostAddress());

                    if (cdn != null) {

                        if (cdn.lastACKFlag) {
                            if (System.currentTimeMillis() - cdn.lastACKTime >= sessionBreakTime) {
                                cdn.currentSession = new LinkedList<>();
                                cdn.sessions.add(cdn.currentSession);
                                logger.debug("new session for {}", cdn.name);
                                cdn.lastACKFlag = false;

                            }
                        } else if (tcpPacket.getHeader().getAck() && ipv4packet.getHeader().getSrcAddr().getHostAddress().equals(myAddr)) {
                            cdn.lastACKFlag = true;
                            cdn.lastACKTime = System.currentTimeMillis();
                        }
                    }
                    continue;
                }
                cdn.currentSession.add(Pair.of(handle.getTimestamp(), packet.length()));
                //            logger.debug(ipv4packet.getHeader().getSrcAddr().getHostAddress());
                cdn.lastACKFlag = false;
            }
        }
        handle.close();

    }


    private void calcOutput(List<CDNsData> cdns) {

        for (CDNsData c : cdns) {
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
            logger.info("CDN: name: {} ; time sum: {} ; bit sum: {}; throughput: {}; sessions {}", c.name, timeSum, byteSum * 8, c.throughput, c.sessions.size());
        }


    }

    private static class CDNsData {
        public String name;
        public List<List<Pair<Timestamp, Integer>>> sessions;
        public List<String> ips;
        public Set<String> ipsSet;
        public List<Pair<Timestamp, Integer>> currentSession;
        public boolean lastACKFlag = false;
        public long lastACKTime = 0;
        public double throughput;

        CDNsData() {
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
