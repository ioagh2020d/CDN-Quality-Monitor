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


    @Scheduled(fixedRate = 1)
    public void scheduledThroughput(){
        throughputService.doMeasurement();
    }
    @Scheduled(fixedRate = 1)
    public void scheduledPing(){

        pingService.addRTTSample();
    }

}
