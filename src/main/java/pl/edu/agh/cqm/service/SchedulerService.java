package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

@Service
@Configuration
@AllArgsConstructor
public class SchedulerService {
    private final PingService pingService;
    private final ThroughputService throughputService;


    @Scheduled(fixedDelay = 1)
    public void scheduledThroughput(){
        throughputService.doMeasurement();
    }


    @Scheduled(fixedRateString = "#{cqmConfiguration.getActiveSamplingRate() * 60 * 1000}")
    public void scheduledPing(){
        pingService.addRTTSample();
    }

}
