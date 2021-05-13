package pl.edu.agh.cqm.data.dto.singlecdn;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.service.MonitoringService;

import java.time.Instant;

@Data
@NoArgsConstructor
public class SingleCdnSampleSearchDTO {

    @NotNull
    private String cdn;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    private Long granularity = MonitoringService.DEFAULT_GRANULARITY;
}
