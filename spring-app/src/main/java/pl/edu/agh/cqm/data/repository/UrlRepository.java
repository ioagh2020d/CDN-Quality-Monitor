package pl.edu.agh.cqm.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.cqm.data.model.Url;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    List<Url> findByActiveTrue();
    List<Url> findByCdnIdEqualsAndActiveTrue(long cdnId);
    Url findByCdnIdEqualsAndAddressEquals(long cdnId, String address);
}
