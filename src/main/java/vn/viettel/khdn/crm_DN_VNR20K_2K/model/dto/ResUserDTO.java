package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RoleEnum;

@Getter
@Setter
public class ResUserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String status;
    private RoleEnum role;
    private Instant createdAt;
    private Instant updatedAt;
}
