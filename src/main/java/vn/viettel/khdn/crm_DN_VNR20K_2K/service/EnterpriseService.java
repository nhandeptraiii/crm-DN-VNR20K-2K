package vn.viettel.khdn.crm_DN_VNR20K_2K.service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RegionEnum;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RoleEnum;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseContactRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.UserRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.ExcelExportHelper;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.ExcelUtils;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.error.IdInvalidException;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.User;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Commune;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.CommuneRepository;

@Service
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseContactRepository enterpriseContactRepository;
    private final UserRepository userRepository;
    private final CommuneRepository communeRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository,
            EnterpriseContactRepository enterpriseContactRepository,
            UserRepository userRepository,
            CommuneRepository communeRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.enterpriseContactRepository = enterpriseContactRepository;
        this.userRepository = userRepository;
        this.communeRepository = communeRepository;
    }

    // --- Tạo mới ---
    @Transactional
    public ResEnterpriseDTO createEnterprise(ReqEnterpriseCreateDTO dto) throws IdInvalidException {

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
        enterprise.setTaxAuthority(dto.getTaxAuthority());
        enterprise.setType(dto.getType());
        
        if (vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.HKD.equals(dto.getType())) {
            enterprise.setRevenueRange(dto.getRevenueRange());
        } else {
            enterprise.setRevenueRange(null);
        }
        
        if (dto.getCommuneId() != null) {
            Commune commune = communeRepository.findById(dto.getCommuneId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy Xã có ID: " + dto.getCommuneId()));
            enterprise.setCommune(commune);
        } else if (dto.getCommuneCode() != null && !dto.getCommuneCode().isBlank()) {
            Commune commune = communeRepository.findByCode(dto.getCommuneCode())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy Xã có mã: " + dto.getCommuneCode()));
            enterprise.setCommune(commune);
        } else if (dto.getCommuneName() != null && !dto.getCommuneName().isBlank()) {
            Commune commune = communeRepository.findFirstByName(dto.getCommuneName())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy Xã có tên: " + dto.getCommuneName()));
            enterprise.setCommune(commune);
        }

        if (dto.getOwnerId() != null) {
            User owner = new User();
            owner.setId(dto.getOwnerId());
            enterprise.setOwner(owner);
        }

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
    public Page<ResEnterpriseDTO> searchEnterprises(String keyword, String status,
            String industryStr, String regionStr, String typeStr, Pageable pageable) {
        EnterpriseStatus enumStatus = null;
        vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry enumIndustry = null;
        vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum enumType = null;
        RegionEnum requestedRegion = null;
        RegionEnum regionFilter = null;
        Long ownerIdFilter = null;
        
        if (typeStr != null && !typeStr.isBlank()) {
            try {
                enumType = vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.valueOf(typeStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }

        if (regionStr != null && !regionStr.isBlank()) {
            try {
                requestedRegion = RegionEnum.valueOf(regionStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }

        if (status != null && !status.isBlank()) {
            try {
                enumStatus = EnterpriseStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu status không hợp lệ, bỏ qua filter
            }
        }
        if (industryStr != null && !industryStr.isBlank()) {
            try {
                enumIndustry = vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry
                        .valueOf(industryStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu industry không hợp lệ, bỏ qua filter
            }
        }

        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // 2. Lấy thông tin User từ Database (để biết họ thuộc Region nào)
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Phân quyền lọc
        boolean hasCommunes = false;
        java.util.List<Long> communeIds = java.util.Collections.emptyList();
        boolean hasRestrictTypes = false;
        java.util.List<vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum> restrictTypes = java.util.Collections.emptyList();

        if (currentUser.getRole() == RoleEnum.ADMIN || currentUser.getRole() == RoleEnum.OPERATOR) {
            regionFilter = requestedRegion;
        } else if (currentUser.getRole() == RoleEnum.CONSULTANT) {
            regionFilter = requestedRegion; // Global visibility
            hasRestrictTypes = true;
            restrictTypes = java.util.List.of(
                vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR20K,
                vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR2000
            );
            if (enumType != null && !restrictTypes.contains(enumType)) {
                return Page.empty(pageable); // Trả rỗng nếu cố tình query sai loại quyền
            }
        } else if (currentUser.getRole() == RoleEnum.MANAGER) {
            regionFilter = currentUser.getRegion();
            if (regionFilter == null) {
                throw new RuntimeException("Tài khoản chưa được cấu hình Vùng (Region). Hãy liên hệ Admin.");
            }
            if (requestedRegion != null && requestedRegion != regionFilter) {
                return Page.empty(pageable);
            }
        } else if (currentUser.getRole() == RoleEnum.ACCOUNT_MANAGER) {
            regionFilter = requestedRegion;
            hasCommunes = true;
            if (currentUser.getManagedCommunes() == null || currentUser.getManagedCommunes().isEmpty()) {
                communeIds = java.util.List.of(-1L);
            } else {
                communeIds = currentUser.getManagedCommunes().stream()
                        .map(vn.viettel.khdn.crm_DN_VNR20K_2K.model.Commune::getId)
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        Page<Enterprise> page = enterpriseRepository.searchEnterprises(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null, enumStatus,
                enumIndustry, regionFilter, enumType, ownerIdFilter, 
                hasCommunes, communeIds, hasRestrictTypes, restrictTypes, pageable);
        return page.map(this::toDTO);
    }

    // --- Lấy theo ID ---
    public ResEnterpriseDTO getEnterpriseById(Long id) throws IdInvalidException {
        Enterprise enterprise = enterpriseRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + id));

        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole() == RoleEnum.ADMIN) {
            return toDTO(enterprise);
        }
        if (currentUser.getRole() != RoleEnum.ADMIN && currentUser.getRole() != RoleEnum.OPERATOR) {
            if (currentUser.getRole() == RoleEnum.MANAGER) {
                if (enterprise.getRegion() != currentUser.getRegion()) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN,
                            "Bạn không có quyền quản lý doanh nghiệp nằm ngoài Tỉnh/Vùng của mình.");
                }
            } else if (currentUser.getRole() == RoleEnum.ACCOUNT_MANAGER) {
                boolean inCommune = enterprise.getCommune() != null &&
                        currentUser.getManagedCommunes().stream()
                                .anyMatch(c -> c.getId().equals(enterprise.getCommune().getId()));
                if (!inCommune) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN,
                            "Bạn không có quyền xem doanh nghiệp nằm ngoài Xã mà bạn phụ trách.");
                }
            } else if (currentUser.getRole() == RoleEnum.CONSULTANT) {
                if (enterprise.getType() != vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR2000 &&
                    enterprise.getType() != vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR20K) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN,
                            "Tư vấn viên chỉ được phép xem các doanh nghiệp thuộc mảng VNR2000 hoặc VNR20K.");
                }
            }
        }

        return toDTO(enterprise);
    }

    // --- Cập nhật ---
    public ResEnterpriseDTO updateEnterprise(Long id, ReqEnterpriseUpdateDTO dto)
            throws IdInvalidException {
        Enterprise enterprise = enterpriseRepository.findById(id).orElseThrow(
                () -> new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + id));
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRole() != RoleEnum.ADMIN && currentUser.getRole() != RoleEnum.OPERATOR) {
            if (currentUser.getRole() == RoleEnum.MANAGER) {
                if (enterprise.getRegion() != currentUser.getRegion()) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN,
                            "Bạn không có quyền quản lý doanh nghiệp nằm ngoài Tỉnh/Vùng của mình.");
                }
            } else if (currentUser.getRole() == RoleEnum.ACCOUNT_MANAGER) {
                boolean inCommune = enterprise.getCommune() != null &&
                        currentUser.getManagedCommunes().stream()
                                .anyMatch(c -> c.getId().equals(enterprise.getCommune().getId()));
                if (!inCommune) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN,
                            "Bạn không có quyền sửa doanh nghiệp nằm ngoài Xã mà bạn phụ trách.");
                }
            } else if (currentUser.getRole() == RoleEnum.CONSULTANT) {
                if (enterprise.getType() != vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR2000 &&
                    enterprise.getType() != vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR20K) {
                    throw new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN,
                            "Tư vấn viên chỉ được phép sửa các doanh nghiệp thuộc mảng VNR2000 hoặc VNR20K.");
                }
            }
        }

        // Cập nhật MST (không kiểm tra trùng nữa)
        if (dto.getTaxCode() != null && !dto.getTaxCode().equals(enterprise.getTaxCode())) {
            enterprise.setTaxCode(dto.getTaxCode());
        }

        if (dto.getName() != null)
            enterprise.setName(dto.getName());
        if (dto.getIndustry() != null)
            enterprise.setIndustry(dto.getIndustry());
        if (dto.getEmployeeCount() != null)
            enterprise.setEmployeeCount(dto.getEmployeeCount());
        if (dto.getAddress() != null)
            enterprise.setAddress(dto.getAddress());
        if (dto.getWebsite() != null)
            enterprise.setWebsite(dto.getWebsite());
        if (dto.getEstablishedDate() != null)
            enterprise.setEstablishedDate(dto.getEstablishedDate());
        if (dto.getPhone() != null)
            enterprise.setPhone(dto.getPhone());
        if (dto.getStatus() != null)
            enterprise.setStatus(dto.getStatus());
        if (dto.getNote() != null)
            enterprise.setNote(dto.getNote());
        if (dto.getTaxAuthority() != null)
            enterprise.setTaxAuthority(dto.getTaxAuthority());
            
        if (dto.getType() != null) {
            enterprise.setType(dto.getType());
            if (!vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.HKD.equals(dto.getType())) {
                enterprise.setRevenueRange(null);
            } else if (dto.getRevenueRange() != null) {
                enterprise.setRevenueRange(dto.getRevenueRange());
            }
        } else {
            if (!vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.HKD.equals(enterprise.getType())) {
                enterprise.setRevenueRange(null);
            } else if (dto.getRevenueRange() != null) {
                enterprise.setRevenueRange(dto.getRevenueRange());
            }
        }
        if (dto.getOwnerId() != null) {
            User owner = new User();
            owner.setId(dto.getOwnerId());
            enterprise.setOwner(owner);
        }
        
        if (dto.getCommuneId() != null) {
            Commune commune = communeRepository.findById(dto.getCommuneId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy Xã có ID: " + dto.getCommuneId()));
            enterprise.setCommune(commune);
        }

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
    public ByteArrayInputStream exportToExcel(String keyword, String status, String industryStr, String regionStr, String typeStr)
            throws IOException {
        EnterpriseStatus enumStatus = null;
        vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum enumType = null;
        RegionEnum requestedRegion = null;
        RegionEnum regionFilter = null;
        Long ownerIdFilter = null;
        
        if (typeStr != null && !typeStr.isBlank()) {
            try {
                enumType = vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.valueOf(typeStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }

        if (regionStr != null && !regionStr.isBlank()) {
            try {
                requestedRegion = RegionEnum.valueOf(regionStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }

        if (status != null && !status.isBlank()) {
            try {
                enumStatus = EnterpriseStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry enumIndustry = null;
        if (industryStr != null && !industryStr.isBlank()) {
            try {
                enumIndustry = vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry
                        .valueOf(industryStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }


        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 6. Phân quyền lọc (Code lặp lại từ search)
        boolean hasCommunes = false;
        java.util.List<Long> communeIds = java.util.Collections.emptyList();
        boolean hasRestrictTypes = false;
        java.util.List<vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum> restrictTypes = java.util.Collections.emptyList();

        if (currentUser.getRole() == RoleEnum.ADMIN || currentUser.getRole() == RoleEnum.OPERATOR) {
            regionFilter = requestedRegion;
        } else if (currentUser.getRole() == RoleEnum.CONSULTANT) {
            regionFilter = requestedRegion;
            hasRestrictTypes = true;
            restrictTypes = java.util.List.of(
                vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR20K,
                vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum.VNR2000
            );
            if (enumType != null && !restrictTypes.contains(enumType)) {
                return ExcelExportHelper.enterprisesToExcel(java.util.Collections.emptyList());
            }
            ownerIdFilter = currentUser.getId();
        } else if (currentUser.getRole() == RoleEnum.MANAGER) {
            regionFilter = currentUser.getRegion();
            if (regionFilter == null) {
                throw new RuntimeException("Tài khoản chưa được cấu hình Vùng (Region). Hãy liên hệ Admin.");
            }
            if (requestedRegion != null && requestedRegion != regionFilter) {
                return ExcelExportHelper.enterprisesToExcel(java.util.Collections.emptyList());
            }
        } else if (currentUser.getRole() == RoleEnum.ACCOUNT_MANAGER) {
            regionFilter = requestedRegion;
            hasCommunes = true;
            if (currentUser.getManagedCommunes() == null || currentUser.getManagedCommunes().isEmpty()) {
                communeIds = java.util.List.of(-1L);
            } else {
                communeIds = currentUser.getManagedCommunes().stream()
                        .map(vn.viettel.khdn.crm_DN_VNR20K_2K.model.Commune::getId)
                        .collect(java.util.stream.Collectors.toList());
            }
        }

        // Tạm thời lấy tất cả (không phân trang) để export report
        Page<Enterprise> page = enterpriseRepository.searchEnterprises(
                keyword != null && !keyword.isBlank() ? keyword.trim() : null, enumStatus,
                enumIndustry, regionFilter, enumType, ownerIdFilter, 
                hasCommunes, communeIds, hasRestrictTypes, restrictTypes, Pageable.unpaged());

        List<ResEnterpriseDTO> dtos = page.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        
        // Load danh sách người đại diện chính
        List<Long> enterpriseIds = dtos.stream().map(ResEnterpriseDTO::getId).collect(Collectors.toList());
        if (!enterpriseIds.isEmpty()) {
            List<EnterpriseContact> primaryContacts = enterpriseContactRepository.findByEnterpriseIdInAndIsPrimaryTrue(enterpriseIds);
            Map<Long, EnterpriseContact> contactMap = primaryContacts.stream()
                .collect(Collectors.toMap(c -> c.getEnterprise().getId(), c -> c, (c1, c2) -> c1)); // Tránh duplicate key nếu có lỗi data

            for (ResEnterpriseDTO dto : dtos) {
                EnterpriseContact contact = contactMap.get(dto.getId());
                if (contact != null) {
                    dto.setContactFullName(contact.getFullName());
                    dto.setContactEmail(contact.getEmail());
                    dto.setContactPhone(contact.getPhone());
                    dto.setContactPosition(contact.getPosition());
                }
            }
        }

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
                    // Gọi hàm createEnterprise hiện có (Hàm này đã có @Transactional và kiểm tra
                    // MST)
                    createEnterprise(dto);
                    result.incrementSuccess();
                }
            } catch (IdInvalidException idEx) {
                // Lỗi ID không hợp lệ từ createEnterprise()
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
        dto.setTaxAuthority(e.getTaxAuthority());
        dto.setRevenueRange(e.getRevenueRange());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        dto.setRegion(e.getRegion());
        dto.setType(e.getType());
        dto.setOwnerId(e.getOwner() != null ? e.getOwner().getId() : null);
        dto.setCommuneCode(e.getCommune() != null ? e.getCommune().getCode() : null);
        dto.setCommuneName(e.getCommune() != null ? e.getCommune().getName() : null);
        return dto;
    }
}
