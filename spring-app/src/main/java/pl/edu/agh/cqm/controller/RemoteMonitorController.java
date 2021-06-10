package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.SubmitSamplesDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.service.MonitorService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/remotes")
@AllArgsConstructor
public class RemoteMonitorController {

    public static final String FORWARDED_FOR_HEADER = "X-Forwarded_For";

    private final MonitorService monitorService;

    @PostMapping("/rtt")
    public void submitRTTSamples(@Valid @RequestBody SubmitSamplesDTO<RTTSampleDTO> samplesDTO, HttpServletRequest request) {
        var address = Optional.ofNullable(request.getHeader(FORWARDED_FOR_HEADER))
            .orElse(request.getRemoteAddr());
        monitorService.submitRTTSamples(samplesDTO, address);
    }

    @PostMapping("/throughput")
    public void submitThroughputSamples(@Valid @RequestBody SubmitSamplesDTO<ThroughputSampleDTO> samplesDTO, HttpServletRequest request) {
        var address = Optional.ofNullable(request.getHeader(FORWARDED_FOR_HEADER))
            .orElse(request.getRemoteAddr());
        monitorService.submitThroughputSamples(samplesDTO, address);
    }
}
