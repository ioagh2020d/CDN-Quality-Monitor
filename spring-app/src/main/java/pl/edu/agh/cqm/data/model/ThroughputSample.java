package pl.edu.agh.cqm.data.model;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
    @Index(columnList = "address"),
    @Index(columnList = "timestamp")
})
public class ThroughputSample implements Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false, length = 64)
    private String address;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private long throughput;
}
