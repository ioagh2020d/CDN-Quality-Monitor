package pl.edu.agh.cqm.data.dto.singlecdn;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.agh.cqm.data.dto.CdnDeviations;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SingleCdnAllParametersResponseDTO {

    private String cdn;
    private Instant startDate;
    private Instant endDate;

    private Map<String, List<RTTSampleDTO>> rttSamples;
    private Map<String, List<ThroughputSampleDTO>> throughputSamples;

    private Map<String, CdnDeviations> deviations;
    private List<ConfigSampleDTO> parameterHistory;
}
