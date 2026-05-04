package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    // Trả về ISO 8601 (vd: "2026-04-16T02:00:00Z") để JavaScript có thể
    // parse bằng new Date() ở mọi nơi trong frontend
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Instant interactionTime;

    private String location;
    private String description;

    // Ảnh gặp mặt — được upload khi AM xác nhận lịch hẹn
    // Mỗi phần tử là đường dẫn public: "appointments/{id}/filename.jpg"
    private List<String> photoPaths;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Instant createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Instant updatedAt;
}
