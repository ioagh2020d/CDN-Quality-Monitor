package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.Cdn;
import pl.edu.agh.cqm.data.model.Url;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    List<Url> findByActiveTrue();
    List<Url> findByCdnAndActiveTrue(Cdn cdn);
    Optional<Url> findByCdnAndAddressEquals(Cdn cdn, String address);
}
