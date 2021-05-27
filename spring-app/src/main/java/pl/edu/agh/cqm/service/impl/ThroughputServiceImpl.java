package pl.edu.agh.cqm.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.dto.CdnWithUrlsDTO;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.model.Url;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;
import pl.edu.agh.cqm.service.MonitorService;
import pl.edu.agh.cqm.service.ParameterService;
import pl.edu.agh.cqm.service.ThroughputService;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private List<URLData> urlsData;
    private Set<String> monitoredIPs;
    private List<String> cdns;

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
        monitoredIPs = new HashSet<>();
        measurementTime = 1000 * 60 * parameterService.getPassiveSamplingRate();
        cdns = parameterService.getActiveCdnsWithUrls().stream().map(c -> c.getName()).collect(Collectors.toList());

        urlsData = new LinkedList<>();
        for (CdnWithUrlsDTO cdn : parameterService.getActiveCdnsWithUrls()) {
            for(String urlName : cdn.getUrls()){
                URLData urlData = new URLData();
                urlData.name = urlName;
                urlData.cdn = cdn.getName();
                urlsData.add(urlData);
            }

        }
        logger.debug("throughput doMeasurement start..");
        try {
            this.measureThroughput(urlsData);
            logger.info("measurement done");
            this.calcOutput(urlsData);
            urlsData.forEach(c -> {
                logger.debug("foreach cdn:url:ip:tput {}:{}:{}:{}", c.cdn, c.name, c.ip, c.throughput);

                if(c.ip == null){
                    return;
                }

                try {


                    if(c.name.equals("")){
                        InetAddress ia = InetAddress.getByName(c.ip);
                        c.name = ia.getCanonicalHostName();
                        if(c.name.equals("")){
                            return;
                        }
                    }
                    Optional<Url> url = parameterService.getURL(c.cdn, c.name);

                    if(url.isEmpty()){
                        logger.debug("adding new URL cdn:url:ip {}:{}:{}", c.cdn, c.name, c.ip);
                        parameterService.addNewUrl(c.cdn, c.name);
                        url = parameterService.getURL(c.cdn, c.name);
                    }

                    ThroughputSample sample = ThroughputSample.builder()
                        .throughput((long) c.throughput)
                        .timestamp(Instant.now())
                        .url(url.get())
                        .monitor(monitorService.getLocalMonitor())
                        .build();

                    logger.debug("add sample cdn:url:ip:tput {}:{}:{}:{}", c.cdn, c.name, c.ip, c.throughput);
                    dataRepository.save(sample);
                } catch (NullPointerException e) {
                    logger.warn("empty throughput session");
                } catch (IndexOutOfBoundsException e) {
                    logger.warn("empty throughput session (index)");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
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
        for(URLData url: urlsData){
            if (!dnsPacket.getHeader().getQuestions().get(0).getQName().toString().equals(url.name)) {
                continue;
            }

            List<DnsResourceRecord> dnsRecords = new ArrayList<>(dnsPacket
                    .getHeader()
                    .getAnswers());
            List<DnsRDataA> dnsARecords = dnsRecords.stream()
                    .filter(r -> r.getDataType().valueAsString().equals("1"))
                    .map(r -> ((DnsRDataA) r.getRData())).collect(Collectors.toList());
            if(dnsARecords.size() == 0){
                return false;
            }
            String ip = dnsARecords.get(0).getAddress().getHostAddress();
            if(monitoredIPs.contains(ip)){
                return false;
            }
            monitoredIPs.add(url.ip);
            url.ip = dnsARecords.get(0).getAddress().getHostAddress();

            logger.debug("found URL dns response cdn:{};  query:{};  ip:{}; url:{};", url.cdn, dnsPacket.getHeader().getQuestions().get(0).getQName().toString(), url.ip, url.name);

            return true;
        }
        for (String cdn : cdns) {

                if (!dnsPacket.getHeader().getQuestions().get(0).getQName().toString().contains(cdn)) {
                    continue;
                }
                List<DnsResourceRecord> dnsRecords = new ArrayList<>(dnsPacket
                        .getHeader()
                        .getAnswers());
                if (dnsRecords.size() == 0) {
                    return false;
                }
                List<DnsRDataA> dnsARecords = dnsRecords.stream()
                        .filter(r -> r.getDataType().valueAsString().equals("1"))
                        .map(r -> ((DnsRDataA) r.getRData())).collect(Collectors.toList());

                if(dnsARecords.size() == 0){
                    logger.debug("DNS A RECORDS 0");
                    return false;
                }
                for (DnsRDataA record : dnsARecords) {
                    if(monitoredIPs.contains(record.getAddress().getHostAddress())) return false;

                    URLData urlData = new URLData();
                    urlData.ip = record.getAddress().getHostAddress();
                    urlData.cdn = cdn;

                    urlsData.add(urlData);
                    monitoredIPs.add(urlData.ip);
                    logger.debug("found CDN dns response cdn:{};  query:{};  ip:{}; url:{};", cdn, dnsPacket.getHeader().getQuestions().get(0).getQName().toString(), urlData.ip, urlData.name);
                }
                return true;
        }


        return false;
    }

    private void measureThroughput(List<URLData> urls) throws PcapNativeException, NotOpenException {

        List<String> ips;
        Map<String, URLData> ipMap = new HashMap<>();


        PcapHandle handle = nif.openLive(snapLen, mode, timeout);

        long startTime = System.currentTimeMillis();
        long stopTime = startTime + measurementTime;
        boolean filterChangedFlag;

        logger.debug("starttime:{} stoptime:{}", startTime, stopTime);
        while (System.currentTimeMillis() < stopTime) {
            filterChangedFlag = false;
            ips = urlsData.stream().filter(u -> u.ip != null).map(u -> u.ip).collect(Collectors.toList());
            StringBuilder filterBuilder = new StringBuilder();
            filterBuilder.append("dst ");
            filterBuilder.append(myIP);
            filterBuilder.append(" or src ");
            filterBuilder.append(myIP);


            for (String ip : ips) {
                filterBuilder.append(" or src ");
                filterBuilder.append(ip);

            }

            urls.stream().filter(d -> d.ip != null).forEach(d -> ipMap.put(d.ip, d));
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
                URLData url = ipMap.get(ipv4packet.getHeader().getSrcAddr().getHostAddress());
                if (url == null) {
                    url = ipMap.get(ipv4packet.getHeader().getDstAddr().getHostAddress());

                    if (url != null) {

                        if (url.lastACKFlag) {
                            if (System.currentTimeMillis() - url.lastACKTime >= sessionBreakTime) {
                                url.currentSession = new LinkedList<>();
                                url.sessions.add(url.currentSession);
                                logger.debug("new session for {}", url.name);
                                url.lastACKFlag = false;

                            }
                        } else if (tcpPacket.getHeader().getAck() && ipv4packet.getHeader().getSrcAddr().getHostAddress().equals(myIP)) {
                            url.lastACKFlag = true;
                            url.lastACKTime = System.currentTimeMillis();
                        }
                    }
                    continue;
                }
                url.currentSession.add(Pair.of(handle.getTimestamp(), packet.length()));
//                logger.debug("packet {}:{}", url.cdn, url.ip);
                url.lastACKFlag = false;
            }
        }
        handle.close();

    }


    private void calcOutput(List<URLData> urls) {

        for (URLData u : urls) {
            double timeSum = 0;
            double byteSum = 0;
            for (List<Pair<Timestamp, Integer>> s : u.sessions) {
                if (s.size() <= 1) continue;
                double timeDelta = s.get(s.size() - 1).getFirst().getTime() - s.get(0).getFirst().getTime();
                timeSum += timeDelta / 1000.0;
                double bytes = s.stream()
                        .map(Pair::getSecond)
                        .reduce(0, Integer::sum);
                byteSum += bytes;

            }
            if (timeSum == 0) {
                u.throughput = 0;
            } else {
                u.throughput = byteSum * 8 / timeSum;
            }
            logger.info("URL: name: {}; ip: {}; time sum: {}; bit sum: {}; throughput: {} kb/s; sessions {}", u.name, u.ip, timeSum, byteSum * 8, u.throughput/1000.0, u.sessions.size());
        }


    }

    private static class URLData {
        public String name = "";
        public List<List<Pair<Timestamp, Integer>>> sessions;
        public String ip;
        //        public Set<String> ipsSet;
        public List<Pair<Timestamp, Integer>> currentSession;
        public boolean lastACKFlag = false;
        public long lastACKTime = 0;
        public double throughput;
        public String cdn;

        URLData() {
//            ips = new LinkedList<>();
            sessions = new LinkedList<>();
            currentSession = new LinkedList<>();
            sessions.add(currentSession);
//            ipsSet = new HashSet<>();
        }

    }
}
