package pl.edu.agh.cqm.data.dto;

import com.sun.istack.NotNull;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.service.MonitoringService;

@Data
@NoArgsConstructor
public class SampleSearchDTO {

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    private Long granularity = MonitoringService.DEFAULT_GRANULARITY;

    private String monitor;
}
