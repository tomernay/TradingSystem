package Service;



import Domain.Externals.Security.Security;
import Domain.Market.Market;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;
import Utilities.SystemLogger;


import java.util.List;
import java.util.Map;

public class UserService {
    private Market market;


    public UserService() {
        this.market = Market.getInstance();
    }

    public Response<String> loginAsSubscriber(Subscriber subscriber) {
        if (!subscriber.loginAsGuest()) {
            return Response.error("Error - can't signed in as a GUEST", null);
        }
        return market.loginAsSubscriber(subscriber);
    }

    public Response<String> logoutAsSubscriber(Subscriber subscriber) {
        return market.logoutAsSubscriber(subscriber);
    }

    public Response<String> loginAsGuest(User user) {
        if (!user.loginAsGuest()) {
            return Response.error("Error - can't signed in as a GUEST", null);
        }
        return market.loginAsGuest(user);
    }

    //function as a Guest - exit from the website
    public Response<String> logoutAsGuest(User user) {
        return market.logoutAsGuest(user);
    }

    // Method to add a store owner subscription
    public Response<String> makeStoreOwner(String storeID, String currentUsername, String subscriberUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to make " + subscriberUsername + " a store owner");
        if(Security.isValidJWT(token,currentUsername)) {
            if (currentUsername.equals(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " is trying to make himself a store manager");
                return Response.error("User: " + subscriberUsername + " is trying to make himself a store manager", null);
            }
            if (!userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return market.makeStoreOwner(storeID, currentUsername, subscriberUsername);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to make " + subscriberUsername + " a store owner but the token was invalid");
        return Response.error("Invalid token",null);
    }

    // Method to add a store manager subscription
    public Response<String> makeStoreManager(String storeID, String currentUsername, String subscriberUsername, List<String> permissions, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to make " + subscriberUsername + " a store manager");
        if(Security.isValidJWT(token,currentUsername)) {
            if (currentUsername.equals(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " is trying to make himself a store manager");
                return Response.error("User: " + subscriberUsername + " is trying to make himself a store manager", null);
            }
            if (!userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return market.makeStoreManager(storeID, currentUsername, subscriberUsername, permissions);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to make " + subscriberUsername + " a store manager but the token was invalid");
        return Response.error("Invalid token",null);
    }



    public Response<String> messageResponse(String currentUsername, boolean answer, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to respond to a message");
        if(Security.isValidJWT(token,currentUsername)) {
            return market.messageResponse(currentUsername, answer);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to respond to a message but the token was invalid");
        return Response.error("Invalid token",null);
    }

    // Method to prompt the subscriber to accept the subscription
    private boolean promptSubscription(String subscriberUsername, String targetUsername) {
        // Implement the prompt logic here
        // For example, display a prompt to the subscriber
        // and wait for user input to accept or decline the subscription
        return true; // Assume subscription is accepted
    }

    //register a new user
    public Response<String> register(String username, String password) {
        return market.register(username, password);
    }

    public Subscriber getUser(String username) {
        return market.getMarketFacade().getUserRepository().getUser(username);
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return market.sendCloseStoreNotification(subscriberNames, storeID);
    }

    public boolean userExists(String subscriberUsername) {
        return market.getMarketFacade().getUserRepository().isUserExist(subscriberUsername);
    }

    public Response<Map<String, SubscriberState>> requestEmployeesStatus(String storeID, String userName, String token) {
        if (Security.isValidJWT(token, userName)) {
            if (market.getMarketFacade().getStoreRepository().isStoreOwner(storeID, userName)) {
                return market.requestEmployeesStatus(storeID);
            }
        }
        return Response.error("invalid token", null);
    }

    public Response<Map<String, List<Permissions>>> requestManagersPermissions(String storeID, String userName, String token) {
        if (Security.isValidJWT(token, userName)) {
            if (market.getMarketFacade().getStoreRepository().isStoreOwner(storeID, userName)) {
                return market.requestManagersPermissions(storeID);
            }
        }
        return Response.error("invalid token", null);
    }

    public Response<String> addProductToShoppingCart(String storeID, String productID, String userName, String token, int quantity) {
        if (Security.isValidJWT(token, userName)) {
            return market.addProductToShoppingCart(storeID, productID, userName, quantity);
        }
        return Response.error("invalid token", null);
    }

    public Response<String> removeProductFromShoppingCart(String storeID, String productID, String userName, String token) {
        if (Security.isValidJWT(token, userName)) {
            return market.removeProductFromShoppingCart(userName,storeID, productID);
        }
        return Response.error("invalid token", null);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, String userName, String token, int quantity) {
        if (Security.isValidJWT(token, userName)) {
            return market.updateProductInShoppingCart(storeID, productID, userName, quantity);
        }
        return Response.error("invalid token", null);
    }

    public Response<String> getShoppingCartContents(String userName, String token) {
        if (Security.isValidJWT(token, userName)) {
            return market.getShoppingCartContents(userName);
        }
        return Response.error("invalid token", null);
    }

    public Response<String> purchaseShoppingCart(String userName, String token) {
        if (Security.isValidJWT(token, userName)) {
            return market.purchaseShoppingCart(userName);
        }
        return Response.error("invalid token", null);
    }


}
