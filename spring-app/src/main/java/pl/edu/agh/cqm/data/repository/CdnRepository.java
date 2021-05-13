package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.Cdn;

import java.util.List;
import java.util.Optional;

@Repository
public interface CdnRepository extends JpaRepository<Cdn, Long> {

    Optional<Cdn> findByNameEquals(String name);

    List<Cdn> findByActiveTrue();
}
