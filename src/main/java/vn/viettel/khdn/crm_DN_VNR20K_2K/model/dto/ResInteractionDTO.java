package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.Instant;
import java.util.List;

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

    // Ảnh gặp mặt — được upload khi AM xác nhận lịch hẹn
    // Mỗi phần tử là đường dẫn public: "appointments/{id}/filename.jpg"
    private List<String> photoPaths;

    private Instant createdAt;
    private Instant updatedAt;
}
