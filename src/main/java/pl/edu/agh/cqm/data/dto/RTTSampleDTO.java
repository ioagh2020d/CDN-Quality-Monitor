package pl.edu.agh.cqm.data.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RTTSampleDTO extends SampleDTO {

    private float average;
    private float min;
    private float max;
    private float standardDeviation;
    private float packetLoss;
}
