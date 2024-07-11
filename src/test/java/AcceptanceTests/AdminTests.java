package AcceptanceTests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.*;
import Utilities.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.Date;

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
        subscriber=userService.getUserFacade().getUserRepository().getSubscriber("yair");

        userService.register("yair2","Password123!");
        userService.loginAsSubscriber("yair2","Password123!");
        buyer=userService.getUserFacade().getUserRepository().getSubscriber("yair2");
        storeService.addStore("yairStore","yair",subscriber.getToken());
        store=storeService.getStoreFacade().getStoreRepository().getActiveStore(0);
        storeService.addProductToStore(1,"bamba","product",10.0,10,"yair",subscriber.getToken());
        userService.addProductToShoppingCart(0, 1, 5, "yair2", buyer.getToken());

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

//    @Test
//    public void getPurchasesHistoryByStore(){
//        orderService.CreateOrder("yair2",buyer.getToken(),"test");
//        Response<String> response = adminService.getPurchaseHistoryByStore(0);
//        Assert.assertTrue(response.isSuccess());
//
//        Response<String> response2 = adminService.getPurchaseHistoryByStore(4);
//        Assert.assertFalse(response2.isSuccess());
//    }
//
//    @Test
//    public void getPurchasesHistoryBySubscriber(){
//        orderService.CreateOrder("yair2",buyer.getToken(),"test");
//        Response<String> response = adminService.getPurchaseHistoryBySubscriber("yair2");
//        Assert.assertTrue(response.isSuccess());
//
//        Response<String> response2 = adminService.getPurchaseHistoryBySubscriber("NonExistentSubscriber");
//        Assert.assertFalse(response2.isSuccess());
//    }

    @Test
    public void recieveSystemInfo(){
        adminService.recieveSystemInfo();
    }

    @Test
    public void suspendUser(){
        Response<String> response = adminService.suspendUser("yair",new Date(2024,10,15));
        Assert.assertTrue(response.isSuccess());

        Response<String> response2 = adminService.suspendUser("non-user",new Date(2024,10,17));
        Assert.assertNull(response2.getData());

        Response<String> response3 = adminService.suspendUser("yair",new Date(2024,10,19));
        Assert.assertNull(response3.getData());
    }

    @Test
    public void resumeUser(){
        adminService.suspendUser("yair",new Date(2024,10,15));
        Assert.assertTrue(adminService.getSuspensionList().getData().containsKey("yair"));

        Response<String> response = adminService.reactivateUser("yair");
        Assert.assertTrue(response.isSuccess());
        Assert.assertTrue(!adminService.getSuspensionList().getData().containsKey("yair"));
    }

    @Test
    public void getSuspensionList(){
        adminService.suspendUser("yair",new Date(2024,10,15));
        adminService.suspendUser("yair2",new Date(2024,10,17));

        Assert.assertTrue(adminService.getSuspensionList().getData().containsKey("yair"));
        Assert.assertTrue(adminService.getSuspensionList().getData().containsKey("yair2"));
    }
}
