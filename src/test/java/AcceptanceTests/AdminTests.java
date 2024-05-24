package AcceptanceTests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import Service.AdminService;

import java.util.HashMap;

public class AdminTests {
    static AdminService adminService;

    static Subscriber subscriber,buyer;
    static Store store;
    static UserService userService;
    static StoreService storeService;

    @BeforeClass
    public static void init(){
        adminService = new AdminService();

        userService=new UserService();
        storeService=new StoreService();
        userService.register("yair","by");
        subscriber=userService.getUser("yair");

        userService.register("yair2","by2");
        buyer=userService.getUser("yair2");
        storeService.addStore("yairStore","yair",subscriber.getToken());
        store=storeService.getStore("0");

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
        adminService.getMarket().getMarketFacade().getOrderRepository().addOrder("0","yair2",new HashMap<>());
        Response<String> response = adminService.getPurchaseHistoryByStore("0");
        Assert.assertTrue(response.isSuccess());

        Response<String> response2 = adminService.getPurchaseHistoryByStore("NonExistentStore");
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void getPurchasesHistoryBySubscriber(){
        adminService.getMarket().getMarketFacade().getOrderRepository().addOrder("yairStore","yair2",new HashMap<>());
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
