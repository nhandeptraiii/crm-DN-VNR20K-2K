package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.UsageStatus;

@Getter
@Setter
public class ReqUsageCreateDTO {

    @NotNull(message = "ID Dịch vụ Viettel không được để trống")
    private Long viettelServiceId;

    @NotBlank(message = "Số hợp đồng không được để trống")
    @Size(max = 100, message = "Số hợp đồng tối đa 100 ký tự")
    private String contractNumber;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    private UsageStatus status;

    private Integer quantity;
}
