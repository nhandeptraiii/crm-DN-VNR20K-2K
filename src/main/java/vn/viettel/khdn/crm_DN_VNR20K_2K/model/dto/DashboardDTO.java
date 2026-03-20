package vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto;

public class DashboardDTO {
    private long totalUsers;
    private long totalEnterprises;
    private long totalServices;
    private long activeServices;
    private long totalInteractedEnterprises;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalEnterprises() {
        return totalEnterprises;
    }

    public void setTotalEnterprises(long totalEnterprises) {
        this.totalEnterprises = totalEnterprises;
    }

    public long getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(long totalServices) {
        this.totalServices = totalServices;
    }

    public long getActiveServices() {
        return activeServices;
    }

    public void setActiveServices(long activeServices) {
        this.activeServices = activeServices;
    }

    public long getTotalInteractedEnterprises() {
        return totalInteractedEnterprises;
    }

    public void setTotalInteractedEnterprises(long totalInteractedEnterprises) {
        this.totalInteractedEnterprises = totalInteractedEnterprises;
    }
}
