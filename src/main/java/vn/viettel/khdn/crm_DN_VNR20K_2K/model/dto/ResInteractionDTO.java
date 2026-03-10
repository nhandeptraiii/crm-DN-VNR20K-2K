package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionResult;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionType;

@Getter
@Setter
public class ResInteractionDTO {
    private Long id;

    private Long enterpriseId;
    private String enterpriseName;

    private Long contactId;
    private String contactName;

    private Long consultantId;
    private String consultantName;

    private InteractionType interactionType;
    private InteractionResult result;
    private Instant interactionTime;
    private String location;
    private String description;

    private Instant createdAt;
    private Instant updatedAt;
}
