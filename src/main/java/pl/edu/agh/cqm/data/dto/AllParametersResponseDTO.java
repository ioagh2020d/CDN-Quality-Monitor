package pl.edu.agh.cqm.data.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AllParametersResponseDTO {

    private Instant startDate;

    private Instant endDate;

    private Map<String, List<RTTSampleDTO>> rttSamples;

    private Map<String, List<ThroughputSampleDTO>> throughputSamples;
}
