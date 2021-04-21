package pl.edu.agh.cqm.data.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.service.PingService;
import pl.edu.agh.cqm.service.PingServiceImpl;

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
public class RTTSample {

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
