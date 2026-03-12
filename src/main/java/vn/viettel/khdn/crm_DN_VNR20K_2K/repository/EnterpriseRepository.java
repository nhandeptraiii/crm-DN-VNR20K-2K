package vn.viettel.khdn.crm_DN_VNR20K_2K.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Enterprise;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseStatus;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Optional<Enterprise> findByTaxCode(String taxCode);

    boolean existsByTaxCode(String taxCode);

    @Query("SELECT e FROM Enterprise e WHERE "
            + "(:keyword IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(e.taxCode) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:status IS NULL OR e.status = :status)")
    Page<Enterprise> searchEnterprises(
            @Param("keyword") String keyword,
            @Param("status") EnterpriseStatus status,
            Pageable pageable);

    @Query("SELECT COALESCE(MAX(e.id), 0) FROM Enterprise e")
    long findMaxId();
}
