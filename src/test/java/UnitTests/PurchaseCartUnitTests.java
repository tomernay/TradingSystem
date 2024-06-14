package UnitTests;

import Domain.Repo.OrderRepository;
import Domain.Store.Inventory.Inventory;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.OrderService;
import Service.PaymentService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseCartUnitTests {

    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber buyer, owner;
    OrderService orderService;
    OrderRepository orderRepository;
    Store store1, store2;

    @Mock
    private PaymentService paymentService;

    @Before
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        orderService = serviceInitializer.getOrderService();

        // Inject the mock payment service into the service initializer or wherever necessary
//        serviceInitializer.setPaymentService(paymentService);

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
    public void lockCart() {
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312", buyer.getToken());
        userService.ReleaseShoppingCartFromStore("yair12312", buyer.getToken());
        Inventory inventory = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(9, Integer.parseInt(inventory.getProductQuantity(1).getData()));
        Assert.assertEquals(9, Integer.parseInt(inventory.getProductQuantity(2).getData()));
    }

    @Test
    public void lockCartNotExist() {
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312", buyer.getToken());
        Inventory inventory = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(10, Integer.parseInt(inventory.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory.getProductQuantity(2).getData()));
    }

    @Test
    public void lockCartNotQuantityOneShop() {
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 100);
        Response<String> res1 = userService.LockShoppingCartAndCalculatedPrice("yair12312", buyer.getToken());
        Inventory inventory = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(10, Integer.parseInt(inventory.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory.getProductQuantity(2).getData()));
    }

    @Test
    public void lockCartNotQuantityTwoShop() {
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 100);
        Response<String> res1 = userService.LockShoppingCartAndCalculatedPrice("yair12312", buyer.getToken());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
    }

    @Test
    public void PricesCalculation() {
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312", buyer.getToken());
        Assert.assertTrue(res.isSuccess());
    }

    @Test
    public void CartLockAndRelease() {
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        Response<String> res = userService.LockShoppingCartAndCalculatedPrice("yair12312", buyer.getToken());
        Assert.assertTrue(Objects.equals(res.getData(), "18.0"));
    }

    @Test
    public void OrderCheck() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.LockShoppingCartAndCalculatedPrice("yair12312", buyer.getToken());

        // Stub the payment process to bypass actual payment but still call the original implementation for other parts
        Mockito.doAnswer(invocation -> {
            // Simulate the mock response for payment
            Response<String> paymentResponse = Response.success("Payment Successful", null);

            // Execute the lines that follow the actual payment call in the immediatePay method
            // Manually call the subsequent methods as needed
            userService.ReleaseShoppingCartFromStore("yair12312", buyer.getToken());
            orderService.CreateOrder("yair12312", buyer.getToken());
            userService.ReleaseShoppingCartForUser("yair12312", buyer.getToken());
            userService.purchaseProcessInterrupt("yair12312");

            return paymentResponse;
        }).when(paymentService).immediatePay(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString());

        // Call the mocked payment method
        Response<String> paymentResponse = paymentService.immediatePay("yair12312", 1000.0, "creditCard123", buyer.getToken());

        // Assert the payment was successful
        Assert.assertTrue(paymentResponse.isSuccess());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        // Verify the purchase history is updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertFalse(orderRepository.getOrders().isEmpty());
        Assert.assertTrue(orderRepository.getOrders().size() == 2);
        Assert.assertEquals(orderRepository.getOrders().get(0).getStoreID(), "0");
        Assert.assertEquals(orderRepository.getOrders().get(0).getProducts().size(), 2);
        Assert.assertEquals(orderRepository.getOrders().get(1).getStoreID(), "1");
        Assert.assertEquals(orderRepository.getOrders().get(1).getProducts().size(), 1);
        Assert.assertEquals(9, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(0, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(9, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertTrue(buyer.getShoppingCartContents().getData().isEmpty());
    }
}
