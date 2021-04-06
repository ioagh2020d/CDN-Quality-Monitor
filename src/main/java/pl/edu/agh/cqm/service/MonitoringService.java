package pl.edu.agh.cqm.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

public interface MonitoringService {

    Map<String,List<RTTSampleDTO>> getRTTSamples(Instant startTime, Instant endTime);

    Map<String, List<ThroughputSampleDTO>> getThroughputSamples(Instant startTime, Instant endTime);
}
