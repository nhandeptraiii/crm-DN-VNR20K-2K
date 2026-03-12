package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqEnterpriseCreateDTO {

    @NotBlank(message = "Tên doanh nghiệp không được để trống")
    @Size(max = 255, message = "Tên doanh nghiệp tối đa 255 ký tự")
    private String name;

    @NotBlank(message = "Mã số thuế không được để trống")
    @Size(max = 20, message = "Mã số thuế tối đa 20 ký tự")
    private String taxCode;

    @Size(max = 100, message = "Ngành nghề tối đa 100 ký tự")
    private String industry;

    private Integer employeeCount;

    @Size(max = 500, message = "Địa chỉ tối đa 500 ký tự")
    private String address;

    @Size(max = 255, message = "Website tối đa 255 ký tự")
    private String website;

    private LocalDate establishedDate;

    @Size(max = 30, message = "SĐT tối đa 30 ký tự")
    private String phone;

    @Size(max = 1000, message = "Ghi chú tối đa 1000 ký tự")
    private String note;

    @Size(max = 150, message = "Họ tên người đại diện tối đa 150 ký tự")
    private String contactFullName;

    @Size(max = 120, message = "Email người đại diện tối đa 120 ký tự")
    private String contactEmail;

    @Size(max = 30, message = "SĐT người đại diện tối đa 30 ký tự")
    private String contactPhone;

    @Size(max = 100, message = "Chức vụ người đại diện tối đa 100 ký tự")
    private String contactPosition;
}
