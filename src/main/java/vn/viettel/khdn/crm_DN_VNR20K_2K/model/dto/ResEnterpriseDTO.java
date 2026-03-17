package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseStatus;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry;

@Getter
@Setter
public class ResEnterpriseDTO {
    private Long id;
    private String name;
    private String taxCode;
    private Industry industry;
    private Integer employeeCount;
    private String address;
    private String website;
    private LocalDate establishedDate;
    private String phone;
    private EnterpriseStatus status;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
}
