package Service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ServiceInitializer {
    private static ServiceInitializer instance;

    private final UserService userService;
    private final StoreService storeService;
    private final AdminService adminService;
    private final OrderService orderService;

    @Autowired
    public ServiceInitializer(@Lazy UserService userService, @Lazy StoreService storeService, @Lazy AdminService adminService, @Lazy OrderService orderService) {
        this.userService = userService;
        this.storeService = storeService;
        this.adminService = adminService;
        this.orderService = orderService;
        instance = this;
    }

    @PostConstruct
    private void init() {
        setupServiceInteractions();
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