package pl.edu.agh.cqm.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.cqm.data.dto.MonitorsDTO;
import pl.edu.agh.cqm.data.dto.MonitorsResponseDTO;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/monitors")
@AllArgsConstructor
public class MonitorController {

//    @PutMapping
//    public void put(
//            @Valid @RequestBody MonitorsDTO monitorsDTO
//    ) {
//    }

    @GetMapping
    public MonitorsResponseDTO get() {
        return MonitorsResponseDTO.builder()
                .monitors(List.of(
                        MonitorsDTO.builder().id(1).address("1.1.1.1").build(),
                        MonitorsDTO.builder().id(2).address("2.2.2.2").build()
                ))
                .build();
    }
}
