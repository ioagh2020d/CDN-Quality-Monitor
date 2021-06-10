package pl.edu.agh.cqm.data.model;

import pl.edu.agh.cqm.data.dto.SampleDTO;

import java.time.Instant;

public interface Sample {

    Instant getTimestamp();

    SampleDTO toDTO();

    Url getUrl();
}
