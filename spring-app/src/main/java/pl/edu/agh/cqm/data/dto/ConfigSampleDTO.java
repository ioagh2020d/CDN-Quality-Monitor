package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSampleDTO {

    private Instant timestamp;

    private int activeSamplingRate;
    private int activeTestsIntensity;

    private int passiveSamplingRate;
}
