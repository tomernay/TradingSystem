package src.main.java.Domain.Market;


import src.main.java.Domain.Market.MarketFacade;
import src.main.java.Domain.Store.StoreData.Permissions;
import src.main.java.Domain.Users.StateOfSubscriber.SubscriberState;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import src.main.java.Domain.Users.User;
import src.main.java.Utilities.Response;

import java.util.List;
import java.util.Map;

public class Market {
    private MarketFacade marketFacade = new MarketFacade();

    public Response<String> loginAsSubscriber(Subscriber subscriber){
        return marketFacade.loginAsSubscriber(subscriber);
    }
    public Response<String> logoutAsSubscriber(Subscriber subscriber){
        return marketFacade.logoutAsSubscriber(subscriber);
    }

    public Response<String> loginAsGuest(User user){
        return marketFacade.loginAsGuest(user);
    }
    public Response<String> logoutAsGuest(User user){
        return marketFacade.logoutAsGuest(user);
    }
    public boolean isStoreOwner(String storeID, String currentUsername) {
        return marketFacade.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> makeStoreOwner(String storeID, String currentUsername, String subscriberUsername) {
        return marketFacade.makeStoreOwner(storeID, currentUsername, subscriberUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return marketFacade.isStoreManager(storeID, currentUsername);
    }

    public Response<String> makeStoreManager(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return marketFacade.makeStoreManager(storeID, currentUsername, subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return marketFacade.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return marketFacade.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return marketFacade.isStoreCreator(storeID, currentUsername);
    }


    public Response<String> openStore(String storeName, String creator) {
        return marketFacade.openStore(storeName, creator);
    }
   
    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return marketFacade.messageResponse(subscriberUsername, answer);
    }

    public MarketFacade getMarketFacade() {
        return marketFacade;
    }

    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        return marketFacade.closeStore(storeID, currentUsername);
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return marketFacade.sendCloseStoreNotification(subscriberNames, storeID);
    }

    public Response<String> register(String username, String password) {
        return marketFacade.register(username, password);
    }

    public Response<Map<String, SubscriberState>> requestEmployeesStatus(String storeID) {
        return marketFacade.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<Permissions>>> requestManagersPermissions(String storeID) {
        return marketFacade.requestManagersPermissions(storeID);
    }

    public Response<String> addProductToShoppingCart(String storeID,String productID,String userName,int quantity) {
        return marketFacade.addProductToShoppingCart(storeID, productID, userName, quantity);
    }
}
