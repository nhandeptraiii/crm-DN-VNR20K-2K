package vn.viettel.khdn.crm_DN_VNR20K_2K.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Enterprise;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.EnterpriseServiceUsage;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.ViettelService;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqUsageCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqUsageUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResUsageDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.UsageStatus;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseServiceUsageRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.ViettelServiceRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.error.IdInvalidException;

@Service
public class EnterpriseServiceUsageService {

    private final EnterpriseServiceUsageRepository usageRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final ViettelServiceRepository viettelServiceRepository;

    public EnterpriseServiceUsageService(EnterpriseServiceUsageRepository usageRepository,
            EnterpriseRepository enterpriseRepository,
            ViettelServiceRepository viettelServiceRepository) {
        this.usageRepository = usageRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.viettelServiceRepository = viettelServiceRepository;
    }

    public ResUsageDTO createUsage(Long enterpriseId, ReqUsageCreateDTO dto) throws IdInvalidException {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + enterpriseId));

        ViettelService viettelService = viettelServiceRepository.findById(dto.getViettelServiceId())
                .orElseThrow(() -> new IdInvalidException(
                        "Không tìm thấy dịch vụ Viettel với ID: " + dto.getViettelServiceId()));

        EnterpriseServiceUsage usage = new EnterpriseServiceUsage();
        usage.setEnterprise(enterprise);
        usage.setViettelService(viettelService);
        usage.setContractNumber(dto.getContractNumber());
        usage.setStartDate(dto.getStartDate());
        usage.setStatus(dto.getStatus() != null ? dto.getStatus() : UsageStatus.ACTIVE);

        EnterpriseServiceUsage saved = usageRepository.save(usage);
        return toDTO(saved);
    }

    public Page<ResUsageDTO> getUsagesByEnterprise(Long enterpriseId, String status, Pageable pageable)
            throws IdInvalidException {
        if (!enterpriseRepository.existsById(enterpriseId)) {
            throw new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + enterpriseId);
        }

        UsageStatus enumStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                enumStatus = UsageStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        Page<EnterpriseServiceUsage> page = usageRepository.findByEnterpriseIdAndStatus(enterpriseId, enumStatus,
                pageable);
        return page.map(this::toDTO);
    }

    public ResUsageDTO getUsageById(Long enterpriseId, Long usageId) throws IdInvalidException {
        EnterpriseServiceUsage usage = findAndValidate(enterpriseId, usageId);
        return toDTO(usage);
    }

    public ResUsageDTO updateUsage(Long enterpriseId, Long usageId, ReqUsageUpdateDTO dto) throws IdInvalidException {
        EnterpriseServiceUsage usage = findAndValidate(enterpriseId, usageId);

        if (dto.getContractNumber() != null)
            usage.setContractNumber(dto.getContractNumber());
        if (dto.getStartDate() != null)
            usage.setStartDate(dto.getStartDate());
        if (dto.getStatus() != null)
            usage.setStatus(dto.getStatus());

        EnterpriseServiceUsage updated = usageRepository.save(usage);
        return toDTO(updated);
    }

    public void deleteUsage(Long enterpriseId, Long usageId) throws IdInvalidException {
        EnterpriseServiceUsage usage = findAndValidate(enterpriseId, usageId);
        usageRepository.delete(usage);
    }

    private EnterpriseServiceUsage findAndValidate(Long enterpriseId, Long usageId) throws IdInvalidException {
        if (!enterpriseRepository.existsById(enterpriseId)) {
            throw new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + enterpriseId);
        }
        EnterpriseServiceUsage usage = usageRepository.findById(usageId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy gói dịch vụ với ID: " + usageId));
        if (!usage.getEnterprise().getId().equals(enterpriseId)) {
            throw new IdInvalidException("Gói dịch vụ ID " + usageId + " không thuộc doanh nghiệp ID " + enterpriseId);
        }
        return usage;
    }

    private ResUsageDTO toDTO(EnterpriseServiceUsage u) {
        ResUsageDTO dto = new ResUsageDTO();
        dto.setId(u.getId());
        dto.setEnterpriseId(u.getEnterprise().getId());
        dto.setEnterpriseName(u.getEnterprise().getName());
        dto.setEnterpriseCode(u.getEnterprise().getEnterpriseCode());
        dto.setViettelServiceId(u.getViettelService().getId());
        dto.setServiceCode(u.getViettelService().getServiceCode());
        dto.setServiceName(u.getViettelService().getServiceName());
        dto.setContractNumber(u.getContractNumber());
        dto.setStartDate(u.getStartDate());
        dto.setStatus(u.getStatus());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }
}
