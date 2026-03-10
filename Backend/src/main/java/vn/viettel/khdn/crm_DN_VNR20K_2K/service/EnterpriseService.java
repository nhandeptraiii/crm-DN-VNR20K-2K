package vn.viettel.khdn.crm_DN_VNR20K_2K.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Enterprise;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqEnterpriseCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqEnterpriseUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResEnterpriseDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseStatus;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.error.IdInvalidException;

@Service
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
    }

    // --- Tạo mới ---
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

        // Auto-gen mã doanh nghiệp: DN-yyyy-xxxx
        enterprise.setEnterpriseCode(generateEnterpriseCode());

        Enterprise saved = enterpriseRepository.save(enterprise);
        return toDTO(saved);
    }

    // --- Lấy danh sách (phân trang + tìm kiếm) ---
    public Page<ResEnterpriseDTO> searchEnterprises(String keyword, String status, Pageable pageable) {
        EnterpriseStatus enumStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                enumStatus = EnterpriseStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu status không hợp lệ, bỏ qua filter
            }
        }
        Page<Enterprise> page = enterpriseRepository.searchEnterprises(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null,
                enumStatus,
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

    // --- Helper: Auto-gen mã DN ---
    private String generateEnterpriseCode() {
        int year = LocalDate.now().getYear();
        long nextSeq = enterpriseRepository.findMaxId() + 1;
        String code;
        do {
            code = String.format("DN-%d-%04d", year, nextSeq);
            nextSeq++;
        } while (enterpriseRepository.existsByEnterpriseCode(code));
        return code;
    }

    // --- Helper: Entity → DTO ---
    private ResEnterpriseDTO toDTO(Enterprise e) {
        ResEnterpriseDTO dto = new ResEnterpriseDTO();
        dto.setId(e.getId());
        dto.setEnterpriseCode(e.getEnterpriseCode());
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
