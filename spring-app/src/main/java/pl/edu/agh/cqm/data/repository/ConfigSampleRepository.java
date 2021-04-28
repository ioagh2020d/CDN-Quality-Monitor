package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.cqm.data.model.ConfigSample;

import java.time.Instant;
import java.util.List;

public interface ConfigSampleRepository extends JpaRepository<ConfigSample, Long> {

    ConfigSample findFirstByOrderByTimestampDesc();

    ConfigSample findFirstByTimestampLessThanEqualOrderByTimestampDesc(Instant timestamp);

    List<ConfigSample> findAllByTimestampBetweenOrderByTimestamp(Instant startDate, Instant endDate);
}
