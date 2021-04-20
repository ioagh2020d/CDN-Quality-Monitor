package pl.edu.agh.cqm.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table
public class ConfigCdn {

    @Id
    @Column(nullable = false, updatable = false)
    String cdn;
}
