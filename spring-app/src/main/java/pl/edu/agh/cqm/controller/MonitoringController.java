package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.cqm.data.dto.*;
import pl.edu.agh.cqm.exception.BadRequestException;
import pl.edu.agh.cqm.service.DeviationsService;
import pl.edu.agh.cqm.service.MonitoringService;
import pl.edu.agh.cqm.service.ParameterService;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/samples")
@AllArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final DeviationsService deviationsService;
    private final ParameterService parameterService;

    @GetMapping("/rtt")
    public SingleParameterResponseDTO<RTTSampleDTO> getRTT(
        @Valid SampleSearchDTO searchDTO
    ) {
        if (!monitoringService.checkRttSamplesExist(searchDTO.getStartDate(), searchDTO.getEndDate(),
                searchDTO.getMonitor())) {
            throw new BadRequestException();
        }
        Map<String, List<RTTSampleDTO>> rttSamples = monitoringService.getRTTSamples(searchDTO.getStartDate(),
                searchDTO.getEndDate(), searchDTO.getGranularity(), searchDTO.getMonitor());

        return new SingleParameterResponseDTO<>(
                searchDTO.getStartDate(),
                searchDTO.getEndDate(),
                rttSamples,
                deviationsService.getRTTDeviations(rttSamples),
                parameterService.getParameterHistory(searchDTO.getStartDate(), searchDTO.getEndDate())
        );
    }

    @GetMapping("/throughput")
    public SingleParameterResponseDTO<ThroughputSampleDTO> getThroughput(
        @Valid SampleSearchDTO searchDTO
    ) {
        if (!monitoringService.checkThroughputSamplesExist(searchDTO.getStartDate(), searchDTO.getEndDate(),
                searchDTO.getMonitor())) {
            throw new BadRequestException();
        }
        Map<String, List<ThroughputSampleDTO>> throughputSamples = monitoringService.getThroughputSamples(
                searchDTO.getStartDate(), searchDTO.getEndDate(), searchDTO.getGranularity(),
                searchDTO.getMonitor());

        return new SingleParameterResponseDTO<>(
                searchDTO.getStartDate(),
                searchDTO.getEndDate(),
                throughputSamples,
                deviationsService.getThroughputDeviations(throughputSamples),
                parameterService.getParameterHistory(searchDTO.getStartDate(), searchDTO.getEndDate())
        );
    }

    @GetMapping("/all")
    public AllParametersResponseDTO getAll(
        @Valid SampleSearchDTO searchDTO
    ) {
        Instant startDate = searchDTO.getStartDate();
        Instant endDate = searchDTO.getEndDate();
        String monitor = searchDTO.getMonitor();

        if (!monitoringService.checkRttSamplesExist(startDate, endDate, monitor)
            && !monitoringService.checkThroughputSamplesExist(startDate, endDate, monitor)) {
            throw new BadRequestException();
        }
        Map<String, List<RTTSampleDTO>> rttSamples = monitoringService.getRTTSamples(startDate, endDate,
                searchDTO.getGranularity(), monitor);
        Map<String, List<ThroughputSampleDTO>> throughputSamples = monitoringService.getThroughputSamples(
                startDate, endDate, searchDTO.getGranularity(), monitor);

        return new AllParametersResponseDTO(
                startDate,
                endDate,
                rttSamples,
                throughputSamples,
                deviationsService.getAllDeviations(rttSamples, throughputSamples),
                parameterService.getParameterHistory(startDate, endDate)
        );
    }
}
