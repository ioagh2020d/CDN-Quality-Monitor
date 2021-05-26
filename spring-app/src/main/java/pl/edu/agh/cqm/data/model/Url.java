package pl.edu.agh.cqm.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"cdn_id", "address"}))
@Data
@NoArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Cdn cdn;

    @Column(nullable = false, updatable = false, length = 64)
    private String address;

    @Column(nullable = false)
    boolean active;

    public Url(Cdn cdn, String address) {
        this.cdn = cdn;
        this.address = address;
        this.active = true;
    }

    public Url(Cdn cdn, String address, boolean active) {
        this.cdn = cdn;
        this.address = address;
        this.active = active;
    }
}
