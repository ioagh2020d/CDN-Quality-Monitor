package pl.edu.agh.cqm.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"cdnId", "address"}))
@Data
@NoArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false)
    private long cdnId;

    @Column(nullable = false, updatable = false, length = 64)
    private String address;

    @Column(nullable = false)
    boolean active;

    public Url(long cdnId, String address) {
        this.cdnId = cdnId;
        this.address = address;
        this.active = true;
    }
}
