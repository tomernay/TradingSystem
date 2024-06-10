package Facades;

import Domain.Repo.AdminRepository;

public class AdminFacade {

    private AdminRepository adminRepository;
    public AdminFacade(){
        adminRepository = new AdminRepository();
    }

    public AdminRepository getAdminRepository() {
        return adminRepository;
    }
}
