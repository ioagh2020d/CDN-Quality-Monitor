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

    private final RTTSampleRepository rttSampleRepository;
    private final ThroughputSampleRepository throughputSampleRepository;
    private final ParameterService parameterService;

    @Override
    public Map<String, List<RTTSampleDTO>> getRTTSamples(Instant startDate, Instant endDate, Long granularity) {
        return parameterService.getActiveUrlAddresses().stream()
            .map(cdn -> Pair.of(cdn, rttSampleRepository.findAllByTimestampBetweenAndAddress(startDate, endDate, cdn)))
            .map(p -> Pair.of(
                p.getFirst(),
                groupRTT(p.getSecond(), granularity)))
            .collect(Pair.toMap());
    }

    @Override
    public Map<String, List<ThroughputSampleDTO>> getThroughputSamples(Instant startDate, Instant endDate, Long granularity) {
        return parameterService.getActiveUrlAddresses().stream()
            .map(cdn -> Pair.of(cdn, throughputSampleRepository.findAllByTimestampBetweenAndAddress(startDate, endDate, cdn)))
            .map(p -> Pair.of(
                p.getFirst(),
                groupThroughput(p.getSecond(), granularity)))
            .collect(Pair.toMap());
    }

    @Override
    public boolean checkRttSamplesExist(Instant startDate, Instant endDate) {
        return rttSampleRepository.existsByTimestampBetween(startDate, endDate);
    }

    @Override
    public boolean checkThroughputSamplesExist(Instant startDate, Instant endDate) {
        return throughputSampleRepository.existsByTimestampBetween(startDate, endDate);
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
}
