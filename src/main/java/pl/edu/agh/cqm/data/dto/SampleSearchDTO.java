package pl.edu.agh.cqm.data.dto;

import com.sun.istack.NotNull;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SampleSearchDTO {

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;
}
