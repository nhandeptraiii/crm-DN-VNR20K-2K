package vn.viettel.khdn.crm_DN_VNR20K_2K.service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Enterprise;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.EnterpriseContact;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ImportResultDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqEnterpriseCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqEnterpriseUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResEnterpriseDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseStatus;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseContactRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.ExcelExportHelper;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.ExcelUtils;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.error.IdInvalidException;

@Service
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseContactRepository enterpriseContactRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository,
            EnterpriseContactRepository enterpriseContactRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.enterpriseContactRepository = enterpriseContactRepository;
    }

    // --- Tạo mới ---
    @Transactional
    public ResEnterpriseDTO createEnterprise(ReqEnterpriseCreateDTO dto) throws IdInvalidException {
        // Kiểm tra MST đã tồn tại
        if (enterpriseRepository.existsByTaxCode(dto.getTaxCode())) {
            throw new IdInvalidException("Mã số thuế '" + dto.getTaxCode() + "' đã tồn tại trong hệ thống");
        }

        Enterprise enterprise = new Enterprise();
        enterprise.setName(dto.getName());
        enterprise.setTaxCode(dto.getTaxCode());
        enterprise.setIndustry(dto.getIndustry());
        enterprise.setEmployeeCount(dto.getEmployeeCount());
        enterprise.setAddress(dto.getAddress());
        enterprise.setWebsite(dto.getWebsite());
        enterprise.setEstablishedDate(dto.getEstablishedDate());
        enterprise.setPhone(dto.getPhone());
        enterprise.setNote(dto.getNote());

        Enterprise saved = enterpriseRepository.save(enterprise);

        // Thêm người đại diện nếu có
        if (dto.getContactFullName() != null && !dto.getContactFullName().isBlank()) {
            EnterpriseContact contact = new EnterpriseContact();
            contact.setEnterprise(saved);
            contact.setFullName(dto.getContactFullName());
            contact.setEmail(dto.getContactEmail());
            contact.setPhone(dto.getContactPhone());
            contact.setPosition(dto.getContactPosition());
            contact.setIsPrimary(true);
            enterpriseContactRepository.save(contact);
        }

        return toDTO(saved);
    }

    // --- Lấy danh sách (phân trang + tìm kiếm) ---
    public Page<ResEnterpriseDTO> searchEnterprises(String keyword, String status, String industryStr, Pageable pageable) {
        EnterpriseStatus enumStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                enumStatus = EnterpriseStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu status không hợp lệ, bỏ qua filter
            }
        }
        vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry enumIndustry = null;
        if (industryStr != null && !industryStr.isBlank()) {
            try {
                enumIndustry = vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry.valueOf(industryStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu industry không hợp lệ, bỏ qua filter
            }
        }
        Page<Enterprise> page = enterpriseRepository.searchEnterprises(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null,
                enumStatus,
                enumIndustry,
                pageable);
        return page.map(this::toDTO);
    }

    // --- Lấy theo ID ---
    public ResEnterpriseDTO getEnterpriseById(Long id) throws IdInvalidException {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + id));
        return toDTO(enterprise);
    }

    // --- Cập nhật ---
    public ResEnterpriseDTO updateEnterprise(Long id, ReqEnterpriseUpdateDTO dto) throws IdInvalidException {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + id));

        // Kiểm tra MST trùng (nếu có thay đổi)
        if (dto.getTaxCode() != null && !dto.getTaxCode().equals(enterprise.getTaxCode())) {
            if (enterpriseRepository.existsByTaxCode(dto.getTaxCode())) {
                throw new IdInvalidException("Mã số thuế '" + dto.getTaxCode() + "' đã tồn tại trong hệ thống");
            }
            enterprise.setTaxCode(dto.getTaxCode());
        }

        if (dto.getName() != null) enterprise.setName(dto.getName());
        if (dto.getIndustry() != null) enterprise.setIndustry(dto.getIndustry());
        if (dto.getEmployeeCount() != null) enterprise.setEmployeeCount(dto.getEmployeeCount());
        if (dto.getAddress() != null) enterprise.setAddress(dto.getAddress());
        if (dto.getWebsite() != null) enterprise.setWebsite(dto.getWebsite());
        if (dto.getEstablishedDate() != null) enterprise.setEstablishedDate(dto.getEstablishedDate());
        if (dto.getPhone() != null) enterprise.setPhone(dto.getPhone());
        if (dto.getStatus() != null) enterprise.setStatus(dto.getStatus());
        if (dto.getNote() != null) enterprise.setNote(dto.getNote());

        Enterprise updated = enterpriseRepository.save(enterprise);
        return toDTO(updated);
    }

    // --- Xóa ---
    public void deleteEnterprise(Long id) throws IdInvalidException {
        if (!enterpriseRepository.existsById(id)) {
            throw new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + id);
        }
        enterpriseRepository.deleteById(id);
    }


    // --- Tải file mẫu Export ---
    public ByteArrayInputStream getTemplateExcel() throws IOException {
        return ExcelExportHelper.createTemplateExcel();
    }

    // --- Export Excel ---
    public ByteArrayInputStream exportToExcel(String keyword, String status, String industryStr) throws IOException {
        EnterpriseStatus enumStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                enumStatus = EnterpriseStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry enumIndustry = null;
        if (industryStr != null && !industryStr.isBlank()) {
            try {
                enumIndustry = vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry.valueOf(industryStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }

        // Tạm thời lấy tất cả (không phân trang) để export report
        Page<Enterprise> page = enterpriseRepository.searchEnterprises(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null,
                enumStatus,
                enumIndustry,
                Pageable.unpaged()); // Hoặc có thể cần viết 1 hàm findAll() riêng trong repository nếu không dùng Pageable

        List<ResEnterpriseDTO> dtos = page.getContent().stream().map(this::toDTO).toList();
        return ExcelExportHelper.enterprisesToExcel(dtos);
    }

    // --- Import Excel ---
    public ImportResultDTO importFromExcel(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();

        // Validate format
        if (!ExcelUtils.hasExcelFormat(file)) {
            result.addError(0, "File không đúng định dạng Excel (.xlsx)");
            return result;
        }

        List<ReqEnterpriseCreateDTO> enterprises;
        try {
            enterprises = ExcelUtils.excelToEnterprises(file.getInputStream());
        } catch (Exception e) {
            result.addError(0, "Lỗi khi đọc nội dung file: " + e.getMessage());
            return result;
        }

        result.setTotalRows(enterprises.size());

        int rowNum = 2; // Dòng 1 là Header
        for (ReqEnterpriseCreateDTO dto : enterprises) {
            try {
                // Kiểm tra sơ bộ các cột bắt buộc
                if (dto.getName() == null || dto.getName().isBlank()) {
                    result.addError(rowNum, "Tên doanh nghiệp không được để trống");
                } else if (dto.getTaxCode() == null || dto.getTaxCode().isBlank()) {
                    result.addError(rowNum, "Mã số thuế không được để trống");
                } else {
                    // Gọi hàm createEnterprise hiện có (Hàm này đã có @Transactional và kiểm tra MST)
                    createEnterprise(dto);
                    result.incrementSuccess();
                }
            } catch (IdInvalidException idEx) {
                // Lỗi trùng mã số thuế từ createEnterprise()
                result.addError(rowNum, idEx.getMessage());
            } catch (Exception e) {
                // Các lỗi khác (ví dụ lưu database thất bại)
                result.addError(rowNum, "Lỗi hệ thống khi lưu dữ liệu: " + e.getMessage());
            }
            rowNum++;
        }

        return result;
    }


    // --- Helper: Entity → DTO ---
    private ResEnterpriseDTO toDTO(Enterprise e) {
        ResEnterpriseDTO dto = new ResEnterpriseDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setTaxCode(e.getTaxCode());
        dto.setIndustry(e.getIndustry());
        dto.setEmployeeCount(e.getEmployeeCount());
        dto.setAddress(e.getAddress());
        dto.setWebsite(e.getWebsite());
        dto.setEstablishedDate(e.getEstablishedDate());
        dto.setPhone(e.getPhone());
        dto.setStatus(e.getStatus());
        dto.setNote(e.getNote());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
