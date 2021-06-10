package pl.edu.agh.cqm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.Sample;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.repository.RTTSampleRepository;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;
import pl.edu.agh.cqm.service.MonitorService;
import pl.edu.agh.cqm.service.MonitoringService;
import pl.edu.agh.cqm.service.ParameterService;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    public static final String ALL_MONITORS = "all";

    private final RTTSampleRepository rttSampleRepository;
    private final ThroughputSampleRepository throughputSampleRepository;
    private final ParameterService parameterService;
    private final MonitorService monitorService;

    @Override
    public Map<String, List<RTTSampleDTO>> getRTTSamples(Instant startDate, Instant endDate, Long granularity,
                                                         String monitor) {
        return parameterService.getActiveCdns().stream()
            .map(cdn -> Pair.of(
                cdn,
                rttSampleRepository.findByCdnAndTimestampBetween(cdn, startDate, endDate).stream()
                    .filter(sample -> isAllMonitors(monitor) || sample.getMonitor().getName().equals(monitor))
                    .collect(Collectors.toList())))
            .map(p -> Pair.of(
                p.getFirst().getName(),
                groupRTT(p.getSecond(), granularity)))
            .filter(p -> !p.getSecond().isEmpty())
            .collect(Pair.toMap());
    }

    @Override
    public Map<String, List<RTTSampleDTO>> getRTTSamplesSingleCdn(String cdn, Instant startDate,
                                                                  Instant endDate, Long granularity, String monitor) {
        return parameterService.getActiveUrls(cdn).stream()
            .map(url -> Pair.of(
                url.getAddress(),
                rttSampleRepository.findAllByTimestampBetweenAndUrl(startDate, endDate, url).stream()
                    .filter(sample -> isAllMonitors(monitor) || sample.getMonitor().getName().equals(monitor))
                    .collect(Collectors.toList())))
            .map(p -> Pair.of(
                p.getFirst(),
                groupRTT(p.getSecond(), granularity)))
            .filter(p -> !p.getSecond().isEmpty())
            .collect(Pair.toMap());
    }

    @Override
    public Map<String, List<RTTSampleDTO>> getRTTSamplesMonitorComp(String cdn, Instant startDate,
                                                                    Instant endDate, Long granularity) {
        return monitorService.getActiveMonitors().stream()
            .map(monitor -> Pair.of(
                monitor.getName(),
                rttSampleRepository.findAllByTimestampBetweenAndMonitor(
                    startDate, endDate, monitor).stream()
                    .filter(sample -> sample.getUrl().getCdn().getName().equals(cdn))
                    .collect(Collectors.toList())))
            .map(p -> Pair.of(
                p.getFirst(),
                groupRTT(p.getSecond(), granularity)))
            .filter(p -> !p.getSecond().isEmpty())
            .collect(Pair.toMap());
    }

    @Override
    public Map<String, List<ThroughputSampleDTO>> getThroughputSamples(Instant startDate, Instant endDate,
                                                                       Long granularity, String monitor) {
        return parameterService.getActiveCdns().stream()
            .map(cdn -> Pair.of(
                cdn,
                throughputSampleRepository.findByCdnAndTimestampBetween(cdn, startDate, endDate).stream()
                    .filter(sample -> isAllMonitors(monitor) || sample.getMonitor().getName().equals(monitor))
                    .collect(Collectors.toList())))
            .map(p -> Pair.of(
                p.getFirst().getName(),
                groupThroughput(p.getSecond(), granularity)))
            .filter(p -> !p.getSecond().isEmpty())
            .collect(Pair.toMap());
    }

    @Override
    public Map<String, List<ThroughputSampleDTO>> getThroughputSamplesSingleCdn(String cdn, Instant startDate,
                                                                                Instant endDate, Long granularity,
                                                                                String monitor) {
        return parameterService.getActiveUrls(cdn).stream()
            .map(url -> Pair.of(
                url.getAddress(),
                throughputSampleRepository.findAllByTimestampBetweenAndUrl(startDate, endDate, url).stream()
                    .filter(sample -> isAllMonitors(monitor) || sample.getMonitor().getName().equals(monitor))
                    .collect(Collectors.toList())))
            .map(p -> Pair.of(
                p.getFirst(),
                groupThroughput(p.getSecond(), granularity)))
            .filter(p -> !p.getSecond().isEmpty())
            .collect(Pair.toMap());
    }

    @Override
    public Map<String, List<ThroughputSampleDTO>> getThroughputSamplesMonitorComp(String cdn, Instant startDate,
                                                                                  Instant endDate, Long granularity) {
        return monitorService.getActiveMonitors().stream()
            .map(monitor -> Pair.of(
                monitor.getName(),
                throughputSampleRepository.findAllByTimestampBetweenAndMonitor(
                    startDate, endDate, monitor).stream()
                    .filter(sample -> sample.getUrl().getCdn().getName().equals(cdn))
                    .collect(Collectors.toList())))
            .map(p -> Pair.of(
                p.getFirst(),
                groupThroughput(p.getSecond(), granularity)))
            .filter(p -> !p.getSecond().isEmpty())
            .collect(Pair.toMap());
    }

    @Override
    public boolean checkRttSamplesExist(Instant startDate, Instant endDate, String monitor) {
        if (isAllMonitors(monitor)) {
            return rttSampleRepository.existsByTimestampBetween(startDate, endDate);
        } else {
            return rttSampleRepository.existsByTimestampBetweenAndMonitor(startDate, endDate,
                monitorService.getMonitor(monitor));
        }
    }

    @Override
    public boolean checkThroughputSamplesExist(Instant startDate, Instant endDate, String monitor) {
        if (isAllMonitors(monitor)) {
            return throughputSampleRepository.existsByTimestampBetween(startDate, endDate);
        } else {
            return throughputSampleRepository.existsByTimestampBetweenAndMonitor(startDate, endDate,
                monitorService.getMonitor(monitor));
        }
    }

    private List<ThroughputSampleDTO> groupThroughput(List<ThroughputSample> samples, long granularity) {
        return group(samples, granularity)
            .map(entry -> ThroughputSampleDTO.builder()
                .timestamp(Instant.ofEpochMilli(entry.getKey() * granularity))
                .throughput(average(entry.getValue(), ThroughputSample::getThroughput))
                .build())
            .collect(Collectors.toList());
    }

    private List<RTTSampleDTO> groupRTT(List<RTTSample> samples, long granularity) {
        return group(samples, granularity)
            .map(entry -> RTTSampleDTO.builder()
                .timestamp(Instant.ofEpochMilli(entry.getKey() * granularity))
                .packetLoss(average(entry.getValue(), RTTSample::getPacketLoss))
                .average(average(entry.getValue(), RTTSample::getAverage))
                .standardDeviation(average(entry.getValue(), RTTSample::getStandardDeviation))
                .max(max(entry.getValue(), RTTSample::getMax))
                .min(min(entry.getValue(), RTTSample::getMin))
                .build())
            .collect(Collectors.toList());
    }

    private <T extends Sample> Stream<Map.Entry<Long, List<T>>> group(List<T> samples, long granularity) {
        return samples.stream()
            .collect(Collectors.groupingByConcurrent(sample -> sample.getTimestamp().toEpochMilli() / granularity))
            .entrySet()
            .stream()
            .sorted((a, b) -> (int) (a.getKey() - b.getKey()));
    }

    private Long average(List<ThroughputSample> samples, ToLongFunction<ThroughputSample> extractor) {
        return samples.stream().collect(Collectors.averagingLong(extractor)).longValue();
    }

    private Float average(List<RTTSample> samples, ToDoubleFunction<RTTSample> extractor) {
        return samples.stream().collect(Collectors.averagingDouble(extractor)).floatValue();
    }

    private Float max(List<RTTSample> samples, Function<RTTSample, Float> extractor) {
        return samples.stream()
            .map(extractor)
            .max(Comparator.naturalOrder())
            .orElse(0f);
    }

    private Float min(List<RTTSample> samples, Function<RTTSample, Float> extractor) {
        return samples.stream()
            .map(extractor)
            .min(Comparator.naturalOrder())
            .orElse(0f);
    }

    private boolean isAllMonitors(String monitor) {
        return monitor == null || monitor.isBlank() || monitor.equals(ALL_MONITORS);
    }
}
