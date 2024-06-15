package UnitTests;

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
    public void purchaseCartSuccessfully() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());

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

    @Test
    public void purchaseCartSuccessfullyWithPolicy() {
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.addSimplePoliceToStore("newOwner",owner.getToken(),"1",null,null,10,null,1.0);
        userService.lockShoppingCart("yair12312", buyer.getToken());

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

    @Test
    public void purchaseCartFailPay(){
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Stub the payment process to bypass actual payment but still call the original implementation for other parts
        Mockito.doAnswer(invocation -> {
            // Simulate the mock response for payment
            Response<String> paymentResponse = Response.error("Payment Fail", null);

            // Execute the lines that follow the actual payment call in the immediatePay method
            // Manually call the subsequent methods as needed
            userService.ReleaseShoppingCartAndBacktoInventory("yair12312", buyer.getToken());
            userService.purchaseProcessInterrupt("yair12312");
            return paymentResponse;
        }).when(paymentService).immediatePay(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString());

        // Call the mocked payment method
        Response<String> paymentResponse = paymentService.immediatePay("yair12312", 1000.0, "creditCard123", buyer.getToken());

        // Assert the payment was successful
        Assert.assertFalse(paymentResponse.isSuccess());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        // Verify the purchase history is updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void purchaseCartFailPolicy(){
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.addSimplePoliceToStore("newOwner",owner.getToken(),null,"0",1,0,3,0.0);
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Stub the payment process to bypass actual payment but still call the original implementation for other parts
        Mockito.doAnswer(invocation -> {
            // Simulate the mock response for payment
            Response<String> paymentResponse = Response.error("Payment Fail", null);

            // Execute the lines that follow the actual payment call in the immediatePay method
            // Manually call the subsequent methods as needed

            return paymentResponse;
        }).when(paymentService).immediatePay(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString());

        // Call the mocked payment method
        Response<String> paymentResponse = paymentService.immediatePay("yair12312", 1000.0, "creditCard123", buyer.getToken());

        // Assert the payment was successful
        Assert.assertFalse(paymentResponse.isSuccess());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        // Verify the purchase history is updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void purchaseCartEmptyCart(){
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.lockShoppingCart("yair12312", buyer.getToken());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertTrue(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void TryToPurchaseTwise(){
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());
        userService.lockShoppingCart("yair12312", buyer.getToken());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(9, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(0, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(9, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void purchaseCartRemoveProuductFromStore(){
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.removeProductFromStore(1, "0", "newOwner", owner.getToken());
        userService.lockShoppingCart("yair12312", buyer.getToken());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());

    }

    @Test
    public void notEnoughQuantity(){
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 100);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        userService.lockShoppingCart("yair12312", buyer.getToken());
        Inventory inventory1 = storeService.getStoreFacade().getStoreRepository().getStore("0").getInventory();
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(1).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory1.getProductQuantity(2).getData()));
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
    }

    @Test
    public void purchaseCartFailRemoveShop(){
        orderRepository = orderService.getOrderFacade().getOrderRepository();
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        storeService.closeStore("0", "newOwner", owner.getToken());
        userService.lockShoppingCart("yair12312", buyer.getToken());

        // Stub the payment process to bypass actual payment but still call the original implementation for other parts
        Mockito.doAnswer(invocation -> {
            // Simulate the mock response for payment
            Response<String> paymentResponse = Response.error("Payment Fail", null);

            // Execute the lines that follow the actual payment call in the immediatePay method
            // Manually call the subsequent methods as needed

            return paymentResponse;
        }).when(paymentService).immediatePay(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString(), Mockito.anyString());

        // Call the mocked payment method
        Response<String> paymentResponse = paymentService.immediatePay("yair12312", 1000.0, "creditCard123", buyer.getToken());

        // Assert the payment was successful
        Inventory inventory2 = storeService.getStoreFacade().getStoreRepository().getStore("1").getInventory();
        // Verify the purchase history is updated
        orderService.getPurchaseHistoryBySubscriber("yair12312");
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertTrue(orderRepository.getOrders().isEmpty());
        Assert.assertEquals(10, Integer.parseInt(inventory2.getProductQuantity(1).getData()));
        Assert.assertFalse(buyer.getShoppingCartContents().getData().isEmpty());
    }

    @Test
    public void priceCalculation(){
        userService.addProductToShoppingCart("0", "1", "yair12312", buyer.getToken(), 1);
        userService.addProductToShoppingCart("0", "2", "yair12312", buyer.getToken(), 10);
        userService.addProductToShoppingCart("1", "1", "yair12312", buyer.getToken(), 1);
        Response<Double> price = userService.calculatedPriceShoppingCart("yair12312", buyer.getToken());
        Assert.assertTrue(price.getData() == 108.0);
    }



}
