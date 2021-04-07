package pl.edu.agh.cqm.service;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.UdpPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ThroughputServiceImpl implements ThroughputService {
    private final PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS;
    Logger logger = LoggerFactory.getLogger(ThroughputServiceImpl.class);
    private final int snapLen;
    private final int timeout;
    private final int measurementTime;
    private final int sessionBreakTime;
    private final List<String> hostToLookFor;
    private final String myIP;
    private PcapNetworkInterface nif;

    public ThroughputServiceImpl(CqmConfiguration configuration) throws UnknownHostException, PcapNativeException {
        snapLen = configuration.getPcapMaxPacketLength();
        timeout = configuration.getPcapTimeout();
        measurementTime = 1000*60 * configuration.getPassiveSamplingRate();
        sessionBreakTime = configuration.getPcapSessionBreak();
        hostToLookFor = configuration.getCdns();
        myIP = configuration.getCardIPAddress();
        getNIF();
    }


    private void getNIF() throws UnknownHostException, PcapNativeException {
        this.nif = Pcaps.getDevByAddress(InetAddress.getByName(myIP));
    }

    public void doMeasurement() {
        try {
            List<String> cdnIPs = this.findDns(hostToLookFor.get(0), myIP);
            List<List<Pair<Timestamp, Integer>>> data = this.measureThroughput(cdnIPs, myIP);
            double value = this.calcOutput(data);
        } catch (PcapNativeException e) {
            logger.error("PCAP native exception");
        } catch (NotOpenException e) {
            logger.error("Internet card not opened");
        }
    }



    private List<String> findDns(String hostToLookFor, String myIP) throws PcapNativeException, NotOpenException {

        PcapHandle handleDNS = nif.openLive(snapLen, mode, timeout);
        handleDNS.setFilter("udp and port 53", BpfProgram.BpfCompileMode.OPTIMIZE);
        UdpPort srcUdpPort;
        DnsDomainName qName;
        while (true) {
            Packet packet = handleDNS.getNextPacket();
            if (packet == null) {
                continue;
            }
            DnsPacket dnsPacket = packet.get(DnsPacket.class);
            DnsDomainName dnsQueryName = dnsPacket.getHeader().getQuestions().get(0).getQName();

            logger.debug("got dns query for " + dnsQueryName);
            if (dnsQueryName.toString().contains(hostToLookFor)) {
                logger.debug("found");
                qName = dnsQueryName;
                UdpPacket udpPacket = packet.get(UdpPacket.class);
                srcUdpPort = udpPacket.getHeader().getSrcPort();
                break;
            }
        }

        logger.debug("looking for a response to " + srcUdpPort.valueAsString());
        handleDNS.setFilter("dst " + myIP + " and udp and port " + srcUdpPort.valueAsString(), BpfProgram.BpfCompileMode.OPTIMIZE);
        List<String> foundIpAddrs;
        while (true) {
            Packet packet = handleDNS.getNextPacket();
            if (packet == null) continue;
            DnsPacket dnsPacket = packet.get(DnsPacket.class);
            if (!dnsPacket.getHeader().isResponse()) {
                continue;
            }
            if (!dnsPacket.getHeader().getQuestions().get(0).getQName().toString().equals(qName.toString())) {
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

            foundIpAddrs = dnsRecords.stream().map(r -> ((DnsRDataA) r.getRData()).getAddress().getHostAddress()).collect(Collectors.toList());
            logger.debug("got   " + foundIpAddrs.size() + "dns answers");
            break;

        }
        handleDNS.close();

        return foundIpAddrs;
    }

    private List<List<Pair<Timestamp, Integer>>> measureThroughput(List<String> ipAddrs, String myAddr) throws PcapNativeException, NotOpenException {

        ipAddrs.add(myAddr);
        PcapHandle handle = nif.openLive(snapLen, mode, timeout);

        StringBuilder filterBuilder = new StringBuilder("src ");
        filterBuilder.append(ipAddrs.get(0));
        if (ipAddrs.size() > 1) {
            for (String ip : ipAddrs.subList(1, ipAddrs.size())) {
                filterBuilder.append(" or src ");
                filterBuilder.append(ip);

            }
        }
        logger.debug(filterBuilder.toString());
        handle.setFilter(filterBuilder.toString(), BpfProgram.BpfCompileMode.OPTIMIZE);

        long startTime = System.currentTimeMillis();
        long stopTime = startTime + measurementTime;

        List<List<Pair<Timestamp, Integer>>> sessions = new LinkedList<>();
        List<Pair<Timestamp, Integer>> currentSession = new LinkedList<>();
        sessions.add(currentSession);

        boolean lastACKFlag = false;
        long lastACKTime = 0;
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

            if (lastACKFlag) {
                if (System.currentTimeMillis() - lastACKTime >= sessionBreakTime) {
                    currentSession = new LinkedList<>();
                    sessions.add(currentSession);
                    logger.debug("----new session----");
                }
            }

            currentSession.add(Pair.of(handle.getTimestamp(), packet.length()));
            logger.debug(ipv4packet.getHeader().getSrcAddr().getHostAddress());

            if (tcpPacket.getHeader().getAck() && ipv4packet.getHeader().getSrcAddr().getHostAddress().equals(myAddr)) {
                lastACKFlag = tcpPacket.getHeader().getAck();
                lastACKTime = System.currentTimeMillis();
                logger.debug("ACK");
                logger.debug(" src: " + ipv4packet.getHeader().getSrcAddr());
                logger.debug(" len: " + packet.length());
            } else {
                lastACKFlag = false;
            }


        }
        handle.close();

        return sessions;
    }


    private double calcOutput(List<List<Pair<Timestamp, Integer>>> data) {

        double timeSum = 0;
        double byteSum = 0;

        for (List<Pair<Timestamp, Integer>> s : data) {
            if (s.size() == 1) continue;
            double timeDelta = s.get(s.size() - 1).getFirst().getTime() - s.get(0).getFirst().getTime();
            timeSum += timeDelta / 1000.0;
            double bytes = s.stream().map(Pair::getSecond).reduce(0, Integer::sum);
            byteSum += bytes;
            double throughput = (bytes / timeDelta);

            logger.debug("FIRST: " + s.get(0).toString());
            logger.debug("LAST: " + s.get(s.size() - 1).toString());
            logger.debug(String.format("TIME: %f VALUE: %f THROUGHPUT:%f", timeDelta, bytes, throughput));
            logger.debug("");
        }

        return byteSum / timeSum;
    }
}
