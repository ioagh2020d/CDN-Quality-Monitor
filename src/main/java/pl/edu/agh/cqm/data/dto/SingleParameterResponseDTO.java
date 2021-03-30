package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class SingleParameterResponseDTO<T extends SampleDTO> {

    private Instant startDate;
    private Instant endDate;
    private int total;
    private List<T> samples;
}
