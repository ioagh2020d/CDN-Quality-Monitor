package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface MonitoringService {

    long DEFAULT_GRANULARITY = 1000 * 60 * 10;

    Map<String, List<RTTSampleDTO>> getRTTSamples(Instant startDate, Instant endDate, Long granularity,
                                                  String monitor);

    Map<String, List<RTTSampleDTO>> getRTTSamplesSingleCdn(String cdn, Instant startDate,
                                                           Instant endDate, Long granularity);

    Map<String, List<RTTSampleDTO>> getRTTSamplesMonitorComp(String cdn, Instant startDate,
                                                             Instant endDate, Long granularity);

    Map<String, List<ThroughputSampleDTO>> getThroughputSamples(Instant startDate, Instant endDate,
                                                                Long granularity, String monitor);

    Map<String, List<ThroughputSampleDTO>> getThroughputSamplesSingleCdn(String cdn, Instant startDate,
                                                                         Instant endDate, Long granularity);

    Map<String, List<ThroughputSampleDTO>> getThroughputSamplesMonitorComp(String cdn, Instant startDate,
                                                                         Instant endDate, Long granularity);

    boolean checkRttSamplesExist(Instant startDate, Instant endDate, String monitor);

    boolean checkThroughputSamplesExist(Instant startDate, Instant endDate, String monitor);
}
