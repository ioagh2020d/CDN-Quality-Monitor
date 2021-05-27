package pl.edu.agh.cqm.data;

import lombok.Data;
import pl.edu.agh.cqm.data.dto.SampleDTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SubmittedSampleWrapperDTO<T extends SampleDTO> {

    @NotNull
    private T sample;

    @NotEmpty
    private String cdnName;

    @NotEmpty
    private String url;
}
