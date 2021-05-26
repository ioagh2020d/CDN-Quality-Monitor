package pl.edu.agh.cqm.data.dto;

import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.edu.agh.cqm.data.serializers.ThroughputSampleDTOSerializer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonAdapter(ThroughputSampleDTOSerializer.class)
public class ThroughputSampleDTO extends SampleDTO {

    @NotNull
    @Min(0)
    private Long throughput;
}
