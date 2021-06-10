package pl.edu.agh.cqm.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(indexes = {
        @Index(columnList = "timestamp")
})
public class RTTSample implements Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false)
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

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Url url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 8)
    private CqmConfiguration.ActiveTestType type;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Monitor monitor;

    public RTTSampleDTO toDTO() {
        return RTTSampleDTO.builder()
                .timestamp(timestamp)
                .average(average)
                .min(min)
                .max(max)
                .standardDeviation(standardDeviation)
                .packetLoss(packetLoss)
                .type(type)
                .build();
    }
}
