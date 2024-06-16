package UnitTests;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.Repo.OrderRepository;
import Domain.Store.Inventory.Inventory;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.*;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseCartUnitTest {

    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber buyer, owner;
    OrderService orderService;
    OrderRepository orderRepository;
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
        orderService = serviceInitializer.getOrderService();
        orderService.setPaymentGateway(paymentGateway);
        orderService.setSupplySystem(supplySystem);

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
    public void purchaseCartSuccessfully() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertTrue(response.isSuccess());

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

    @Test
    public void purchaseCartSuccessfullyWithPolicy() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), "1", null, null, 10, null, 1.0);
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertTrue(response.isSuccess());

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

    @Test
    public void purchaseCartFailPay() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment system to simulate payment failure
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(-1); // Simulate payment failure

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed
        Assert.assertFalse(response.isSuccess());

        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();

        // Verify the purchase history is not updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void purchaseCartFailPolicy() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), null, "0", 1, 0, 3, 0.0);
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment system to simulate payment failure due to policy
        Mockito.lenient().when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(-1); // Simulate payment failure

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed
        Assert.assertFalse(response.isSuccess());

        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();

        // Verify the purchase history is not updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void purchaseCartEmptyCart() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        Response<String> response = orderService.payAndSupply(0.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because cart is empty
        Assert.assertFalse(response.isSuccess());

        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();

        // Verify the purchase history is not updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertTrue(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void TryToPurchaseTwice() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertTrue(response.isSuccess());

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

    @Test
    public void purchaseCartRemoveProductFromStore() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.removeProductFromStore(1, "0", "newOwner", owner.getToken());
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because product was removed from store
        Assert.assertFalse(response.isSuccess());

        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();

        // Verify the purchase history is not updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void notEnoughQuantity() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 100);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because not enough quantity
        Assert.assertFalse(response.isSuccess());

        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();

        // Verify the purchase history is not updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void purchaseCartFailRemoveShop() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.closeStore("0", "newOwner", owner.getToken());
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        Response<String> response = orderService.payAndSupply(108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because shop was removed
        Assert.assertFalse(response.isSuccess());

        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();

        // Verify the purchase history is not updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
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
