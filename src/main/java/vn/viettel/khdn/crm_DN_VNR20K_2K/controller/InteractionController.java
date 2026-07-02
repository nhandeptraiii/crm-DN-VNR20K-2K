package vn.viettel.khdn.crm_DN_VNR20K_2K.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.error.IdInvalidException;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqInteractionCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqInteractionUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqUsageCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResInteractionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionResult;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.InteractionType;
import vn.viettel.khdn.crm_DN_VNR20K_2K.service.InteractionService;

@RestController
@RequestMapping("/interactions")
public class InteractionController {

    private final InteractionService interactionService;
    private final ObjectMapper objectMapper;

    public InteractionController(InteractionService interactionService, ObjectMapper objectMapper) {
        this.interactionService = interactionService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ResInteractionDTO> create(@Valid @RequestBody ReqInteractionCreateDTO dto) throws Exception {
        ResInteractionDTO created = interactionService.createInteraction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResInteractionDTO> createWithPhotos(
            @RequestParam("enterpriseId") Long enterpriseId,
            @RequestParam(value = "contactId", required = false) Long contactId,
            @RequestParam(value = "consultantId", required = false) Long consultantId,
            @RequestParam("interactionType") String interactionTypeStr,
            @RequestParam(value = "result", required = false) String resultStr,
            @RequestParam("interactionTime") String interactionTimeStr,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "newUsages", required = false) String newUsagesJson,
            @RequestPart(value = "photos", required = false) MultipartFile[] photos) throws Exception {

        ReqInteractionCreateDTO dto = new ReqInteractionCreateDTO();
        dto.setEnterpriseId(enterpriseId);
        dto.setContactId(contactId);
        dto.setConsultantId(consultantId);

        try {
            dto.setInteractionType(InteractionType.valueOf(interactionTypeStr.trim().toUpperCase()));
        } catch (Exception e) {
            throw new IdInvalidException("Loại tương tác không hợp lệ.");
        }

        if (resultStr != null && !resultStr.isBlank()) {
            try {
                dto.setResult(InteractionResult.valueOf(resultStr.trim().toUpperCase()));
            } catch (Exception e) {
                throw new IdInvalidException("Kết quả tương tác không hợp lệ.");
            }
        }

        if (interactionTimeStr != null && !interactionTimeStr.isBlank()) {
            try {
                dto.setInteractionTime(Instant.parse(interactionTimeStr.trim()));
            } catch (Exception e) {
                try {
                    dto.setInteractionTime(Instant.ofEpochMilli(Long.parseLong(interactionTimeStr.trim())));
                } catch (Exception ex) {
                    throw new IdInvalidException("Thời gian tương tác không hợp lệ.");
                }
            }
        }

        dto.setLocation(location);
        dto.setDescription(description);

        if (newUsagesJson != null && !newUsagesJson.isBlank()) {
            try {
                com.fasterxml.jackson.core.type.TypeReference<List<ReqUsageCreateDTO>> typeRef = new com.fasterxml.jackson.core.type.TypeReference<>() {};
                List<ReqUsageCreateDTO> usages = objectMapper.readValue(newUsagesJson, typeRef);
                dto.setNewUsages(usages);
            } catch (Exception e) {
                throw new IdInvalidException("Định dạng dữ liệu dịch vụ đã ký không hợp lệ: " + e.getMessage());
            }
        }

        ResInteractionDTO created = interactionService.createInteraction(dto, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<ResInteractionDTO>> list(
            @RequestParam(value = "enterpriseId", required = false) Long enterpriseId,
            @RequestParam(value = "consultantId", required = false) Long consultantId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "result", required = false) String result,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

        int safeSize = Math.min(Math.max(size, 1), 200);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Order.desc("interactionTime")));

        Page<ResInteractionDTO> pageResult = interactionService.searchInteractions(enterpriseId, consultantId, type,
                result, pageable);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * GET /interactions/enterprise-summary?page=0&size=10
     * Trả về danh sách doanh nghiệp kèm số lần tiếp xúc và ngày tiếp xúc gần nhất.
     * Mỗi dòng = 1 doanh nghiệp. Dùng cho trang "Quản lý tiếp xúc" phía FE.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/enterprise-summary")
    public ResponseEntity<Page<vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResEnterpriseInteractionSummaryDTO>> listByEnterprise(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize);

        return ResponseEntity.ok(interactionService.getEnterpriseInteractionSummary(pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ResInteractionDTO> getById(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok(interactionService.getInteractionById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ResInteractionDTO> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ReqInteractionUpdateDTO dto) throws Exception {
        return ResponseEntity.ok(interactionService.updateInteraction(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws Exception {
        interactionService.deleteInteraction(id);
        return ResponseEntity.noContent().build();
    }
}
