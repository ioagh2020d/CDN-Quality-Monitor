package pl.edu.agh.cqm.data.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ThroughputSampleDTO extends SampleDTO {

    private long throughput;
}
