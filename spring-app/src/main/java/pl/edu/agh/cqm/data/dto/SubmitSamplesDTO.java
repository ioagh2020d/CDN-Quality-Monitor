package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.cqm.data.SubmittedSampleWrapperDTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSamplesDTO<T extends SampleDTO> {

    @NotNull
    @NotEmpty
    private List<SubmittedSampleWrapperDTO<T>> samples;
}
