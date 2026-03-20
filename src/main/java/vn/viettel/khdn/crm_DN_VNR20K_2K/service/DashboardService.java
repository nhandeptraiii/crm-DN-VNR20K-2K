package vn.viettel.khdn.crm_DN_VNR20K_2K.service;

import org.springframework.stereotype.Service;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.DashboardDTO;
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

    public DashboardDTO getDashboard() {
        DashboardDTO dto = new DashboardDTO();

        dto.setTotalUsers(userRepo.count());
        dto.setTotalEnterprises(enterpriseRepo.count());
        dto.setTotalInteractedEnterprises(enterpriseRepo.countInteractedEnterprises());
        dto.setTotalServices(serviceRepo.count());
        dto.setActiveServices(serviceRepo.countActiveServices());

        return dto;
    }
}
