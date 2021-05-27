package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RTTSampleDTO extends SampleDTO {

    @NotNull
    @Min(0)
    private Float average;

    @NotNull
    @Min(0)
    private Float min;

    @NotNull
    @Min(0)
    private Float max;

    @NotNull
    private Float standardDeviation;

    @NotNull
    @Min(0)
    private Float packetLoss;

    @NotNull
    private CqmConfiguration.ActiveTestType type;
}
