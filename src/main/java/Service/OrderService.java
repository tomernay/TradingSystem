package Service;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Suppliers.SupplySystem;
import Facades.OrderFacade;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.Map;

public class OrderService {
    private final OrderFacade orderFacade;
    private UserService userService;
    private final SupplySystem supplySystem;
    private final PaymentGateway paymentGateway;

    public OrderService(PaymentGateway paymentGateway, SupplySystem supplySystem) {
        this.orderFacade = new OrderFacade();
        this.supplySystem = supplySystem;
        this.paymentGateway = paymentGateway;
    }

    public OrderFacade getOrderFacade() {
        return orderFacade;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Response<Map<String, String>> getOrderHistory(String storeID) {
        return orderFacade.getOrdersHistory(storeID);
    }

    public Response<String> payAndSupply(double purchasePrice, String username, String token, String deliveryAddress, String creditCardNumber, String expirationDate, String cvv, String fullName) {
        if (creditCardNumber == null) {
            handlePaymentFailure(username, token);
            SystemLogger.error("[ERROR] User: " + username + " has cancelled payment");
            return Response.error("Payment cancelled", null);
        }
        SystemLogger.info("[START] User: " + username + " is trying to pay");
        if (!userService.isValidToken(token, username)) {
            SystemLogger.error("[ERROR] User: " + username + " is trying to pay with invalid token");
            return Response.error("Invalid token", null);
        }

        if (!userService.isInPurchaseProcess(username)) {
            SystemLogger.error("[ERROR] User: " + username + " is trying to pay without being in purchase process");
            return Response.error("User is not in purchase process", null);
        }

        boolean paymentStatus = paymentGateway.processPayment(purchasePrice, creditCardNumber, expirationDate, cvv, fullName);
        if (!paymentStatus) {
            handlePaymentFailure(username, token);
            SystemLogger.error("[ERROR] User: " + username + " payment failed");
            return Response.error("Payment failed", null);
        }

        Response<Map<String, Map<String, Integer>>> resShoppingCartContents = userService.getShoppingCartContents(username, token);
        boolean supplyStatus = SupplyOrder(resShoppingCartContents.getData(), deliveryAddress);
        if (!supplyStatus) {
            handlePaymentFailure(username, token);
            SystemLogger.error("[ERROR] User: " + username + " supply order failed");
            return Response.error("Supply order failed", null);
        }

        finalizeOrder(username, token, deliveryAddress);
        return Response.success("Payment and supply successful", null);
    }

    private void handlePaymentFailure(String username, String token) {
        userService.unlockProductsBackToStore(username, token);
        userService.purchaseProcessInterrupt(username);
    }

    private void finalizeOrder(String username, String token, String deliveryAddress) {
        userService.RemoveOrderFromStoreAfterSuccessfulPurchase(username, token);
        CreateOrder(username, token, deliveryAddress);
        userService.ResetCartAfterPurchase(username, token);
        userService.purchaseProcessInterrupt(username);
    }

    public Response<String> CreateOrder(String username, String token, String deliveryAddress) {
        SystemLogger.info("[START] User: " + username + " is trying to purchase the shopping cart");
        Response<Map<String, Map<String, Integer>>> resShoppingCartContents = userService.getShoppingCartContents(username, token);

        if (!userService.isValidToken(token, username)) {
            SystemLogger.error("[ERROR] User: " + username + " tried to purchase the shopping cart but the token was invalid");
            return Response.error("Invalid token", null);
        }

        SupplyOrder(resShoppingCartContents.getData(), deliveryAddress);
        return orderFacade.CreatOrder(username, deliveryAddress, resShoppingCartContents.getData());
    }

    public Response<String> getPurchaseHistoryByStore(String storeID) {
        return orderFacade.getPurchaseHistoryByStore(storeID);
    }

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberID) {
        return orderFacade.getPurchaseHistoryBySubscriber(subscriberID);
    }

    private boolean SupplyOrder(Map<String, Map<String, Integer>> shoppingCartContents, String deliveryAddress) {
        for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCartContents.entrySet()) {
            String storeID = storeEntry.getKey();
            Map<String, Integer> items = storeEntry.getValue();
            SystemLogger.info("Ordering supplies from store: " + storeID);
            if (!supplySystem.orderSupply(items, deliveryAddress)) {
                SystemLogger.error("Failed to order supply for store: " + storeID);
                return false;
            }
        }
        return true;
    }
}
