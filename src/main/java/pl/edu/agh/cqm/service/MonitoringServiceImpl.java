package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.repository.RTTSampleRepository;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    private final RTTSampleRepository rttSampleRepository;
    private final ThroughputSampleRepository throughputSampleRepository;

    @Override
    public List<RTTSample> getRTTSamples(Instant startTime, Instant endTime) {
        return rttSampleRepository.findAllByTimestampBetween(startTime, endTime);
    }

    @Override
    public List<ThroughputSample> getThroughputSamples(Instant startTime, Instant endTime) {
        return throughputSampleRepository.findAllByTimestampBetween(startTime, endTime);
    }
}
