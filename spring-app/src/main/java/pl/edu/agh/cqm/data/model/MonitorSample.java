package pl.edu.agh.cqm.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.data.dto.MonitorsDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class MonitorSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String address;

    @Column(nullable = false)
    boolean active;

    public MonitorsDTO toDTO() {
        return MonitorsDTO.builder()
                .address(address)
                .build();
    }
}
