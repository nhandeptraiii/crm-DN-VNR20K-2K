package vn.viettel.khdn.crm_DN_VNR20K_2K.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Enterprise;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.EnterpriseContact;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Interaction;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.User;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqInteractionCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqInteractionUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResInteractionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionResult;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionType;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseContactRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.InteractionRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.UserRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.error.IdInvalidException;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseContactRepository contactRepository;
    private final UserRepository userRepository;

    public InteractionService(InteractionRepository interactionRepository,
            EnterpriseRepository enterpriseRepository,
            EnterpriseContactRepository contactRepository,
            UserRepository userRepository) {
        this.interactionRepository = interactionRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() throws IdInvalidException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IdInvalidException("Không thể xác định người dùng hiện tại"));
    }

    public ResInteractionDTO createInteraction(ReqInteractionCreateDTO dto) throws Exception {
        Enterprise enterprise = enterpriseRepository.findById(dto.getEnterpriseId())
                .orElseThrow(
                        () -> new IdInvalidException("Không tìm thấy doanh nghiệp với ID: " + dto.getEnterpriseId()));

        EnterpriseContact contact = null;
        if (dto.getContactId() != null) {
            contact = contactRepository.findById(dto.getContactId())
                    .orElseThrow(
                            () -> new IdInvalidException("Không tìm thấy người liên hệ với ID: " + dto.getContactId()));

            if (!contact.getEnterprise().getId().equals(enterprise.getId())) {
                throw new IdInvalidException("Người liên hệ ID " + dto.getContactId() + " không thuộc Doanh nghiệp ID "
                        + dto.getEnterpriseId());
            }
        }

        User consultant = getCurrentUser();

        Interaction interaction = new Interaction();
        interaction.setEnterprise(enterprise);
        interaction.setContact(contact);
        interaction.setConsultant(consultant);
        interaction.setInteractionType(dto.getInteractionType());
        interaction.setResult(dto.getResult());
        interaction.setInteractionTime(dto.getInteractionTime());
        interaction.setLocation(dto.getLocation());
        interaction.setDescription(dto.getDescription());

        Interaction saved = interactionRepository.save(interaction);
        return toDTO(saved);
    }

    public Page<ResInteractionDTO> searchInteractions(Long enterpriseId, Long consultantId, String typeStr,
            String resultStr, Pageable pageable) throws Exception {
        InteractionType type = null;
        InteractionResult result = null;

        if (typeStr != null && !typeStr.isBlank()) {
            try {
                type = InteractionType.valueOf(typeStr.toUpperCase());
            } catch (Exception ignored) {
            }
        }
        if (resultStr != null && !resultStr.isBlank()) {
            try {
                result = InteractionResult.valueOf(resultStr.toUpperCase());
            } catch (Exception ignored) {
            }
        }

        User currentUser = getCurrentUser();
        Long filterConsultantId = consultantId;

        boolean isConsultant = currentUser.getRole() == vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RoleEnum.CONSULTANT;
        if (isConsultant) {
            filterConsultantId = currentUser.getId();
        }

        Page<Interaction> page = interactionRepository.searchInteractions(enterpriseId, filterConsultantId, type,
                result, pageable);
        return page.map(this::toDTO);
    }

    public ResInteractionDTO getInteractionById(Long id) throws Exception {
        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy nhật ký tương tác với ID: " + id));
        checkPermission(interaction);
        return toDTO(interaction);
    }

    public ResInteractionDTO updateInteraction(Long id, ReqInteractionUpdateDTO dto) throws Exception {
        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy nhật ký tương tác với ID: " + id));

        checkPermission(interaction);

        if (dto.getInteractionType() != null)
            interaction.setInteractionType(dto.getInteractionType());
        if (dto.getResult() != null)
            interaction.setResult(dto.getResult());
        if (dto.getInteractionTime() != null)
            interaction.setInteractionTime(dto.getInteractionTime());
        if (dto.getLocation() != null)
            interaction.setLocation(dto.getLocation());
        if (dto.getDescription() != null)
            interaction.setDescription(dto.getDescription());

        Interaction updated = interactionRepository.save(interaction);
        return toDTO(updated);
    }

    public void deleteInteraction(Long id) throws IdInvalidException {
        if (!interactionRepository.existsById(id)) {
            throw new IdInvalidException("Không tìm thấy nhật ký tương tác với ID: " + id);
        }
        interactionRepository.deleteById(id);
    }

    private void checkPermission(Interaction interaction) throws Exception {
        User currentUser = getCurrentUser();
        boolean isAdminOrManager = currentUser.getRole() == vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RoleEnum.ADMIN;

        if (!isAdminOrManager && !interaction.getConsultant().getId().equals(currentUser.getId())) {
            throw new Exception("Bạn không có quyền truy cập nhật ký tương tác của người khác!");
        }
    }

    private ResInteractionDTO toDTO(Interaction i) {
        ResInteractionDTO dto = new ResInteractionDTO();
        dto.setId(i.getId());
        dto.setEnterpriseId(i.getEnterprise().getId());
        dto.setEnterpriseName(i.getEnterprise().getName());

        if (i.getContact() != null) {
            dto.setContactId(i.getContact().getId());
            dto.setContactName(i.getContact().getFullName());
        }

        dto.setConsultantId(i.getConsultant().getId());
        dto.setConsultantName(i.getConsultant().getFullName());

        dto.setInteractionType(i.getInteractionType());
        dto.setResult(i.getResult());
        dto.setInteractionTime(i.getInteractionTime());
        dto.setLocation(i.getLocation());
        dto.setDescription(i.getDescription());

        // Chuyển chuỗi đường dẫn ảnh thành danh sách
        if (i.getPhotoPaths() != null && !i.getPhotoPaths().isBlank()) {
            dto.setPhotoPaths(java.util.Arrays.asList(i.getPhotoPaths().split(",")));
        } else {
            dto.setPhotoPaths(new java.util.ArrayList<>());
        }

        dto.setCreatedAt(i.getCreatedAt());
        dto.setUpdatedAt(i.getUpdatedAt());
        return dto;
    }
}
