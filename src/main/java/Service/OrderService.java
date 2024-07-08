package Service;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.OrderDTO;
import Domain.Store.Inventory.ProductDTO;

import Facades.OrderFacade;
import Presentation.application.View.UtilitiesView.Broadcaster;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderService {
    private final OrderFacade orderFacade;
    private UserService userService;
    private SupplySystem supplySystem;
    private StoreService storeService;
    private PaymentGateway paymentGateway;

    public OrderService(PaymentGateway paymentGateway, SupplySystem supplySystem,StoreService storeService) {
        this.orderFacade = new OrderFacade();
        this.supplySystem = supplySystem;
        this.paymentGateway = paymentGateway;
        this.storeService=storeService;
    }

    public OrderFacade getOrderFacade() {
        return orderFacade;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Response<Map<String, String>> getOrderHistory(Integer storeID) {
        return orderFacade.getOrdersHistory(storeID);
    }

    public void setPaymentGateway(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void setSupplySystem(SupplySystem supplySystem) {
        this.supplySystem = supplySystem;
    }

    public Response<String> payAndSupply(List<ProductDTO> products, Double purchasePrice, String username, String token, String deliveryAddress, String creditCardNumber, String expirationDate, String cvv, String fullName, String id) {
        if (creditCardNumber == null) {
            handlePaymentFailure(username, token);
            SystemLogger.error("[ERROR] User: " + username + " has cancelled payment");
            return Response.error("Payment cancelled", null);
        }
        SystemLogger.info("[START] User: " + username + " is trying to pay");

        if (!paymentGateway.handshake() || !supplySystem.handshake()) {
            handlePaymentFailure(username, token);
            SystemLogger.error("[ERROR] External system is not available");
            return Response.error("External system is not available", null);
        }

        if (!userService.isValidToken(token, username)) {
            handlePaymentFailure(username, token);
            SystemLogger.error("[ERROR] User: " + username + " is trying to pay with invalid token");
            return Response.error("Invalid token", null);
        }

        if (!userService.isInPurchaseProcess(username)) {
            SystemLogger.error("[ERROR] User: " + username + " is trying to pay without being in purchase process");
            return Response.error("User is not in purchase process", null);
        }

        int paymentTransactionId = processPayment(purchasePrice, creditCardNumber, expirationDate, cvv, fullName, id);
        if (paymentTransactionId == -1) {
            handlePaymentFailure(username, token);
            SystemLogger.error("[ERROR] User: " + username + " payment failed");
            return Response.error("Payment failed", null);
        }
        List<Integer> supplyTransactionIds = new ArrayList<>();
        for (ProductDTO product : products) {
            Map<Integer, Integer> productsMap = Map.of(product.getProductID(), product.getQuantity());
            int supplyTransactionId = processSupply(productsMap, deliveryAddress, fullName);
            if (supplyTransactionId == -1) {
                paymentGateway.cancelPayment(paymentTransactionId); // Cancel the payment
                for (int transactionId : supplyTransactionIds) {
                    supplySystem.cancelSupply(transactionId); // Cancel the supply
                }
                handlePaymentFailure(username, token);
                SystemLogger.error("[ERROR] User: " + username + " supply order failed");
                return Response.error("Supply order failed", null);
            }
            supplyTransactionIds.add(supplyTransactionId);
        }

        finalizeOrder(username, token, deliveryAddress, products);
        sendNotificationToStaff(products);
        return Response.success("Payment and supply successful", null);
    }

    /**
     * send notification for all
     * @param products
     */
    private void sendNotificationToStaff(List<ProductDTO> products){
           orderFacade.getOrderRepository().notifyStaff(products, storeService.getStoreRolls(products.get(0).getStoreID()).getData());
    }

    private int processPayment(Double purchasePrice, String creditCardNumber, String expirationDate, String cvv, String fullName, String id) {
        // Call the payment gateway and get the transaction ID
        return paymentGateway.processPayment(purchasePrice, creditCardNumber, expirationDate, cvv, fullName, id);
    }

    private int processSupply(Map<Integer, Integer> shoppingCartContents, String deliveryAddress, String name) {
        // Call the supply system and get the transaction ID
        return supplySystem.orderSupply(shoppingCartContents, deliveryAddress, name);
    }

    private void handlePaymentFailure(String username, String token) {
        userService.unlockProductsBackToStore(username, token);
        userService.purchaseProcessInterrupt(username);
    }

    private void finalizeOrder(String username, String token, String deliveryAddress, List<ProductDTO> products) {
        userService.RemoveOrderFromStoreAfterSuccessfulPurchase(username, token);
        CreateOrder(username, token, deliveryAddress, products);
        userService.ResetCartAfterPurchase(username, token);
        userService.purchaseProcessInterrupt(username);
    }

    public Response<String> CreateOrder(String username, String token, String deliveryAddress, List<ProductDTO> products) {
        SystemLogger.info("[START] User: " + username + " is trying to purchase the shopping cart");
        if (!userService.isValidToken(token, username)) {
            SystemLogger.error("[ERROR] User: " + username + " tried to purchase the shopping cart but the token was invalid");
            return Response.error("Invalid token", null);
        }
        return orderFacade.CreateOrder(username, deliveryAddress, products);
    }

    public Response<List<OrderDTO>> getPurchaseHistoryByStore(Integer storeID) {
        return orderFacade.getPurchaseHistoryByStore(storeID);
    }

    public Response<List<OrderDTO >> getPurchaseHistoryBySubscriber(String subscriberID) {
        return orderFacade.getPurchaseHistoryBySubscriber(subscriberID);
    }

    public Response<List<OrderDTO>> getOrdersHistory(Integer storeID) {
        return orderFacade.getOrdersHistoryDTO(storeID);
    }

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    public SupplySystem getSupplySystem() {
        return supplySystem;
    }
}
