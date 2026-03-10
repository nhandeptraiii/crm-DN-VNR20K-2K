package vn.viettel.khdn.crm_DN_VNR20K_2K.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqInteractionCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqInteractionUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResInteractionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.service.InteractionService;

@RestController
@RequestMapping("/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ResInteractionDTO> create(@Valid @RequestBody ReqInteractionCreateDTO dto) throws Exception {
        ResInteractionDTO created = interactionService.createInteraction(dto);
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

        int safeSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Order.desc("interactionTime")));

        Page<ResInteractionDTO> pageResult = interactionService.searchInteractions(enterpriseId, consultantId, type,
                result, pageable);
        return ResponseEntity.ok(pageResult);
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
