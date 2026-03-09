package vn.viettel.khdn.crm_DN_VNR20K_2K.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Role;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.User;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.UserRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.service.RoleService;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository,
            RoleService roleService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleService.ensureRole("ADMIN", "Toàn quyền hệ thống");

        userRepository.findByEmail("admin@viettel.com").orElseGet(() -> {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmail("admin@viettel.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setStatus("ACTIVE");
            admin.getRoles().add(adminRole);
            return userRepository.save(admin);
        });
    }
}
