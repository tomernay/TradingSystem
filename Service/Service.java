package Service;

import Domain.Externals.Security.Security;
import Domain.Market.Market;
import Utilities.Response;
import Utilities.SystemLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Service {
    private UserService userService;
    private PaymentService paymentService;
    private StoreService storeService;
    private Market market;

    public Service(){
        market=new Market();
        userService=new UserService(market);
        paymentService=new PaymentService(market);
          storeService=new StoreService(market);
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public UserService getUserService() {
        return userService;
    }

    public StoreService getStoreService() {
        return storeService;
    }

    // Method to add a store owner subscription
    public Response<String> makeStoreOwner(String storeName, String currentUsername, String subscriberUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to make " + subscriberUsername + " a store owner");
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.makeStoreOwner(storeName, currentUsername, subscriberUsername);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to make " + subscriberUsername + " a store owner but the token was invalid");
        return Response.error("Invalid token",null);
    }

//     Method to add a store manager subscription
    public Response<String> makeStoreManager(String storeName, String currentUsername, String subscriberUsername, List<String> permissions, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to make " + subscriberUsername + " a store manager");
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.makeStoreManager(storeName, currentUsername, subscriberUsername, permissions);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to make " + subscriberUsername + " a store manager but the token was invalid");
        return Response.error("Invalid token",null);
    }

    // Method to change permissions of a store manager
    public Response<String> addManagerPermissions(String storeName, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to add permissions: " + permission + " to " + subscriberUsername);
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.addManagerPermissions(storeName, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> removeManagerPermissions(String storeName, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to remove permissions: " + permission + " from " + subscriberUsername);
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.removeManagerPermissions(storeName, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    // Method to close a store
    public Response<String> closeStore(String storeID, String currentUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to close store: " + storeID);
        if(Security.isValidJWT(token,currentUsername)) {
            Response<List<String>> storeCloseResponse = storeService.closeStore(storeID, currentUsername);
            if (!storeCloseResponse.isSuccess()) {
                return Response.error(storeCloseResponse.getMessage(), null);
            }
            return userService.sendCloseStoreNotification(storeCloseResponse.getData(), storeID);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to close store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> messageResponse(String currentUsername, boolean answer, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to respond to a message");
        if(Security.isValidJWT(token,currentUsername)) {
            return userService.messageResponse(currentUsername, answer);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to respond to a message but the token was invalid");
        return Response.error("Invalid token",null);
    }
}
