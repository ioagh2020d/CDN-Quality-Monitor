package pl.edu.agh.cqm.controller;

import java.time.Instant;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;
import pl.edu.agh.cqm.data.dto.*;
import pl.edu.agh.cqm.exception.BadRequestException;
import pl.edu.agh.cqm.service.MonitoringService;

@RestController
@RequestMapping("/api/samples")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/rtt")
    public SingleParameterResponseDTO<RTTSampleDTO> getRTT(
            @Valid SampleSearchDTO searchDTO
    ) {
        if (!monitoringService.checkRttSamplesExist(searchDTO.getStartDate(), searchDTO.getEndDate())) {
            throw new BadRequestException();
        }
        return new SingleParameterResponseDTO<>(
                searchDTO.getStartDate(),
                searchDTO.getEndDate(),
                monitoringService.getRTTSamples(searchDTO.getStartDate(), searchDTO.getEndDate())
        );
    }

    @GetMapping("/throughput")
    public SingleParameterResponseDTO<ThroughputSampleDTO> getThroughput(
            @Valid SampleSearchDTO searchDTO
    ) {
        if (!monitoringService.checkThroughputSamplesExist(searchDTO.getStartDate(), searchDTO.getEndDate())) {
            throw new BadRequestException();
        }
        return new SingleParameterResponseDTO<>(
                searchDTO.getStartDate(),
                searchDTO.getEndDate(),
                monitoringService.getThroughputSamples(searchDTO.getStartDate(), searchDTO.getEndDate())
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
        return new AllParametersResponseDTO(
                startDate,
                endDate,
                monitoringService.getRTTSamples(startDate, endDate),
                monitoringService.getThroughputSamples(startDate, endDate)
        );
    }
}
