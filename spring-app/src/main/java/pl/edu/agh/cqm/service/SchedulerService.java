package pl.edu.agh.cqm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

@Service
@Configuration
@RequiredArgsConstructor
public class SchedulerService {
    private final PingService pingService;
    private final ThroughputService throughputService;
    private final CqmConfiguration cqmConfiguration;

    private int schedulePingCounter = 0;

    @Scheduled(fixedDelay = 1)
    public void scheduledThroughput(){
        throughputService.doMeasurement();
    }

    @Scheduled(fixedDelay = 60000)
    public void schedulePing() {
        if (schedulePingCounter == 0) {
            pingService.doMeasurement();
            schedulePingCounter = cqmConfiguration.getActiveSamplingRate();
        }
        schedulePingCounter--;
    }
}