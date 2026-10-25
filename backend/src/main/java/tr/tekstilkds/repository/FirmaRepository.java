package tr.tekstilkds.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.tekstilkds.entity.Firma;

public interface FirmaRepository extends JpaRepository<Firma, Long> {
}
