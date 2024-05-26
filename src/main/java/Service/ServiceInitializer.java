package Service;

public class ServiceInitializer {
    private UserService userService;
    private StoreService storeService;
    private AdminService adminService;
    private PaymentService paymentService;

    public ServiceInitializer() {
        userService = new UserService();
        storeService = new StoreService();
        adminService = new AdminService();
        paymentService = new PaymentService();
        userService.setStoreService(storeService);
        storeService.setUserService(userService);
        adminService.setUserService(userService);
        adminService.setStoreService(storeService);
        paymentService.setUserService(userService);
    }

    public UserService getUserService() {
        return userService;
    }

    public StoreService getStoreService() {
        return storeService;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }
}
