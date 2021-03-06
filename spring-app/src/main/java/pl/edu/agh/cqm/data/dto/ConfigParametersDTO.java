package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ConfigParametersDTO {

    @NotNull
    private List<CdnWithUrlsDTO> cdns;

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
