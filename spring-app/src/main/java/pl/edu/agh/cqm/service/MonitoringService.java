package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface MonitoringService {

    long DEFAULT_GRANULARITY = 1000 * 60 * 10;

    Map<String, List<RTTSampleDTO>> getRTTSamples(Instant startDate, Instant endDate, Long granularity);

    Map<String, List<RTTSampleDTO>> getRTTSamples(String cdn, Instant startDate,
                                                  Instant endDate, Long granularity);

    Map<String, List<ThroughputSampleDTO>> getThroughputSamples(Instant startDate, Instant endDate,
                                                                Long granularity);

    Map<String, List<ThroughputSampleDTO>> getThroughputSamples(String cdn, Instant startDate,
                                                                Instant endDate, Long granularity);

    boolean checkRttSamplesExist(Instant startDate, Instant endDate);

    boolean checkRttSamplesExist(String cdn, Instant startDate, Instant endDate);

    boolean checkThroughputSamplesExist(Instant startDate, Instant endDate);

    boolean checkThroughputSamplesExist(String cdn, Instant startDate, Instant endDate);
}
