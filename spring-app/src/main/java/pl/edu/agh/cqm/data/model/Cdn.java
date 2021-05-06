package pl.edu.agh.cqm.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Cdn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false, unique = true, length = 64)
    private String name;

    @Column(nullable = false)
    private boolean inUse;

    public Cdn(String name) {
        this.name = name;
        this.inUse = true;
    }
}
