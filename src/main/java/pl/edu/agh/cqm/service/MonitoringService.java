package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.ThroughputSample;

import java.time.Instant;
import java.util.List;

public interface MonitoringService {

    List<RTTSample> getRTTSamples(Instant startTime, Instant endTime);

    List<ThroughputSample> getThroughputSamples(Instant startTime, Instant endTime);
}
