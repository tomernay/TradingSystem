package UnitTests;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.Repo.OrderRepository;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

public class DiscoundTest {

    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber buyer, owner;
    Store store1, store2;

    @Before
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        userService.register("yair12312", "Password123!");
        userService.register("newOwner", "Password123!");
        userService.loginAsSubscriber("yair12312", "Password123!");
        userService.loginAsSubscriber("newOwner", "Password123!");
        buyer = userService.getUserFacade().getUserRepository().getUser("yair12312");
        owner = userService.getUserFacade().getUserRepository().getUser("newOwner");
        storeService.addStore("newStore0", "newOwner", owner.getToken());
        store1 = storeService.getStoreFacade().getStoreRepository().getStore(0);
        storeService.addStore("newStore1", "newOwner", owner.getToken());
        store2 = storeService.getStoreFacade().getStoreRepository().getStore(1);
        storeService.addProductToStore(0, "newOProduct1", "DOG", 5, 10, "newOwner", owner.getToken());
        storeService.addProductToStore(0, "newOProduct2", "DOG", 10, 10, "newOwner", owner.getToken());
        storeService.addProductToStore(1, "newOProduct3", "DOG", 3, 10, "newOwner", owner.getToken());
        userService.addProductToShoppingCart(0, 1,1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1, "yair12312", buyer.getToken());
    }

    @Test
    public void calculationPrice(){
        Response<Double> price = userService.calculateShoppingCartPrice("yair12312", buyer.getToken());
        Assert.assertEquals(108, (double) price.getData(), 0.0);
    }

    @Test
    public void simpleDiscountOnStore(){
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),null,0,null,20.0);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("21.0", discount.getData());
    }

    @Test
    public void simpleDiscountOnProduct(){
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),1,0,null,20.0);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("1.0", discount.getData());
    }

    @Test
    public void FailDiscountAllnull(){
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),null,null,null,20.0);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("0.0", discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertTrue(allDiscount1.getData().isEmpty());
        Response<List<DiscountDTO>> allDiscount2 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertTrue(allDiscount2.getData().isEmpty());
    }

    @Test
    public void FailDiscountWithoutPermission(){
        storeService.CreateDiscountSimple("newOwner","",null,1,null,20.0);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("0.0", discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(0, allDiscount1.getData().size());
        Response<List<DiscountDTO>> allDiscount2 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertTrue(allDiscount2.getData().isEmpty());
    }

    @Test
    public void twoDiscounts(){
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),null,0,null,20.0);
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),1,1,null,50.0);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("22.5", discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount1.getData().size());
        Response<List<DiscountDTO>> allDiscount2 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount2.getData().size());
    }

    @Test
    public void maxDiscount(){
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),null,0,null,20.0);
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),1,0,null,50.0);
        storeService.makeComplexDiscount("newOwner",owner.getToken(),0,1,2,"MAX");
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("21.0", discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount1.getData().size());;
    }

    @Test
    public void plusDiscound(){
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),null,0,null,20.0);
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),1,0,null,50.0);
        storeService.makeComplexDiscount("newOwner",owner.getToken(),0,1,2,"PLUS");
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("23.5", discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount1.getData().size());;

    }

    @Test
    public void conditionalDiscount(){
        storeService.CreateDiscountSimple("newOwner",owner.getToken(),null,0,null,20.0);
        storeService.addSimplePolicyToStore("newOwner",owner.getToken(),0,null,null,null,10.0,null,true);
        storeService.makeConditionDiscount("newOwner",owner.getToken(),0,1,1);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals("21.0", discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount1.getData().size());;
    }

}
