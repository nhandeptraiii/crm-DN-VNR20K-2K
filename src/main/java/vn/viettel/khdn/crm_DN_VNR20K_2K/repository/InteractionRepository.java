package vn.viettel.khdn.crm_DN_VNR20K_2K.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Interaction;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionType;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionResult;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    @Query("SELECT i FROM Interaction i WHERE " +
            "(:enterpriseId IS NULL OR i.enterprise.id = :enterpriseId) " +
            "AND (:consultantId IS NULL OR i.consultant.id = :consultantId) " +
            "AND (:type IS NULL OR i.interactionType = :type) " +
            "AND (:result IS NULL OR i.result = :result)")
    Page<Interaction> searchInteractions(
            @Param("enterpriseId") Long enterpriseId,
            @Param("consultantId") Long consultantId,
            @Param("type") InteractionType type,
            @Param("result") InteractionResult result,
            Pageable pageable);
}
