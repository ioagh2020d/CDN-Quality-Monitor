package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.Cdn;
import pl.edu.agh.cqm.data.model.Monitor;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.Url;

import javax.persistence.OrderBy;
import java.time.Instant;
import java.util.List;

@Repository
public interface RTTSampleRepository extends JpaRepository<RTTSample, Long> {

    @OrderBy("timestamp ASC")
    List<RTTSample> findAllByTimestampBetweenAndUrl(Instant startDate, Instant endDate, Url url);

    boolean existsByTimestampBetween(Instant startDate, Instant endDate);

    boolean existsByTimestampBetweenAndMonitor(Instant startDate, Instant endDate, Monitor monitor);

    @Query("""
            from RTTSample
            where url.cdn = ?1
                and url.active = true
                and timestamp between ?2 and ?3
            order by timestamp asc
            """)
    List<RTTSample> findByCdnAndTimestampBetween(Cdn cdn, Instant startDate, Instant endDate);
}
