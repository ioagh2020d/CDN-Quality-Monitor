package pl.edu.agh.cqm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.cqm.data.dto.AllParametersResponseDTO;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.SingleParameterResponseDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.service.MonitoringService;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/rtt")
    public SingleParameterResponseDTO<RTTSampleDTO> getRTT(
        @RequestParam Instant startDate,
        @RequestParam Instant endDate
    ) {

        List<RTTSampleDTO> samples = monitoringService.getRTTSamples(startDate, endDate).stream()
            .map(RTTSample::toDTO)
            .collect(Collectors.toList());

        return new SingleParameterResponseDTO<>(
            startDate,
            endDate,
            samples.size(),
            samples
        );
    }

    @GetMapping("/throughput")
    public SingleParameterResponseDTO<ThroughputSampleDTO> getThroughput(
        @RequestParam Instant startDate,
        @RequestParam Instant endDate
    ) {
        List<ThroughputSampleDTO> samples = monitoringService.getThroughputSamples(startDate, endDate).stream()
            .map(ThroughputSample::toDTO)
            .collect(Collectors.toList());

        return new SingleParameterResponseDTO<>(
            startDate,
            endDate,
            samples.size(),
            samples
        );
    }

    @GetMapping("/all")
    public AllParametersResponseDTO getAll(
        @RequestParam Instant startDate,
        @RequestParam Instant endDate
    ) {
        List<RTTSampleDTO> rttSamples = monitoringService.getRTTSamples(startDate, endDate).stream()
            .map(RTTSample::toDTO)
            .collect(Collectors.toList());
        List<ThroughputSampleDTO> throughputSamples = monitoringService.getThroughputSamples(startDate, endDate).stream()
            .map(ThroughputSample::toDTO)
            .collect(Collectors.toList());

        return new AllParametersResponseDTO(rttSamples, throughputSamples);
    }
}
