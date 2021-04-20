package pl.edu.agh.cqm.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class ConfigParametersDTO {

    @NotNull
    private List<String> cdns;

    @NotNull
    @Min(1)
    private Integer activeSamplingRate;

    @NotNull
    @Min(1)
    private Integer activeTestIntensity;

    @NotNull
    @Min(1)
    private Integer passiveSamplingRate;
}
