package pl.edu.agh.cqm.data.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
public abstract class SampleDTO {

    private long id;
    private Instant timestamp;
}
