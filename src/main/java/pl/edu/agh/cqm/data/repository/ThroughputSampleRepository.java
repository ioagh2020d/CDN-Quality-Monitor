package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.ThroughputSample;

@Repository
public interface ThroughputSampleRepository extends JpaRepository<ThroughputSample, Long> {
}
