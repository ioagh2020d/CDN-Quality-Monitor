package pl.edu.agh.cqm.data.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table
@EqualsAndHashCode
public class ConfigSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private long id;

    @Column(nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private int activeSamplingRate;

    @Column(nullable = false, updatable = false)
    private int activeTestIntensity;

    @Column(nullable = false, updatable = false)
    private int passiveSamplingRate;
}
