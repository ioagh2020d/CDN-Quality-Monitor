package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.model.Url;

import javax.persistence.OrderBy;
import java.time.Instant;
import java.util.List;

@Repository
public interface ThroughputSampleRepository extends JpaRepository<ThroughputSample, Long> {

    @OrderBy("timestamp ASC")
    List<ThroughputSample> findAllByTimestampBetweenAndUrl(Instant startDate, Instant endDate, Url url);

    boolean existsByTimestampBetween(Instant startDate, Instant endDate);
}
