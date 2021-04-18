package pl.edu.agh.cqm.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ConfigParametersDTO {

    private List<String> cdns;

    private int activeSamplingRate;
    private int activeTestIntensity;

    private int passiveSamplingRate;
}
