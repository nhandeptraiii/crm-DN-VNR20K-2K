package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.UsageStatus;

@Getter
@Setter
public class ResUsageDTO {
    private Long id;

    // Enterprise Info
    private Long enterpriseId;
    private String enterpriseName;
    private String enterpriseCode;

    // Viettel Service Info
    private Long viettelServiceId;
    private String serviceCode;
    private String serviceName;

    // Usage Info
    private String contractNumber;
    private LocalDate startDate;
    private UsageStatus status;

    private Instant createdAt;
    private Instant updatedAt;
}
