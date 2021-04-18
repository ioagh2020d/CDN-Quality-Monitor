package pl.edu.agh.cqm.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParametersDTO {

    private int activeSamplingRate;
    private int activeTestIntensity;

    private int passiveSamplingRate;
}
