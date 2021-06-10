package pl.edu.agh.cqm.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
    @Index(columnList = "timestamp")
})
public class ThroughputSample implements Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Url url;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private long throughput;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Monitor monitor;

    public ThroughputSampleDTO toDTO() {
        return ThroughputSampleDTO.builder()
                .timestamp(timestamp)
                .throughput(throughput)
                .build();
    }
}
