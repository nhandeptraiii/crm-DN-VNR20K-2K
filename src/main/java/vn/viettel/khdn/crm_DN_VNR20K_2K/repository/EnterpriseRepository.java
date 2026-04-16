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
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RegionEnum;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {


        @Query("SELECT e FROM Enterprise e WHERE "
                        + "(:keyword IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
                        + "OR LOWER(e.taxCode) LIKE LOWER(CONCAT('%', :keyword, '%')) "
                        + "OR LOWER(e.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                        + "AND (:status IS NULL OR e.status = :status) "
                        + "AND (:industry IS NULL OR e.industry = :industry) "
                        + "AND (:region IS NULL OR e.commune.cluster.region = :region) "
                        + "AND (:type IS NULL OR e.type = :type) "
                        + "AND (:ownerId IS NULL OR e.owner.id = :ownerId)")
        Page<Enterprise> searchEnterprises(@Param("keyword") String keyword,
                        @Param("status") EnterpriseStatus status,
                        @Param("industry") vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry industry,
                        @Param("region") RegionEnum region, @Param("type") EnterpriseTypeEnum type,
                        @Param("ownerId") Long ownerId,
                        Pageable pageable);

        @Query("SELECT COALESCE(MAX(e.id), 0) FROM Enterprise e")
        long findMaxId();

        @Query("SELECT COUNT(DISTINCT i.enterprise.id) FROM Interaction i")
        long countInteractedEnterprises();

        @Query("SELECT e FROM Enterprise e WHERE " + "(:region IS NULL OR e.commune.cluster.region = :region) AND "
                        + "(:status IS NULL OR e.status = :status) AND "
                        + "(:industry IS NULL OR e.industry = :industry) AND "
                        + "(:kw IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :kw, '%')) OR e.taxCode LIKE CONCAT('%', :kw, '%'))")
        Page<Enterprise> searchEnterprisesWithRegion(String kw, EnterpriseStatus status,
                        Industry industry, RegionEnum region, Pageable pageable);

        @Query("SELECT e FROM Enterprise e " +
               "WHERE :roleName IN ('ADMIN', 'OPERATOR') " +
               "   OR (:roleName = 'MANAGER' AND :regionName = 'DA' AND e.type IN :daTypes) " +
               "   OR (:roleName = 'MANAGER' AND :regionName != 'DA' AND e.type IN :smeTypes AND e.commune.cluster.region = :managedRegion) " +
               "   OR (:roleName = 'CONSULTANT' AND e.type IN :smeTypes AND e.commune IN :managedCommunes)")
        Page<Enterprise> findAllByDataVisibility(
            @Param("roleName") String roleName,
            @Param("regionName") String regionName,
            @Param("managedRegion") RegionEnum managedRegion,
            @Param("daTypes") java.util.Collection<EnterpriseTypeEnum> daTypes,
            @Param("smeTypes") java.util.Collection<EnterpriseTypeEnum> smeTypes,
            @Param("managedCommunes") java.util.Collection<vn.viettel.khdn.crm_DN_VNR20K_2K.model.Commune> managedCommunes,
            Pageable pageable
        );
}
