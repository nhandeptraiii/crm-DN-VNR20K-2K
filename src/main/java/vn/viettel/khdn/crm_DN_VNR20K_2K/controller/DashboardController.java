package vn.viettel.khdn.crm_DN_VNR20K_2K.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.DashboardDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.DashboardDTO.AppointmentItemDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.DashboardDTO.RegionDistributionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.DashboardDTO.UncontactedEnterpriseDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.EmployeeInteractionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ─── CŨ: Giữ nguyên ───────────────────────────────────────────────────────

    /**
     * GET /dashboard?month=4&year=2026 Trả toàn bộ dashboard (bao gồm cả các field mới)
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardDTO> getDashboard(
            @RequestParam(name = "month", required = false, defaultValue = "3") int month,
            @RequestParam(name = "year", required = false, defaultValue = "2026") int year) {
        return ResponseEntity.ok(dashboardService.getDashboard(month, year));
    }

    /**
     * GET /dashboard/employee-stats Hiệu suất nhân viên tiếp xúc (biểu đồ bar chart)
     */
    @GetMapping("/employee-stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EmployeeInteractionDTO>> getEmployeeStats() {
        return ResponseEntity.ok(dashboardService.getEmployeeStatistics());
    }

    // ─── MỚI: Các endpoint tách riêng cho từng section ────────────────────────

    /**
     * GET /dashboard/region-distribution?month=4&year=2026 Phân bổ DN theo khu vực CTO / HUG / STG
     */
    @GetMapping("/region-distribution")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RegionDistributionDTO>> getRegionDistribution(
            @RequestParam(name = "month", required = false, defaultValue = "3") int month,
            @RequestParam(name = "year", required = false, defaultValue = "2026") int year) {
        return ResponseEntity.ok(dashboardService.getRegionDistribution(month, year));
    }

    /**
     * GET /dashboard/upcoming-appointments Lịch hẹn hôm nay và ngày mai (real-time)
     */
    @GetMapping("/upcoming-appointments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardDTO> getUpcomingAppointments() {
        return ResponseEntity.ok(dashboardService.getUpcomingAppointments());
    }

    /**
     * GET /dashboard/weekly-calendar Lịch hẹn 7 ngày trong tuần (Thứ 2 → Chủ nhật)
     */
    @GetMapping("/weekly-calendar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardDTO> getWeeklyCalendar() {
        return ResponseEntity.ok(dashboardService.getWeeklyCalendarOnly());
    }

    /**
     * GET /dashboard/uncontacted-warning Cảnh báo: DN 2000 và 20K chưa được tiếp xúc
     */
    @GetMapping("/uncontacted-warning")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardDTO> getUncontactedWarning() {
        return ResponseEntity.ok(dashboardService.getUncontactedWarningOnly());
    }
}
