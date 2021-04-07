package pl.agh.edu.pcap;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.UdpPort;
import org.springframework.data.util.Pair;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ThroughputService {
    private PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.NONPROMISCUOUS;
    private int snapLen = 65536; //TODO injection
    private int timeout = 10; // TODO injection
    private int measurementTime = 1000*60*1;
    private int sessionBreakTime = 1000;
    private String hostToLookFor = "youtube.com";
    private String myIP = "192.168.1.136";
    private PcapNetworkInterface nif;

    public ThroughputService() throws UnknownHostException, PcapNativeException {
        openNIF();
    }
    public ThroughputService(String ipAddr, String host) throws UnknownHostException, PcapNativeException {
        this.myIP = ipAddr;
        this.hostToLookFor = host;
        openNIF();
    }
    private void openNIF() throws UnknownHostException, PcapNativeException {
        this.nif = Pcaps.getDevByAddress(InetAddress.getByName(myIP));
    }

    public void doMeasurement() throws NotOpenException, PcapNativeException {
        List<String> cdnIPs  = this.findDns(hostToLookFor, myIP);
        List<List<Pair<Timestamp, Integer>>> data = this.measureThroughput(cdnIPs, myIP);
        double value = this.calcOutput(data);
        System.out.printf("value: %f kB/s", value/1000);
    }

    private void myLog(String msg){
//        System.out.println(msg);
    }
    private  List<String> findDns(String hostToLookFor, String myIP) throws NotOpenException, PcapNativeException {

        PcapHandle handleDNS = nif.openLive(snapLen, mode, timeout);
        handleDNS.setFilter("udp and port 53", BpfProgram.BpfCompileMode.OPTIMIZE);
        UdpPort srcUdpPort;
        DnsDomainName qName;
        while (true) {
            Packet packet = handleDNS.getNextPacket();
            if(packet == null){
                continue;
            }
            DnsPacket dnsPacket = packet.get(DnsPacket.class);
            DnsDomainName dnsQueryName = dnsPacket.getHeader().getQuestions().get(0).getQName();

            myLog("got dns query for " + dnsQueryName);
            if (dnsQueryName.toString().contains(hostToLookFor)) {
                myLog("found");
                qName = dnsQueryName;
                UdpPacket udpPacket = packet.get(UdpPacket.class);
                srcUdpPort = udpPacket.getHeader().getSrcPort();
                break;
            }
        }

        myLog("looking for a response to " + srcUdpPort.valueAsString());
        handleDNS.setFilter("dst " + myIP + " and udp and port " + srcUdpPort.valueAsString(), BpfProgram.BpfCompileMode.OPTIMIZE);
        List<String> foundIpAddrs;
        while (true) {
            Packet packet = handleDNS.getNextPacket();
            if (packet == null) continue;
            DnsPacket dnsPacket = packet.get(DnsPacket.class);
            if(!dnsPacket.getHeader().isResponse()){
                continue;
            }
            if (!dnsPacket.getHeader().getQuestions().get(0).getQName().toString().equals(qName.toString())) {
                continue;
            }
            List<DnsResourceRecord> dnsRecords = dnsPacket
                    .getHeader()
                    .getAnswers()
                    .stream()
                    .filter( r -> r.getDataType().valueAsString().equals("1"))
                    .collect(Collectors.toList());
            if(dnsRecords == null || dnsRecords.size() == 0){
                continue;
            }

            foundIpAddrs = dnsRecords.stream().map(r -> ((DnsRDataA)r.getRData()).getAddress().getHostAddress()).collect(Collectors.toList());
            myLog("got   " + foundIpAddrs.size() + "dns answers");
            break;

        }
        handleDNS.close();

        return  foundIpAddrs;
    }

    private List<List<Pair<Timestamp, Integer>>> measureThroughput(List<String> ipAddrs, String myAddr) throws PcapNativeException, NotOpenException {

        ipAddrs.add(myAddr);
        PcapHandle handle = nif.openLive(snapLen, mode, timeout);

        StringBuilder filterBuilder = new StringBuilder("src ");
        filterBuilder.append(ipAddrs.get(0));
        if(ipAddrs.size() > 1){
            for(String ip : ipAddrs.subList(1, ipAddrs.size())){
                filterBuilder.append(" or src ");
                filterBuilder.append(ip);

            }
        }
        myLog(filterBuilder.toString());
        handle.setFilter(filterBuilder.toString(), BpfProgram.BpfCompileMode.OPTIMIZE);

        long startTime = System.currentTimeMillis();
        long stopTime = startTime + measurementTime;

        List<List<Pair<Timestamp, Integer>>> sessions = new LinkedList<>();
        List<Pair<Timestamp, Integer>> currentSession = new LinkedList<>();
        sessions.add(currentSession);

        boolean lastACKFlag = false;
        long lastACKTime = 0;
        while(true){
            if (System.currentTimeMillis() > stopTime) {
                myLog("closing");
                break;
            }

            Packet packet = handle.getNextPacket();
            if(packet == null) continue;
            IpV4Packet ipv4packet = packet.get(IpV4Packet.class);
            if(ipv4packet == null) continue;
            TcpPacket tcpPacket = ipv4packet.get(TcpPacket.class);
            if(tcpPacket == null) continue;

            if(lastACKFlag){
                if(System.currentTimeMillis() - lastACKTime >= sessionBreakTime) {
                    currentSession = new LinkedList<>();
                    sessions.add(currentSession);
                    myLog("----new session----");
                }
            }

            currentSession.add(Pair.of(handle.getTimestamp(), packet.length()));
            myLog(ipv4packet.getHeader().getSrcAddr().getHostAddress());

            if(tcpPacket.getHeader().getAck() && ipv4packet.getHeader().getSrcAddr().getHostAddress().equals(myAddr)){
                lastACKFlag = tcpPacket.getHeader().getAck();
                lastACKTime = System.currentTimeMillis();
                myLog("ACK");
                myLog(" src: " + ipv4packet.getHeader().getSrcAddr());
                myLog(" len: " + packet.length());
            }else{
                lastACKFlag = false;
            }



        }
        handle.close();

        return sessions;
    }


    private double calcOutput(List<List<Pair<Timestamp, Integer>>> data){

        double timeSum = 0;
        double byteSum = 0;

        for(List<Pair<Timestamp, Integer>> s : data){
            if(s.size() == 1) continue;
            double timeDelta  = s.get(s.size()-1).getFirst().getTime() - s.get(0).getFirst().getTime();
            timeSum += timeDelta/1000.0;
            double bytes = s.stream().map(Pair::getSecond).reduce(0, Integer::sum);
            byteSum += bytes;
            double throughput = (bytes/timeDelta);

            myLog("FIRST: " + s.get(0).toString());
            myLog("LAST: " + s.get(s.size()-1).toString());
            myLog(String.format("TIME: %f VALUE: %f THROUGHPUT:%f", timeDelta, bytes, throughput));
            myLog("");
        }

        return byteSum/timeSum;
    }
}
