package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.Monitor;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    String LOCAL_MONITOR_NAME = "localhost";

    Optional<Monitor> getMonitorByName(String name);
}
