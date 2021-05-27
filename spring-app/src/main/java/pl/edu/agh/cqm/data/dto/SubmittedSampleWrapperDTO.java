package pl.edu.agh.cqm.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class SubmittedSampleWrapperDTO<T extends SampleDTO> {

    @NotNull
    private T sample;

    @NotEmpty
    private String cdnName;

    @NotEmpty
    private String url;
}
