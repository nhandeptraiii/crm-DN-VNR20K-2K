package vn.viettel.khdn.crm_DN_VNR20K_2K.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Commune;
import java.util.Optional;

@Repository
public interface CommuneRepository extends JpaRepository<Commune, Long> {
    Optional<Commune> findByCode(String code);
}
