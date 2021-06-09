package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.cqm.data.dto.MonitorsResponseDTO;
import pl.edu.agh.cqm.data.model.Monitor;
import pl.edu.agh.cqm.service.MonitorService;

import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("api/monitors")
@AllArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @GetMapping
    public MonitorsResponseDTO get() {
        return MonitorsResponseDTO.builder()
                .isLocal(monitorService.isLocal())
                .monitors(monitorService.getActiveMonitors().stream()
                        .map(Monitor::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
