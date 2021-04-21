package pl.edu.agh.cqm.configuration;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@ToString
@Component
public class CqmConfiguration {
    public enum ActiveTestType {
        TCP, ICMP;
    }

    @Value("${cqm.active.tests_type}")
    private ActiveTestType activeTestsType;

    @Value("${cqm.pcap_max_packet_length}")
    private int pcapMaxPacketLength;

    @Value("${cqm.pcap_timeout}")
    private int pcapTimeout;

    @Value("${cqm.pcap_session_break}")
    private int pcapSessionBreak; // in ms

    @Value("${cqm.interface_name}")
    private String interfaceName;

}
