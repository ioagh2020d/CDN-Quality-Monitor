package pl.edu.agh.cqm.data.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class SingleParameterResponseDTO<T extends SampleDTO> {

    private Instant startDate;
    private Instant endDate;
    private Map<String, List<T>> samples;

    private Map<String, CdnDeviations> deviations;
}
