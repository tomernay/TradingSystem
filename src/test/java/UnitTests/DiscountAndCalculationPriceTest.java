package UnitTests;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

public class DiscountAndCalculationPriceTest {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber buyer, owner;
    Store store1, store2;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private SupplySystem supplySystem;

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
        store1 = storeService.getStoreFacade().getStoreRepository().getStore("0");
        storeService.addStore("newStore1", "newOwner", owner.getToken());
        store2 = storeService.getStoreFacade().getStoreRepository().getStore("1");
        storeService.addProductToStore("0", "newOProduct1", "DOG", 5, 10, "newOwner", owner.getToken());
        storeService.addProductToStore("0", "newOProduct2", "DOG", 10, 10, "newOwner", owner.getToken());
        storeService.addProductToStore("1", "newOProduct3", "DOG", 3, 10, "newOwner", owner.getToken());
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
    }

    @Test
    public void priceCalculation() {
        Response<Double> price = userService.calculateShoppingCartPrice("yair12312", buyer.getToken());
        Assert.assertTrue(price.getData() == 108.0);
    }

    @Test
    public void creatSimpleDiscountAndCalculation() {
        storeService.CreateDiscountSimple("newOwner", owner.getToken(), "1", "0", null, "20");
        Response<List<DiscountDTO>> discountInStore = storeService.getDiscountsFromStore("0", "newOwner", owner.getToken());
        Assert.assertTrue(discountInStore.getData().size() == 1);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        System.out.println(discount.getData());
        Assert.assertTrue(discount.getData().equals("1.0"));
    }

    @Test
    public void creatPlusDiscountAndCalculation() {
        storeService.CreateDiscountSimple("newOwner", owner.getToken(), "1", "0", null, "20");
        storeService.CreateDiscountSimple("newOwner", owner.getToken(), "2", "0", null, "20");
        storeService.makeComplexDiscount("newOwner", owner.getToken(), "0", 1, 2, "PLUS");
        Response<List<DiscountDTO>> discountInStore = storeService.getDiscountsFromStore("0", "newOwner", owner.getToken());
        Assert.assertTrue(discountInStore.getData().size() == 1);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertTrue(discount.getData().equals("21.0"));
    }

    @Test
    public void creatMaxDiscountAndCalculation() {
        storeService.CreateDiscountSimple("newOwner", owner.getToken(), "1", "0", null, "20");
        storeService.CreateDiscountSimple("newOwner", owner.getToken(), "2", "0", null, "20");
        storeService.makeComplexDiscount("newOwner", owner.getToken(), "0", 1, 2, "MAX");
        Response<List<DiscountDTO>> discountInStore = storeService.getDiscountsFromStore("0", "newOwner", owner.getToken());
        Assert.assertTrue(discountInStore.getData().size() == 1);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertTrue(discount.getData().equals("20.0"));
    }

    @Test
    public void creatConditionDiscountAndCalculation() {
        storeService.CreateDiscountSimple("newOwner", owner.getToken(), "2", "0", null, "20");
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), "0", null,null,null,10.0,null,1.0);
        storeService.makeConditionDiscount("newOwner", owner.getToken(), "0", 1,1);
        Response<List<DiscountDTO>> discountInStore = storeService.getDiscountsFromStore("0", "newOwner", owner.getToken());
        Assert.assertTrue(discountInStore.getData().size() == 1);
        Response<List<ConditionDTO>> policies = storeService.getPoliciesFromStore("0", "newOwner", owner.getToken());
        System.out.println(policies.getData().size());
        Assert.assertTrue(policies.getData().size() == 0);
        Response<String> discount = userService.CalculateDiscounts("yair12312", buyer.getToken());
        Assert.assertTrue(discount.getData().equals("20.0"));



    }
}