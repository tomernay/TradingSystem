package AcceptanceTests;

import Domain.Users.Subscriber.Subscriber;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PurchaseCartTests {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber buyer, owner;
    OrderService orderService;

    @Before
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        orderService = serviceInitializer.getOrderService();
        userService.register("yair12312", "Password123!");
        userService.register("newOwner", "Password123!");
        userService.loginAsSubscriber("yair12312", "Password123!");
        userService.loginAsSubscriber("newOwner", "Password123!");
        buyer = userService.getUserFacade().getUserRepository().getUser("yair12312");
        owner = userService.getUserFacade().getUserRepository().getUser("newOwner");
        storeService.addStore("newStore0", "newOwner", owner.getToken());
        storeService.addStore("newStore1", "newOwner", owner.getToken());
        storeService.addProductToStore("0","newOProduct1","DOG",5, 10,"newOwner",owner.getToken());
        storeService.addProductToStore("0","newOProduct2","DOG",10, 10,"newOwner",owner.getToken());
        storeService.addProductToStore("1","newOProduct3","DOG",3, 10,"newOwner",owner.getToken());

    }
    @Test
    public void lockCard(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("1","1","yair12312",buyer.getToken(),1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
    }

    @Test
    public void lockCardNotExist(){
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Assert.assertFalse(res.isSuccess());
    }

    @Test
    public void lockCardNotQuantity(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),100);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Assert.assertFalse(res.isSuccess());
    }

    @Test
    public void PricecCalculation(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("1","1","yair12312",buyer.getToken(),1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
    }

    @Test
    public void CartLockAndRelease(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("1","1","yair12312",buyer.getToken(),1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
        Response<String> res1 = userService.ReleaseShoppingCartAndBacktoInventory("yair12312",buyer.getToken());
        Assert.assertTrue(res1.isSuccess());
    }

    @Test
    public void CartLockAndReleaseNotExist(){
        Response<String> res1 = userService.ReleaseShoppingCartAndBacktoInventory("yair12312",buyer.getToken());
        Assert.assertFalse(res1.isSuccess());
    }

    @Test
    public void PurchaseCartSuccess() {
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("1","1","yair12312",buyer.getToken(),1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
        Response<String> res1 = userService.CalculateDiscounts("yair12312",buyer.getToken());
        Assert.assertTrue(res1.isSuccess());
        Response<String> res3 = orderService.CreateOrder("yair12312",buyer.getToken());
        Assert.assertTrue(res3.isSuccess());
        Response<String> res4 = userService.ReleaseShoppSingCartFromStore("yair12312",buyer.getToken());
        Assert.assertTrue(res4.isSuccess());
        Response<String> res2 = userService.ReleaseShoppSingCartForUser("yair12312",buyer.getToken());
        Assert.assertTrue(res2.isSuccess());
    }

    @Test
    public void PurchaseCartFail(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("1","1","yair12312",buyer.getToken(),1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
        Response<String> res1 = userService.ReleaseShoppingCartAndBacktoInventory("yair12312",buyer.getToken());
        Assert.assertTrue(res1.isSuccess());
    }


}
