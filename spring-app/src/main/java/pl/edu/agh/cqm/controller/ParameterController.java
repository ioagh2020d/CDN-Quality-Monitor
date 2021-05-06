package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.cqm.data.dto.CdnWithUrlsDTO;
import pl.edu.agh.cqm.data.dto.ConfigParametersDTO;
import pl.edu.agh.cqm.service.ParameterService;

import javax.validation.Valid;
import java.util.List;

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
        List<CdnWithUrlsDTO> cdns = configParametersDTO.getCdns();
        parameterService.updateCdns(cdns);

        int activeSamplingRate = configParametersDTO.getActiveSamplingRate();
        int activeTestIntensity = configParametersDTO.getActiveTestIntensity();
        int passiveSamplingRate = configParametersDTO.getPassiveSamplingRate();
        parameterService.updateSampleParameters(activeSamplingRate, activeTestIntensity, passiveSamplingRate);
    }

    @GetMapping
    public ConfigParametersDTO get() {
        return ConfigParametersDTO.builder()
                .cdns(parameterService.getCdns())
                .activeSamplingRate(parameterService.getActiveSamplingRate())
                .activeTestIntensity(parameterService.getActiveTestIntensity())
                .passiveSamplingRate(parameterService.getPassiveSamplingRate())
                .build();
    }

}
