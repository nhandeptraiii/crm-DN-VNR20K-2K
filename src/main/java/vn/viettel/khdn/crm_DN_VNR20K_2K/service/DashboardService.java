package vn.viettel.khdn.crm_DN_VNR20K_2K.service;

import java.util.List;
import org.springframework.stereotype.Service;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.DashboardDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.EmployeeInteractionDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.EnterpriseRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.InteractionRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.UserRepository;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.ViettelServiceRepository;

@Service
public class DashboardService {
    private final UserRepository userRepo;
    private final EnterpriseRepository enterpriseRepo;
    private final ViettelServiceRepository serviceRepo;
    private final InteractionRepository interactionRepo;

    public DashboardService(UserRepository userRepo, EnterpriseRepository enterpriseRepo,
            ViettelServiceRepository serviceRepo, InteractionRepository interactionRepo) {
        this.userRepo = userRepo;
        this.enterpriseRepo = enterpriseRepo;
        this.serviceRepo = serviceRepo;
        this.interactionRepo = interactionRepo;
    }

    public DashboardDTO getDashboard(int month, int year) {
        DashboardDTO dto = new DashboardDTO();

        dto.setTotalUsers(userRepo.count());
        dto.setTotalEnterprises(enterpriseRepo.count());
        dto.setTotalInteractedEnterprises(enterpriseRepo.countInteractedEnterprises());
        dto.setTotalServices(serviceRepo.count());
        dto.setActiveServices(serviceRepo.countActiveServices());
        List<EmployeeInteractionDTO> employeeStats = interactionRepo.countInteractionsByMonthAndYear(month, year);

        dto.setEmployeeStats(employeeStats);
        dto.setTotalActiveEmployees(employeeStats.size());
        return dto;
    }

    public List<EmployeeInteractionDTO> getEmployeeStatistics() {
        return interactionRepo.countInteractionsByEmployee();
    }
}
