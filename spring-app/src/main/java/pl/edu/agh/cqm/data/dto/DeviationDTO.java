package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class DeviationDTO {

    private Instant startDate;
    private Instant endDate;
    String description;
}
