package vn.viettel.khdn.crm_DN_VNR20K_2K.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Role;
import vn.viettel.khdn.crm_DN_VNR20K_2K.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Map<String, String> body) {
        Role role = roleService.createRole(body.get("name"), body.get("description"));
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/assign")
    public ResponseEntity<Map<String, String>> assignRole(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, String> body) {
        roleService.assignRoleToUser(userId, body.get("role"));
        return ResponseEntity.ok(Map.of("message", "Gán role thành công"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/revoke")
    public ResponseEntity<Map<String, String>> revokeRole(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, String> body) {
        roleService.revokeRoleFromUser(userId, body.get("role"));
        return ResponseEntity.ok(Map.of("message", "Thu hồi role thành công"));
    }
}
