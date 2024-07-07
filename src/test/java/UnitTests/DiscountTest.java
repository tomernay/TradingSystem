package UnitTests;

import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Discounts.TYPE;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DiscountTest {

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
        buyer = userService.getUserFacade().getUserRepository().getSubscriber("yair12312");
        owner = userService.getUserFacade().getUserRepository().getSubscriber("newOwner");
        storeService.addStore("newStore0", "newOwner", owner.getToken());
        store1 = storeService.getStoreFacade().getStoreRepository().getActiveStore(0);
        storeService.addStore("newStore1", "newOwner", owner.getToken());
        store2 = storeService.getStoreFacade().getStoreRepository().getActiveStore(1);
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
        Assert.assertEquals(108, price.getData(), 0.0);
    }

    @Test
    public void simpleDiscountOnStore(){
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0, 20.0, "PRICE","1");
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(21.0), discount.getData());
    }

    @Test
    public void simpleDiscountOnProduct(){
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,20.0,"PRODUCT" ,"1" );
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(1.0), discount.getData());
    }

    @Test
    public void FailDiscountAllnull(){
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), null,null ,null,null );
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(0.0), discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertTrue(allDiscount1.getData().isEmpty());
        Response<List<DiscountDTO>> allDiscount2 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertTrue(allDiscount2.getData().isEmpty());
    }

    @Test
    public void FailDiscountWithoutPermission(){
        storeService.CreateSimpleDiscount("newOwner","", 1,20.0 ,"PRODUCT","1" );
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(0.0), discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(0, allDiscount1.getData().size());
        Response<List<DiscountDTO>> allDiscount2 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertTrue(allDiscount2.getData().isEmpty());
    }

    @Test
    public void twoDiscounts(){
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,20.0,"PRODUCT" ,"1" );
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,20.0,"PRODUCT" ,"2");
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(21.0), discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(2, allDiscount1.getData().size());
        Response<List<DiscountDTO>> allDiscount2 = storeService.getDiscountsFromStore(1,"newOwner",owner.getToken());
        Assert.assertEquals(0, allDiscount2.getData().size());
    }

    @Test
    public void maxDiscount(){
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,20.0,"PRODUCT" ,"1");
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,20.0,"PRODUCT" ,"2");
        storeService.makeComplexDiscount("newOwner",owner.getToken(),0,1,2,"MAX");
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(20.0), discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount1.getData().size());;
    }

    @Test
    public void plusDiscound(){
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,40.0,"PRODUCT" ,"1");
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,20.0,"PRODUCT" ,"2");
        storeService.makeComplexDiscount("newOwner",owner.getToken(),0,1,2,"PLUS");
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(22.0), discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount1.getData().size());;

    }

    @Test
    public void conditionalDiscount(){
        storeService.CreateSimpleDiscount("newOwner",owner.getToken(), 0,20.0,"PRODUCT" ,"1");
        storeService.addSimplePolicyToStore("newOwner",owner.getToken(),0, null,null,10.0,"PRODUCT" ,"1");
        storeService.makeConditionDiscount("newOwner",owner.getToken(),0,1,1);
        Response<Double> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertEquals(Double.valueOf(1.0), discount.getData());
        Response<List<DiscountDTO>> allDiscount1 = storeService.getDiscountsFromStore(0,"newOwner",owner.getToken());
        Assert.assertEquals(1, allDiscount1.getData().size());;
    }

}
