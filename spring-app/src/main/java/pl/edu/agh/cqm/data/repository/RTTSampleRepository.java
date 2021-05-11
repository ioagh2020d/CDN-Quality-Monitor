package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.RTTSample;

import javax.persistence.OrderBy;
import java.time.Instant;
import java.util.List;

@Repository
public interface RTTSampleRepository extends JpaRepository<RTTSample, Long> {

    @OrderBy("timestamp ASC")
    List<RTTSample> findAllByTimestampBetweenAndAddress(Instant startDate, Instant endDate, String address); // TODO deprecated function

    @OrderBy("timestamp ASC")
    List<RTTSample> findAllByTimestampBetweenAndUrlId(Instant startDate, Instant endDate, Long urlId);

    boolean existsByTimestampBetween(Instant startDate, Instant endDate);
}
