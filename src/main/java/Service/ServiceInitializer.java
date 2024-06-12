package Service;

import org.springframework.stereotype.Component;

@Component
public class ServiceInitializer {
    private static ServiceInitializer instance = null;

    private UserService userService;
    private StoreService storeService;
    private AdminService adminService;
    private PaymentService paymentService;
    private OrderService orderService;

    private ServiceInitializer() {
        userService = new UserService();
        storeService = new StoreService();
        adminService = new AdminService();
        paymentService = new PaymentService();
        orderService = new OrderService();
        userService.setStoreService(storeService);
        userService.setOrderService(orderService);
        userService.setAdminService(adminService);
        storeService.setUserService(userService);
        adminService.setUserService(userService);
        adminService.setStoreService(storeService);
        adminService.setOrderService(orderService);
        paymentService.setUserService(userService);
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


    public PaymentService getPaymentService() {
        return paymentService;
    }

    public static void reset() {
        instance = null;
    }
}
