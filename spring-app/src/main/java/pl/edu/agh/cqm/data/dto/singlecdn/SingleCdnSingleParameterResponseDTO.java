package pl.edu.agh.cqm.data.dto.singlecdn;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.agh.cqm.data.dto.CdnDeviations;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;
import pl.edu.agh.cqm.data.dto.SampleDTO;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SingleCdnSingleParameterResponseDTO<T extends SampleDTO> {

    private String cdn;
    private Instant startDate;
    private Instant endDate;

    private Map<String, List<T>> samples;

    private Map<String, CdnDeviations> deviations;
    private List<ConfigSampleDTO> parameterHistory;
}
