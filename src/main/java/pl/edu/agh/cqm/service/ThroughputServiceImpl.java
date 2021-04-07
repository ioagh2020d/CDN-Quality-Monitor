package pl.edu.agh.cqm.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.UdpPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ThroughputServiceImpl implements ThroughputService {

    private final PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS;
    private final ThroughputSampleRepository dataRepository;
    private Logger logger = LogManager.getLogger(ThroughputServiceImpl.class);
    private final int snapLen;
    private final int timeout;
    private final int measurementTime;
    private final int sessionBreakTime;
    private final List<String> hostsToLookFor;
    private final String myIP;
    private final List<CDNsData> cdns;
    private  PcapNetworkInterface nif;

    public ThroughputServiceImpl(@Autowired CqmConfiguration configuration, @Autowired ThroughputSampleRepository dataRepository) throws UnknownHostException, PcapNativeException {
        this.dataRepository = dataRepository;
        snapLen = configuration.getPcapMaxPacketLength();
        timeout = configuration.getPcapTimeout();
        measurementTime = 1000 * 30 * configuration.getPassiveSamplingRate();
        sessionBreakTime = configuration.getPcapSessionBreak();
        hostsToLookFor = configuration.getCdns();
        myIP = configuration.getCardIPAddress();
        cdns = new ArrayList<>(hostsToLookFor.size());

        for (int i = 0; i < hostsToLookFor.size(); i++) {
            CDNsData cdn = new CDNsData();
            cdn.name = hostsToLookFor.get(i);
            cdns.add(cdn);
        }

        getNIF();

    }


    private void getNIF() throws UnknownHostException, PcapNativeException {

        this.nif = Pcaps.getDevByAddress(InetAddress.getByName(myIP));
    }

    public void doMeasurement() {
        try {
            logger.info("looking for dns");
            this.findDns(cdns, myIP);
            logger.info("all dns found");
            this.measureThroughput(cdns, myIP);
            logger.info("measurement done");
            this.calcOutput(cdns);
            cdns.forEach(c -> {
                try {
                    ThroughputSample sample = new ThroughputSample();
                    sample.setThroughput((long) c.throughput);
                    sample.setTimestamp(c.sessions.get(0).get(0).getFirst().toInstant());
                    sample.setAddress(c.name);
                    dataRepository.save(sample);
                } catch (NullPointerException e) {
                    logger.warn("empty throughput session");
                } catch (IndexOutOfBoundsException e) {
                    logger.warn("empty throughput session");
                }

            });

        } catch (PcapNativeException e) {
            logger.error("PCAP native exception");
        } catch (NotOpenException e) {
            logger.error("Internet card not opened");
        }
    }


    private void findDns(List<CDNsData> cdns, String myIP) throws PcapNativeException, NotOpenException {

        PcapHandle handleDNS = nif.openLive(snapLen, mode, timeout);

        handleDNS.setFilter("dst " + myIP + " and udp", BpfProgram.BpfCompileMode.OPTIMIZE);
        int fillingCounter = 0;
        while (true) {
            Packet packet = handleDNS.getNextPacket();
            if (packet == null) continue;
            DnsPacket dnsPacket = packet.get(DnsPacket.class);
            if (dnsPacket == null) continue;
            if (!dnsPacket.getHeader().isResponse()) {
                continue;
            }
            logger.debug("found dns response");
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
                    continue;
                }
                fillingCounter++;
                cdn.ips = dnsRecords.stream().map(r -> ((DnsRDataA) r.getRData()).getAddress().getHostAddress()).collect(Collectors.toList());
                logger.debug("found dns response for cdn {} : {}", cdn.name, cdn.ips.get(0));
            }
            if (fillingCounter == cdns.size()) break;

        }
        handleDNS.close();

    }

    private void measureThroughput(List<CDNsData> cdns, String myAddr) throws PcapNativeException, NotOpenException {

        List<String> ips = cdns.stream().flatMap(c -> c.ips.stream()).collect(Collectors.toList());
        Map<String, CDNsData> ipMap = new HashMap<>();
        for (CDNsData c : cdns) {
            c.ips.forEach(ip -> ipMap.put(ip, c));
        }

        ips.add(myAddr);

        PcapHandle handle = nif.openLive(snapLen, mode, timeout);
        StringBuilder filterBuilder = new StringBuilder("src ");
        filterBuilder.append(ips.get(0));
        if (ips.size() > 1) {
            for (String ip : ips.subList(1, ips.size())) {
                filterBuilder.append(" or src ");
                filterBuilder.append(ip);

            }
        }
        logger.debug(filterBuilder.toString());
        handle.setFilter(filterBuilder.toString(), BpfProgram.BpfCompileMode.OPTIMIZE);

        long startTime = System.currentTimeMillis();
        long stopTime = startTime + measurementTime;

        cdns.forEach(c -> {
            c.sessions = new LinkedList<>();
            c.currentSession = new LinkedList<>();
            c.sessions.add(c.currentSession);
        });

        while (true) {
            if (System.currentTimeMillis() > stopTime) {
                logger.debug("closing");
                break;
            }

            Packet packet = handle.getNextPacket();
            if (packet == null) continue;
            IpV4Packet ipv4packet = packet.get(IpV4Packet.class);
            if (ipv4packet == null) continue;
            TcpPacket tcpPacket = ipv4packet.get(TcpPacket.class);
            if (tcpPacket == null) continue;
            CDNsData cdn = ipMap.get(ipv4packet.getHeader().getSrcAddr().getHostAddress());
            if (cdn == null) {
                logger.debug("cdn not found in map");
                continue;
            }

            if (cdn.lastACKFlag) {
                if (System.currentTimeMillis() - cdn.lastACKTime >= sessionBreakTime) {
                    cdn.currentSession = new LinkedList<>();
                    cdn.sessions.add(cdn.currentSession);
                    logger.debug("new session for {}", cdn.name);
                }
            }

            cdn.currentSession.add(Pair.of(handle.getTimestamp(), packet.length()));
            logger.debug(ipv4packet.getHeader().getSrcAddr().getHostAddress());

            if (tcpPacket.getHeader().getAck() && ipv4packet.getHeader().getSrcAddr().getHostAddress().equals(myAddr)) {
                cdn.lastACKFlag = tcpPacket.getHeader().getAck();
                cdn.lastACKTime = System.currentTimeMillis();
            } else {
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
                double throughput = (bytes / timeDelta);

            }
            c.throughput = byteSum / timeSum;
            logger.info("CDN: name: {} ; time sum: {} ; byte sum: {}; throughput: {}", c.name, timeSum, byteSum, c.throughput);
        }


    }

    private class CDNsData {
        public String name;
        public List<List<Pair<Timestamp, Integer>>> sessions;
        public List<String> ips;
        public List<Pair<Timestamp, Integer>> currentSession;
        public boolean lastACKFlag = false;
        public long lastACKTime = 0;
        public double throughput;

        CDNsData() {
            ips = new LinkedList<>();
        }
    }
}
