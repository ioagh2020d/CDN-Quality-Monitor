package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public abstract class SampleDTO {

    @NotNull
    private Instant timestamp;
}
