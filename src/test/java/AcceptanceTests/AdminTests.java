package AcceptanceTests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.AdminService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
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
    static AdminService adminService;

    @BeforeClass
    public static void init(){
        serviceInitializer = new ServiceInitializer();
        userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();
        adminService = serviceInitializer.getAdminService();
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
        adminService.getOrderFacade().getOrderRepository().addOrder("0","yair2",new HashMap<>());
        Response<String> response = adminService.getPurchaseHistoryByStore("0");
        Assert.assertTrue(response.isSuccess());

        Response<String> response2 = adminService.getPurchaseHistoryByStore("NonExistentStore");
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void getPurchasesHistoryBySubscriber(){
        adminService.getOrderFacade().getOrderRepository().addOrder("yairStore","yair2",new HashMap<>());
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
