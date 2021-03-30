package pl.edu.agh.cqm.data.model;

import javax.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
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

    public RTTSampleDTO toDTO() {
        return RTTSampleDTO.builder()
            .id(id)
            .timestamp(timestamp)
            .average(average)
            .min(min)
            .max(max)
            .build();
    }
}
