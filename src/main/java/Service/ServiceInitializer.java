package Service;

import Domain.Externals.InitFile.Configuration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ServiceInitializer {
    private static ServiceInitializer instance = null;

    @Autowired
    private @Lazy UserService userService;
    @Autowired
    private @Lazy StoreService storeService;
    @Autowired
    private @Lazy AdminService adminService;
    @Autowired
    private @Lazy OrderService orderService;

    @PostConstruct
    private void init() {
        setupServiceInteractions();
    }

    public ServiceInitializer(){
    }

    public static void reset() {
        instance = null;
    }

    public static ServiceInitializer getInstance() {
        return instance;
    }

    private void setupServiceInteractions() {
        userService.setStoreService(storeService);
        userService.setAdminService(adminService);
        storeService.setUserService(userService);
        storeService.setAdminService(adminService);
        adminService.setUserService(userService);
        adminService.setStoreService(storeService);
        adminService.setOrderService(orderService);
        orderService.setUserService(userService);
    }

    // Getters for services
    public UserService getUserService() {
        return userService;
    }

    public StoreService getStoreService() {
        return storeService;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public OrderService getOrderService() {
        return orderService;
    }
}
