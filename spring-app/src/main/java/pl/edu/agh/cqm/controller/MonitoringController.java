package pl.edu.agh.cqm.controller;

import java.time.Instant;
import java.util.List;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.*;
import pl.edu.agh.cqm.data.dto.*;
import pl.edu.agh.cqm.exception.BadRequestException;
import pl.edu.agh.cqm.service.MonitoringService;
import pl.edu.agh.cqm.service.ParameterService;

@RestController
@RequestMapping("/api/samples")
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final ParameterService parameterService;

    public MonitoringController(MonitoringService monitoringService,
                                ParameterService parameterService
    ) {
        this.monitoringService = monitoringService;
        this.parameterService = parameterService;
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

    @PutMapping("/updateParameters")
    public void ParametersDTO(
            @Valid ConfigParametersDTO configParametersDTO
    ) {
        List<String> cdns = configParametersDTO.getCdns();
        parameterService.updateCdns(cdns);

        int activeSamplingRate = configParametersDTO.getActiveSamplingRate();
        int activeTestIntensity = configParametersDTO.getActiveTestIntensity();

        int passiveSamplingRate = configParametersDTO.getPassiveSamplingRate();

        parameterService.updateSampleParameters(activeSamplingRate, activeTestIntensity, passiveSamplingRate);
    }
}
