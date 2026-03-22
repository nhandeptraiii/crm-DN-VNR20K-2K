package vn.viettel.khdn.crm_DN_VNR20K_2K.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Interaction;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.EmployeeInteractionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionType;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionResult;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

        @Query("SELECT i FROM Interaction i WHERE "
                        + "(:enterpriseId IS NULL OR i.enterprise.id = :enterpriseId) "
                        + "AND (:consultantId IS NULL OR i.consultant.id = :consultantId) "
                        + "AND (:type IS NULL OR i.interactionType = :type) "
                        + "AND (:result IS NULL OR i.result = :result)")
        Page<Interaction> searchInteractions(@Param("enterpriseId") Long enterpriseId,
                        @Param("consultantId") Long consultantId,
                        @Param("type") InteractionType type,
                        @Param("result") InteractionResult result, Pageable pageable);

        @Query("SELECT new vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.EmployeeInteractionDTO(c.fullName, COUNT(i)) "
                        + "FROM Interaction i JOIN i.consultant c "
                        + "WHERE MONTH(i.createdAt) = MONTH(CURRENT_DATE) "
                        + "AND YEAR(i.createdAt) = YEAR(CURRENT_DATE) "
                        + "GROUP BY c.id, c.fullName")
        List<EmployeeInteractionDTO> countInteractionsByEmployeeThisMonth();

        @Query("SELECT new vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.EmployeeInteractionDTO(c.fullName, COUNT(i)) "
                        + "FROM Interaction i JOIN i.consultant c " + "GROUP BY c.id, c.fullName")
        List<EmployeeInteractionDTO> countInteractionsByEmployee();

        @Query("SELECT new vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.EmployeeInteractionDTO(c.fullName, COUNT(i)) "
                        + "FROM Interaction i JOIN i.consultant c "
                        + "WHERE MONTH(i.createdAt) = :month " + "AND YEAR(i.createdAt) = :year "
                        + "GROUP BY c.id, c.fullName")
        List<EmployeeInteractionDTO> countInteractionsByMonthAndYear(@Param("month") int month,
                        @Param("year") int year);
}
