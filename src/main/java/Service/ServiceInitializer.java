package Service;

import Domain.Externals.Payment.DefaultPaymentGateway;
import Domain.Externals.Suppliers.DefaultSupplySystem;
import org.springframework.stereotype.Component;

@Component
public class ServiceInitializer {
    private static ServiceInitializer instance = null;

    private UserService userService;
    private StoreService storeService;
    private AdminService adminService;
//    private PaymentService paymentService;
    private OrderService orderService;

    private ServiceInitializer() {
        userService = new UserService();
        storeService = new StoreService();
        adminService = new AdminService();
        orderService = new OrderService(new DefaultPaymentGateway(), new DefaultSupplySystem());
        userService.setStoreService(storeService);
        userService.setAdminService(adminService);
        storeService.setUserService(userService);
        storeService.setAdminService(adminService);
        adminService.setUserService(userService);
        adminService.setStoreService(storeService);
        adminService.setOrderService(orderService);
        orderService.setUserService(userService);


    }

    public static ServiceInitializer getInstance() {
        if (instance == null) {
            instance = new ServiceInitializer();
        }
        return instance;
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

    public OrderService getOrderService() {
        return orderService;
    }

    public static void reset() {
        instance = null;
    }
}
