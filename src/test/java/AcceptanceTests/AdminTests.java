package AcceptanceTests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.*;
import Utilities.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

public class AdminTests {

    static Subscriber subscriber,buyer;
    static Store store;
    static ServiceInitializer serviceInitializer;
    static UserService userService;
    static StoreService storeService;
    static OrderService orderService;
    static AdminService adminService;

    @BeforeClass
    public static void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();
        adminService = serviceInitializer.getAdminService();
        orderService = serviceInitializer.getOrderService();
        userService.register("yair","Password123!");
        userService.loginAsSubscriber("yair","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getUser("yair");

        userService.register("yair2","Password123!");
        userService.loginAsSubscriber("yair2","Password123!");
        buyer=userService.getUserFacade().getUserRepository().getUser("yair2");
        storeService.addStore("yairStore","yair",subscriber.getToken());
        store=storeService.getStoreFacade().getStoreRepository().getStore("0");

    }
    @Test
    public void closeStore(){
        adminService.closeStore();
    }

    @Test
    public void removeSubscriber(){
        adminService.removeSubscriber();
    }

    @Test
    public void recieveInfoWithResponse(){
        adminService.recieveInfoWithResponse();
    }

    @Test
    public void getPurchasesHistoryByStore(){
        orderService.getOrderFacade().getOrderRepository().addOrder("0","yair2",new HashMap<>());
        Response<String> response = adminService.getPurchaseHistoryByStore("0");
        Assert.assertTrue(response.isSuccess());

        Response<String> response2 = adminService.getPurchaseHistoryByStore("NonExistentStore");
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void getPurchasesHistoryBySubscriber(){
        orderService.getOrderFacade().getOrderRepository().addOrder("yairStore","yair2",new HashMap<>());
        Response<String> response = adminService.getPurchaseHistoryBySubscriber("yair2");
        Assert.assertTrue(response.isSuccess());

        Response<String> response2 = adminService.getPurchaseHistoryBySubscriber("NonExistentSubscriber");
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void recieveSystemInfo(){
        adminService.recieveSystemInfo();
    }



}
