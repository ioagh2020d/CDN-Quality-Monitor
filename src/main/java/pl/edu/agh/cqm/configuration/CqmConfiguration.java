package pl.edu.agh.cqm.configuration;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@ToString
@Component
public class CqmConfiguration {

    @Value("${cqm.cdns}")
    private List<String> cdns;

    @Value("${cqm.active.sampling_rate}")
    private int activeSamplingRate;

    @Value("${cqm.active.tests_intensity}")
    private int activeTestsIntensity;

    @Value("${cqm.passive.sampling_rate}")
    private int passiveSamplingRate;

    @Value("65536")
    private int pcapMaxPacketLength;

    @Value("10")
    private int pcapTimeout;

    @Value("1000")
    private int pcapSessionBreak; // in ms

    @Value("")
    private  String cardIPAddress;

}
