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

@RestController
@RequestMapping("/api/remotes")
@AllArgsConstructor
public class RemoteMonitorController {

    private final MonitorService monitorService;

    @PostMapping("/rtt")
    public void submitRTTSamples(@Valid @RequestBody SubmitSamplesDTO<RTTSampleDTO> samplesDTO, HttpServletRequest request) {
        monitorService.submitRTTSamples(samplesDTO, request.getRemoteAddr());
    }

    @PostMapping("/throughput")
    public void submitThroughputSamples(@Valid @RequestBody SubmitSamplesDTO<ThroughputSampleDTO> samplesDTO, HttpServletRequest request) {
        monitorService.submitThroughputSamples(samplesDTO, request.getRemoteAddr());
    }
}
