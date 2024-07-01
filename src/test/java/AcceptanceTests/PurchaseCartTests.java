package AcceptanceTests;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.Repo.OrderRepository;
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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PurchaseCartTests {

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
        buyer = userService.getUserFacade().getUserRepository().getSubscriber("yair12312");
        owner = userService.getUserFacade().getUserRepository().getSubscriber("newOwner");
        storeService.addStore("newStore0", "newOwner", owner.getToken());
        store1 = storeService.getStoreFacade().getStoreRepository().getActiveStore(0);
        storeService.addStore("newStore1", "newOwner", owner.getToken());
        store2 = storeService.getStoreFacade().getStoreRepository().getActiveStore(1);
        storeService.addProductToStore(0, "newOProduct1", "DOG", 5, 10, "newOwner", owner.getToken());
        storeService.addProductToStore(0, "newOProduct2", "DOG", 10, 10, "newOwner", owner.getToken());
        storeService.addProductToStore(1, "newOProduct3", "DOG", 3, 10, "newOwner", owner.getToken());
    }

    @Test
    public void purchaseCartSuccessfully() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertTrue(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void purchaseCartSuccessfullyWithPolicy() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, null, null,10.0, null, true);
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertTrue(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertTrue(response.isSuccess());
    }


    @Test
    public void purchaseCartFailHandshakePay() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertTrue(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(false);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void purchaseCartFailHandshakeSupply() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertTrue(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(false);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void purchaseCartFailSupply() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertTrue(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(-1); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void purchaseCartFailPay() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertTrue(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment system to simulate payment failure
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(-1); // Simulate payment failure

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID
        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void purchaseCartFailPolicy() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 4, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        storeService.addSimplePolicyToStore("newOwner", owner.getToken(), 0, null, 1, null, null, 3.0, null);
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertFalse(response1.isSuccess());

        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment system to simulate payment failure due to policy
        Mockito.lenient().when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate payment failure

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void purchaseCartEmptyCart() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertFalse(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),0.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because cart is empty
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void TryToPurchaseTwice() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        Response<List<ProductDTO>> response2 =  userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertTrue(response2.isSuccess());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertFalse(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID


        Response<String> response = orderService.payAndSupply(response2.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment and supply were successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void purchaseCartRemoveProductFromStore() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        storeService.removeProductFromStore(1, 0, "newOwner", owner.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertFalse(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because product was removed from store
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void notEnoughQuantity() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 100, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertFalse(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because not enough quantity
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void purchaseCartFailRemoveShop() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart(0, 1, 1, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(0, 2, 10, "yair12312", buyer.getToken());
        userService.addProductToShoppingCart(1, 1, 1,"yair12312", buyer.getToken());
        storeService.closeStore(0, "newOwner", owner.getToken());
        Response<List<ProductDTO>> response1 = userService.lockShoppingCart("yair12312", buyer.getToken(),null );
        Assert.assertFalse(response1.isSuccess());
        // Mock the handshake method to return true
        Mockito.when(paymentGateway.handshake()).thenReturn(true);
        Mockito.when(supplySystem.handshake()).thenReturn(true);

        // Stub the payment process to simulate successful payment
        Mockito.when(paymentGateway.processPayment(Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(10000); // Simulate successful payment with transaction ID

        // Stub the supply process to simulate successful supply
        Mockito.when(supplySystem.orderSupply(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(20000); // Simulate successful supply with transaction ID

        Response<String> response = orderService.payAndSupply(response1.getData(),108.0, "yair12312", buyer.getToken(),
                "123 Street, City, State, Zip", "1234567812345678", "12/23", "123", "John Doe", "111111111");

        // Assert the payment failed because shop was removed
        Assert.assertFalse(response.isSuccess());

    }



}
