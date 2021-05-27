package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MonitorsResponseDTO {

    @NotNull
    Boolean isLocal;

    @NotNull
    private List<MonitorDTO> monitors;
}
