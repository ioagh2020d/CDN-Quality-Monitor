package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.cqm.data.dto.ConfigParametersDTO;
import pl.edu.agh.cqm.data.repository.CdnRepository;
import pl.edu.agh.cqm.data.repository.UrlRepository;
import pl.edu.agh.cqm.service.ParameterService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("api/parameters")
@AllArgsConstructor
public class ParameterController {

    private final ParameterService parameterService;

    @PutMapping
    public void put(
            @Valid @RequestBody ConfigParametersDTO configParametersDTO
    ) {
        Map<String, List<String>> cdns = configParametersDTO.getCdns();
        parameterService.updateCdnsWithUrls(cdns);

        int activeSamplingRate = configParametersDTO.getActiveSamplingRate();
        int activeTestIntensity = configParametersDTO.getActiveTestIntensity();
        int passiveSamplingRate = configParametersDTO.getPassiveSamplingRate();
        parameterService.updateSampleParameters(activeSamplingRate, activeTestIntensity, passiveSamplingRate);
    }

    @GetMapping
    public ConfigParametersDTO get() {
        return ConfigParametersDTO.builder()
                .cdns(parameterService.getCdnsWithUrls())
                .activeSamplingRate(parameterService.getActiveSamplingRate())
                .activeTestIntensity(parameterService.getActiveTestIntensity())
                .passiveSamplingRate(parameterService.getPassiveSamplingRate())
                .build();
    }

}
