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

    @GetMapping("/rtt")
    public SingleParameterResponseDTO<RTTSampleDTO> getRTT(
            @Valid SampleSearchDTO searchDTO
    ) {
        if (!monitoringService.checkRttSamplesExist(searchDTO.getStartDate(), searchDTO.getEndDate())) {
            throw new BadRequestException();
        }
        Map<String, List<RTTSampleDTO>> rttSamples =
                monitoringService.getRTTSamples(searchDTO.getStartDate(), searchDTO.getEndDate());
        return new SingleParameterResponseDTO<>(
                searchDTO.getStartDate(),
                searchDTO.getEndDate(),
                rttSamples,
                deviationsService.getRTTDeviations(rttSamples)
        );
    }

    @GetMapping("/throughput")
    public SingleParameterResponseDTO<ThroughputSampleDTO> getThroughput(
            @Valid SampleSearchDTO searchDTO
    ) {
        if (!monitoringService.checkThroughputSamplesExist(searchDTO.getStartDate(), searchDTO.getEndDate())) {
            throw new BadRequestException();
        }
        Map<String, List<ThroughputSampleDTO>> throughputSamples =
                monitoringService.getThroughputSamples(searchDTO.getStartDate(), searchDTO.getEndDate());
        return new SingleParameterResponseDTO<>(
                searchDTO.getStartDate(),
                searchDTO.getEndDate(),
                throughputSamples,
                deviationsService.getThroughputDeviations(throughputSamples)
        );
    }

    @GetMapping("/all")
    public AllParametersResponseDTO getAll(
            @Valid SampleSearchDTO searchDTO
    ) {
        Instant startDate = searchDTO.getStartDate();
        Instant endDate = searchDTO.getEndDate();

        if (!monitoringService.checkRttSamplesExist(startDate, endDate)
                && !monitoringService.checkThroughputSamplesExist(startDate, endDate)) {
            throw new BadRequestException();
        }
        Map<String, List<RTTSampleDTO>> rttSamples =
                monitoringService.getRTTSamples(searchDTO.getStartDate(), searchDTO.getEndDate());
        Map<String, List<ThroughputSampleDTO>> throughputSamples =
                monitoringService.getThroughputSamples(searchDTO.getStartDate(), searchDTO.getEndDate());
        return new AllParametersResponseDTO(
                startDate,
                endDate,
                rttSamples,
                throughputSamples,
                deviationsService.getAllDeviations(rttSamples, throughputSamples)
        );
    }
}
