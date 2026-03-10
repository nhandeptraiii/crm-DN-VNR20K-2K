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
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqEnterpriseCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqEnterpriseUpdateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResEnterpriseDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.service.EnterpriseService;
import vn.viettel.khdn.crm_DN_VNR20K_2K.util.error.IdInvalidException;

@RestController
@RequestMapping("/enterprises")
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    public EnterpriseController(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ResEnterpriseDTO> create(@Valid @RequestBody ReqEnterpriseCreateDTO dto)
            throws IdInvalidException {
        ResEnterpriseDTO created = enterpriseService.createEnterprise(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<ResEnterpriseDTO>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Order.desc("createdAt")));
        Page<ResEnterpriseDTO> result = enterpriseService.searchEnterprises(keyword, status, pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ResEnterpriseDTO> getById(@PathVariable("id") Long id) throws IdInvalidException {
        return ResponseEntity.ok(enterpriseService.getEnterpriseById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ResEnterpriseDTO> update(@PathVariable("id") Long id,
            @Valid @RequestBody ReqEnterpriseUpdateDTO dto) throws IdInvalidException {
        return ResponseEntity.ok(enterpriseService.updateEnterprise(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws IdInvalidException {
        enterpriseService.deleteEnterprise(id);
        return ResponseEntity.noContent().build();
    }
}
