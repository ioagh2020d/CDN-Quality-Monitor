package pl.edu.agh.cqm.data.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.List;

@AllArgsConstructor
public class CdnDeviations {

    @Getter(onMethod_={@JsonAnyGetter})
    private Map<String, List<DeviationDTO>> deviations;

    public List<DeviationDTO> get(String parameter) {
        return deviations.get(parameter);
    }
}