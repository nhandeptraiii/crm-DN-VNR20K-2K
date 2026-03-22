package vn.viettel.khdn.crm_DN_VNR20K_2K.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.DashboardDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.EmployeeInteractionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardDTO> getDashboard(
            @RequestParam(name = "month", required = false, defaultValue = "3") int month,
            @RequestParam(name = "year", required = false, defaultValue = "2026") int year) {
        return ResponseEntity.ok(dashboardService.getDashboard(month, year));
    }

    @GetMapping("/employee-stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EmployeeInteractionDTO>> getEmployeeStats() {
        return ResponseEntity.ok(dashboardService.getEmployeeStatistics());
    }
}
