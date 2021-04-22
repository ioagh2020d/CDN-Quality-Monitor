package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.repository.RTTSampleRepository;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    private final RTTSampleRepository rttSampleRepository;
    private final ThroughputSampleRepository throughputSampleRepository;
    private final ParameterService parameterService;

    @Override
    public Map<String, List<RTTSampleDTO>> getRTTSamples(Instant startDate, Instant endDate) {
        return parameterService.getCdns().stream()
            .map(cdn -> Pair.of(cdn, rttSampleRepository.findAllByTimestampBetweenAndAddress(startDate, endDate, cdn)))
                .map(p -> Pair.of(
                p.getFirst(),
                p.getSecond().stream()
                    .map(RTTSample::toDTO)
                    .collect(Collectors.toList())))
            .collect(Pair.toMap());
    }

    @Override
    public Map<String, List<ThroughputSampleDTO>> getThroughputSamples(Instant startDate, Instant endDate) {
        return parameterService.getCdns().stream()
            .map(cdn -> Pair.of(cdn, throughputSampleRepository.findAllByTimestampBetweenAndAddress(startDate, endDate, cdn)))
            .map(p -> Pair.of(
                p.getFirst(),
                p.getSecond().stream()
                    .map(ThroughputSample::toDTO)
                    .collect(Collectors.toList())))
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
}
