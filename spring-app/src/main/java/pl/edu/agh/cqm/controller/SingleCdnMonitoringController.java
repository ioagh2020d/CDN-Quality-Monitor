package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.data.dto.singlecdn.SingleCdnAllParametersResponseDTO;
import pl.edu.agh.cqm.data.dto.singlecdn.SingleCdnSampleSearchDTO;
import pl.edu.agh.cqm.data.dto.singlecdn.SingleCdnSingleParameterResponseDTO;
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
@RequestMapping("/api/samples/singleCdn")
@AllArgsConstructor
public class SingleCdnMonitoringController {

    private final MonitoringService monitoringService;
    private final DeviationsService deviationsService;
    private final ParameterService parameterService;

    @GetMapping("/rtt")
    public SingleCdnSingleParameterResponseDTO<RTTSampleDTO> getRTT(
            @Valid SingleCdnSampleSearchDTO searchDTO
    ) {
        try {
            Map<String, List<RTTSampleDTO>> rttSamples = monitoringService.getRTTSamples(searchDTO.getCdn(),
                    searchDTO.getStartDate(), searchDTO.getEndDate(), searchDTO.getGranularity());
            if (rttSamples.values().stream().allMatch(List::isEmpty)) {
                throw new IllegalArgumentException();
            }

            return new SingleCdnSingleParameterResponseDTO<>(
                    searchDTO.getCdn(),
                    searchDTO.getStartDate(),
                    searchDTO.getEndDate(),
                    rttSamples,
                    deviationsService.getRTTDeviations(rttSamples),
                    parameterService.getParameterHistory(searchDTO.getStartDate(), searchDTO.getEndDate())
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

    @GetMapping("/throughput")
    public SingleCdnSingleParameterResponseDTO<ThroughputSampleDTO> getThroughput(
            @Valid SingleCdnSampleSearchDTO searchDTO
    ) {
        try {
            Map<String, List<ThroughputSampleDTO>> throughputSamples = monitoringService.getThroughputSamples(
                    searchDTO.getCdn(), searchDTO.getStartDate(), searchDTO.getEndDate(), searchDTO.getGranularity());
            if (throughputSamples.values().stream().allMatch(List::isEmpty)) {
                throw new IllegalArgumentException();
            }

            return new SingleCdnSingleParameterResponseDTO<>(
                    searchDTO.getCdn(),
                    searchDTO.getStartDate(),
                    searchDTO.getEndDate(),
                    throughputSamples,
                    deviationsService.getThroughputDeviations(throughputSamples),
                    parameterService.getParameterHistory(searchDTO.getStartDate(), searchDTO.getEndDate())
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

    @GetMapping("/all")
    public SingleCdnAllParametersResponseDTO getAll(
            @Valid SingleCdnSampleSearchDTO searchDTO
    ) {
        try {
            String cdn = searchDTO.getCdn();
            Instant startDate = searchDTO.getStartDate();
            Instant endDate = searchDTO.getEndDate();
            Long granularity = searchDTO.getGranularity();

            Map<String, List<RTTSampleDTO>> rttSamples =
                    monitoringService.getRTTSamples(cdn, startDate, endDate, granularity);
            Map<String, List<ThroughputSampleDTO>> throughputSamples =
                    monitoringService.getThroughputSamples(cdn, startDate, endDate, granularity);

            if (rttSamples.values().stream().allMatch(List::isEmpty)
                    && throughputSamples.values().stream().allMatch(List::isEmpty)) {
                throw new IllegalArgumentException();
            }

            return new SingleCdnAllParametersResponseDTO(
                    cdn,
                    startDate,
                    endDate,
                    rttSamples,
                    throughputSamples,
                    deviationsService.getAllDeviations(rttSamples, throughputSamples),
                    parameterService.getParameterHistory(startDate, endDate)
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

}
