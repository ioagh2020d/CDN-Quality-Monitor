package pl.edu.agh.cqm.data.model;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

@Data
@NoArgsConstructor
@Entity
public class ThroughputSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false, unique = true)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private long throughput;

    public ThroughputSampleDTO toDTO() {
        return ThroughputSampleDTO.builder()
            .id(id)
            .timestamp(timestamp)
            .throughput(throughput)
            .build();
    }
}
