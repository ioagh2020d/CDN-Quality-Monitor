package pl.edu.agh.cqm.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue
    private long id;

    @Column(nullable = false, updatable = false, unique = true)
    private Instant timestamp;

    @Column(nullable = false, updatable = false)
    private float average;

    @Column(nullable = false, updatable = false)
    private float min;

    @Column(nullable = false, updatable = false)
    private float max;
}
