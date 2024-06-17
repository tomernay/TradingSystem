package UnitTests;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.Repo.OrderRepository;
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

public class DiscountAnaCalulationPriceTest {
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
    }

    @Test
    public void priceCalculation() {
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        Response<Double> price = userService.calculateShoppingCartPrice("yair12312", buyer.getToken());
        Assert.assertTrue(price.getData() == 108.0);
    }
}
