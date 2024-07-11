package UnitTests;

import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Inventory.ProductDTO;
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

public class policyTest {
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
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1, "yair12312", buyer.getToken());
    }

    @Test
    public void addSimplePolicy() {
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, 3.0, "PRODUCT", "1");
        Response<List<ProductDTO>> res1 = userService.lockShoppingCart("yair12312", buyer.getToken(), null);
        Assert.assertTrue(res1.isSuccess());
        Response<List<ConditionDTO>> res2 = storeService.getPoliciesFromStore(0, "newOwner", owner.getToken());
        Assert.assertEquals(1, res2.getData().size());
    }

    @Test
    public void FailAddSimplePolicy() {
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), null, null, null, 3.0, "PRODUCT", "1");
        Response<List<ConditionDTO>> res2 = storeService.getPoliciesFromStore(0, "newOwner", owner.getToken());
        Assert.assertEquals(0, res2.getData().size());
    }

    @Test
    public void addComplexPolicyAND() {
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, 3.0, "PRODUCT", "1");
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, 15.0, "PRODUCT", "2");
        storeService.makeComplexCondition(owner.getToken(), 0, 1, 2, "AND", "newOwner");
        Response<List<ConditionDTO>> res2 = storeService.getPoliciesFromStore(0, "newOwner", owner.getToken());
        Assert.assertEquals(1, res2.getData().size());
        Response<List<ProductDTO>> res1 = userService.lockShoppingCart("yair12312", buyer.getToken(), null);
        Assert.assertTrue(res1.isSuccess());
    }

    @Test
    public void addComplexPolicyOR() {
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, 3.0, "PRODUCT", "1");
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, 15.0, "PRODUCT", "2");
        storeService.makeComplexCondition(owner.getToken(), 0, 1, 2, "OR", "newOwner");
        Response<List<ConditionDTO>> res2 = storeService.getPoliciesFromStore(0, "newOwner", owner.getToken());
        Assert.assertEquals(1, res2.getData().size());
        Response<List<ProductDTO>> res1 = userService.lockShoppingCart("yair12312", buyer.getToken(), null);
        Assert.assertTrue(res1.isSuccess());
    }

    @Test
    public void addComplexPolicyX0R() {
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, 3.0, "PRODUCT", "1");
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, 1.0, "PRODUCT", "2");
        storeService.makeComplexCondition(owner.getToken(), 0, 1, 2, "XOR", "newOwner");
        Response<List<ConditionDTO>> res2 = storeService.getPoliciesFromStore(0, "newOwner", owner.getToken());
        Assert.assertEquals(1, res2.getData().size());
        Response<List<ProductDTO>> res1 = userService.lockShoppingCart("yair12312", buyer.getToken(), null);
        Assert.assertTrue(res1.isSuccess());
    }





}
