package vn.viettel.khdn.crm_DN_VNR20K_2K.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.viettel.khdn.crm_DN_VNR20K_2K.service.RoleService;

@Configuration
public class RoleInitializer {

    @Bean
    public ApplicationRunner rolesInitializer(RoleService roleService) {
        return args -> {
            roleService.ensureRole("ADMIN", "Toàn quyền hệ thống");
            roleService.ensureRole("MANAGER", "Quản lý, xem tất cả dữ liệu");
            roleService.ensureRole("CONSULTANT", "Nhân viên tư vấn giải pháp");
        };
    }
}
