package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.AppointmentStatus;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionType;

@Getter
@Setter
public class ResAppointmentDTO {

    private Long id;

    // Thông tin doanh nghiệp
    private Long enterpriseId;
    private String enterpriseName;

    // Thông tin người liên hệ (nullable)
    private Long contactId;
    private String contactName;

    // Thông tin AM
    private Long consultantId;
    private String consultantName;
    private String consultantEmail;

    // Thông tin lịch hẹn
    private InteractionType appointmentType;
    private Instant scheduledTime;
    private String location;
    private String purpose;

    // Trạng thái
    private AppointmentStatus status;
    private Boolean reminder24hSent;
    private Boolean reminder1hSent;

    // ID của Interaction được tạo sau khi AM xác nhận (null nếu chưa xác nhận)
    // Dùng interactionId này để gọi GET /interactions/{id} lấy kết quả + ảnh đầy đủ
    private Long interactionId;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
}
