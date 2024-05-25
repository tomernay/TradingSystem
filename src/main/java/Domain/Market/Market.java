package Domain.Market;


import Domain.Repo.StoreRepository;
import Domain.Store.Store;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public class Market {

    private static Market instance;

    // Private constructor to prevent instantiation
    private Market() {
        marketFacade = new MarketFacade();
    }

    // Static method to get the single instance
    public static synchronized Market getInstance() {
        if (instance == null) {
            instance = new Market();
        }
        return instance;
    }

    private MarketFacade marketFacade = new MarketFacade();

    public void reset() {
        instance = new Market();
    }

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

    public Response<String> removeProductFromShoppingCart(String userName ,String storeID, String productID) {
        return marketFacade.removeProductFromShoppingCart(userName,storeID, productID);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, String userName, int quantity) {
        return marketFacade.updateProductInShoppingCart(storeID, productID, userName, quantity);
    }


    public Response<String> getShoppingCartContents(String userName) {
        return marketFacade.getShoppingCartContents(userName);
    }

    public Response<String> purchaseShoppingCart(String userName) {
        return marketFacade.purchaseShoppingCart(userName);
    }

    public boolean userExists(String subscriberUsername) {
        return marketFacade.userExists(subscriberUsername);
    }

    public Subscriber getUser(String subscriberUsername) {
        return marketFacade.getUser(subscriberUsername);
    }

    public Store getStore(String storeID) {
        return marketFacade.getStore(storeID);
    }

    public StoreRepository getStoreRepository() {
        return marketFacade.getStoreRepository();
    }

    public Response<String> waiveOwnership(String storeID, String currentUsername) {
        return marketFacade.waiveOwnership(storeID, currentUsername);
    }
}
