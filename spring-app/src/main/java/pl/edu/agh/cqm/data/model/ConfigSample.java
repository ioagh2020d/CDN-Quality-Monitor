package pl.edu.agh.cqm.data.model;

import lombok.*;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class ConfigSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private int activeSamplingRate;

    @Column(nullable = false, updatable = false)
    private int activeTestIntensity;

    @Column(nullable = false, updatable = false)
    private int passiveSamplingRate;

    public ConfigSampleDTO toDTO() {
        return ConfigSampleDTO.builder()
                .timestamp(timestamp)
                .activeSamplingRate(activeSamplingRate)
                .activeTestsIntensity(activeTestIntensity)
                .passiveSamplingRate(passiveSamplingRate)
                .build();
    }
}
