package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.Cdn;
import pl.edu.agh.cqm.data.model.Monitor;
import pl.edu.agh.cqm.data.model.ThroughputSample;
import pl.edu.agh.cqm.data.model.Url;

import javax.persistence.OrderBy;
import java.time.Instant;
import java.util.List;

@Repository
public interface ThroughputSampleRepository extends JpaRepository<ThroughputSample, Long> {

    @OrderBy("timestamp ASC")
    List<ThroughputSample> findAllByTimestampBetweenAndUrl(Instant startDate, Instant endDate, Url url);

    @Query("""
            from ThroughputSample
            where url.cdn = ?1
                and url.active = true
                and timestamp between ?2 and ?3
            order by timestamp asc
            """)
    List<ThroughputSample> findByCdnAndTimestampBetween(Cdn cdn, Instant startDate, Instant endDate);

    boolean existsByTimestampBetween(Instant startDate, Instant endDate);

    boolean existsByTimestampBetweenAndMonitor(Instant startDate, Instant endDate, Monitor monitor);
}
