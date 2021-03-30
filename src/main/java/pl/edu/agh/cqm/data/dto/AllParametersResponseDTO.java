package pl.edu.agh.cqm.data.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AllParametersResponseDTO {

    private List<RTTSampleDTO> rttSamples;

    private List<ThroughputSampleDTO> throughputSamples;
}
