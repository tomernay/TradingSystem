package Service;

import Domain.Externals.Security.Security;
import Domain.Market.Market;
import Utilities.Response;

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
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.makeStoreOwner(storeName, currentUsername, subscriberUsername);
        }
        return Response.error("Invalid token",null);
    }

//     Method to add a store manager subscription
    public Response<String> makeStoreManager(String storeName, String currentUsername, String subscriberUsername, List<String> permissions, String token) {
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.makeStoreManager(storeName, currentUsername, subscriberUsername, permissions);
        }
        return Response.error("Invalid token",null);
    }

    // Method to change permissions of a store manager
    public Response<String> addManagerPermissions(String storeName, String currentUsername, String subscriberUsername, String permission, String token) {
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.addManagerPermissions(storeName, currentUsername, subscriberUsername, permission);
        }
        return Response.error("Invalid token",null);
    }

    public Response<String> removeManagerPermissions(String storeName, String currentUsername, String subscriberUsername, String permission, String token) {
        if(Security.isValidJWT(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return userService.removeManagerPermissions(storeName, currentUsername, subscriberUsername, permission);
        }
        return Response.error("Invalid token",null);
    }

    // Method to close a store
    public Response<String> closeStore(String storeID, String currentUsername, String token) {
        if(Security.isValidJWT(token,currentUsername)) {
            Response<List<String>> storeCloseResponse = storeService.closeStore(storeID, currentUsername);
            if (!storeCloseResponse.isSuccess()) {
                return Response.error(storeCloseResponse.getMessage(), null);
            }
            return userService.sendCloseStoreNotification(storeCloseResponse.getData(), storeID);
        }
        return Response.error("Invalid token",null);
    }

    public Response<String> messageResponse(String currentUsername, boolean answer, String token) {
        if(Security.isValidJWT(token,currentUsername)) {
            return userService.messageResponse(currentUsername, answer);
        }
        return Response.error("Invalid token",null);
    }
}
