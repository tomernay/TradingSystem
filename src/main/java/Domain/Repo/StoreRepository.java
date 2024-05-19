package src.main.java.Domain.Repo;

import src.main.java.Domain.Store.Inventory.Inventory;
import src.main.java.Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import src.main.java.Domain.Store.Store;
import src.main.java.Domain.Users.Subscriber.Messages.Message;
import src.main.java.Utilities.Response;
import src.main.java.Utilities.SystemLogger;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRepository {
    private Map<String, Store> stores;
    private Map<String, Store> deactivatedStores; // <StoreID, Store>
    private Integer storeID;


    public StoreRepository() {
        this.stores = new HashMap<>();
        this.storeID = 0;
        this.deactivatedStores = new HashMap<>();
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return stores.get(storeID).isStoreOwner(currentUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return stores.get(storeID).isStoreManager(currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(String storeID, String currentUsername, String subscriberUsername) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] The user: " + currentUsername + " is not the store owner.");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (isStoreOwner(storeID, subscriberUsername)) { //The subscriber is already the store owner
            SystemLogger.error("[ERROR] The user: " + subscriberUsername + " is already the store owner.");
            return Response.error("The user you're trying to nominate is already the store owner.",null);
        }
        return stores.get(storeID).makeNominateOwnerMessage(subscriberUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] The user: " + currentUsername + " is not the store owner.");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (isStoreOwner(storeID, subscriberUsername)) { //The subscriber is already the store owner
            SystemLogger.error("[ERROR] The user: " + subscriberUsername + " is already the store owner.");
            return Response.error("The user you're trying to nominate is already the store owner.",null);
        }
        if (isStoreManager(storeID, subscriberUsername)) { //The subscriber is already the store manager
            SystemLogger.error("[ERROR] The user: " + subscriberUsername + " is already the store manager.");
            return Response.error("The user you're trying to nominate is already the store manager.",null);
        }
        return stores.get(storeID).makeNominateManagerMessage(subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The storeCreatorID is not the store owner / creator
            SystemLogger.error("[ERROR] The user: " + currentUsername + " is not the store owner.");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] The user: " + subscriberUsername + " is not the store manager.");
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return stores.get(storeID).addManagerPermissions(subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The storeCreatorID is not the store owner / creator
            SystemLogger.error("[ERROR] The user: " + currentUsername + " is not the store owner.");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] The user: " + subscriberUsername + " is not the store manager.");
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return stores.get(storeID).removeManagerPermissions(subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return stores.get(storeID).isStoreCreator(currentUsername);
    }

    //yair added
    public void addPayByBid(HashMap<Integer,Integer> products, double fee, String store,String user){
        Store s=stores.get(store);
        PayByBid pay=new PayByBid(products,fee,s);
        s.addPayByBid(pay,user);
    }

    public Response<String> addStore(String storeName,String creator) {

        try {
            Store store = new Store(storeID.toString() ,storeName ,new Inventory(),creator);
            stores.put(Integer.toString(storeID), store);
            storeID++;
            SystemLogger.info("[INFO] Successfully opened the store: "+ storeName + " with id: " + (storeID - 1));
            return Response.success("Successfully opened the store: "+ storeName + " with id: " + (storeID - 1), null);
        }
        catch (Exception e) {
            SystemLogger.error("[ERROR] couldn't open store "+ storeName);
            return Response.error("couldn't open store "+ storeName, null);
        }
    }

    public Store getStore(String name) {
        return stores.get(name);
    }

    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist");
                return Response.error("Store with ID: " + storeID + " doesn't exist", null);
            }
            else{
                SystemLogger.error("[ERROR] Store with ID: " + storeID + " is already closed");
                return Response.error("Store with ID: " + storeID + " is already closed", null);
            }
        }
        if (!isStoreCreator(storeID, currentUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] The user: " + currentUsername + " is not the store creator.");
            return Response.error("The user trying to do this action is not the store creator.",null);
        }
        Store store = stores.get(storeID);
        deactivatedStores.put(storeID, store);
        stores.remove(storeID);
        SystemLogger.info("[INFO] Store with ID: " + storeID + " was closed successfully");
        return Response.success("Store with ID: " + storeID + " was closed successfully", new ArrayList<>(deactivatedStores.get(storeID).getSubscribers().keySet()));
    }
}
