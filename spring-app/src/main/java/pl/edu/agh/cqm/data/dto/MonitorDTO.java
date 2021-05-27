package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MonitorDTO {
    @NotNull
    private Long id;

    @NotNull
    private String name;
}
