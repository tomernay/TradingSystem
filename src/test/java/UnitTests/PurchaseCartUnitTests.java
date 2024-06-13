package UnitTests;

import Domain.Store.Inventory.Inventory;
import Domain.Users.Subscriber.Subscriber;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

public class PurchaseCartUnitTests {

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
    public void lockCart(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        userService.ReleaseShoppingCartFromStore("yair12312",buyer.getToken());
        Inventory inventory = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(9,Integer.parseInt(inventory.getProductQuantity(1).getData()));
        Assert.assertEquals(9,Integer.parseInt(inventory.getProductQuantity(2).getData()));
    }

    @Test
    public void lockCartNotExist(){
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Inventory inventory = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(10,Integer.parseInt(inventory.getProductQuantity(1).getData()));
        Assert.assertEquals(10,Integer.parseInt(inventory.getProductQuantity(2).getData()));
    }

    @Test
    public void lockCartNotQuantityOneShop(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),100);
        Response<String> res1 = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Inventory inventory = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(10,Integer.parseInt(inventory.getProductQuantity(1).getData()));
        Assert.assertEquals(10,Integer.parseInt(inventory.getProductQuantity(2).getData()));
    }
    @Test
    public void lockCartNotQuantityTwoShop(){
        userService.addProductToShoppingCart("1","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),100);
        Response<String> res1 = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(10,Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10,Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10,Integer.parseInt(inventory2.getProductQuantity(1).getData()));
    }




    @Test
    public void PricesCalculation(){
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
        Assert.assertTrue(Objects.equals(res.getData(), "18.0"));
    }

    @Test
    public void OrderCheck(){
        userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        userService.addProductToShoppingCart("0","2","yair12312",buyer.getToken(),100);
        userService.addProductToShoppingCart("1","1","yair12312",buyer.getToken(),1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312",buyer.getToken());
        Response<String> res1 = orderService.getPurchaseHistoryByStore("yair12312");
        Assert.assertTrue(res1.getData()==null);
    }
}
