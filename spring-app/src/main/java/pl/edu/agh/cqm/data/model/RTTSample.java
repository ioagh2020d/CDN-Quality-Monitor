package pl.edu.agh.cqm.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(indexes = {
    @Index(columnList = "address"),
    @Index(columnList = "timestamp")
})
public class RTTSample implements Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false, unique = true)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private float average;

    @Column(nullable = false, updatable = false)
    private float min;

    @Column(nullable = false, updatable = false)
    private float max;

    @Column(nullable = false, updatable = false)
    private float standardDeviation;

    @Column(nullable = false, updatable = false)
    private float packetLoss;

    @Column(nullable = false, updatable = false, length = 64)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 8)
    private CqmConfiguration.ActiveTestType type;
}
