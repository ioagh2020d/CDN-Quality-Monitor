package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.cqm.data.model.ConfigSample;

import java.time.Instant;

public interface ConfigSampleRepository extends JpaRepository<ConfigSample, Long> {

    ConfigSample findFirstByOrderByTimestampDesc();

    ConfigSample findFirstByTimestampGreaterThanEqual(Instant timestamp);
}
