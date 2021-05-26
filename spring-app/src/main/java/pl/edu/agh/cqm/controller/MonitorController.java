package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.cqm.data.dto.MonitorsResponseDTO;
import pl.edu.agh.cqm.service.MonitorService;

@CrossOrigin
@RestController
@RequestMapping("api/monitors")
@AllArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @GetMapping
    public MonitorsResponseDTO get() {
        return MonitorsResponseDTO.builder()
                .monitors(monitorService.getActiveMonitors())
                .build();
    }
}
