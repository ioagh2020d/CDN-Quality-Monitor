package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.Cdn;

import java.util.List;

@Repository
public interface CdnRepository extends JpaRepository<Cdn, Long> {

    Cdn findByAddressEquals(String address);
    List<Cdn> findByInUseTrue();
}
