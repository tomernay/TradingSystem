package Domain.Repo;

import Domain.Store.Inventory.Inventory;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;
import Domain.Store.Store;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRepository {
    private Map<String, Store> stores;
    private Map<String, Store> deactivatedStores; // <StoreID, Store>
    private Integer storeID = 0;

    public StoreRepository() {
        this.stores = new HashMap<>();
        //this.storeID = 0;
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
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (isStoreOwner(storeID, subscriberUsername)) { //The subscriber is already the store owner
            return Response.error("The user you're trying to nominate is already the store owner.",null);
        }
        return stores.get(storeID).makeNominateOwnerMessage(subscriberUsername, currentUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        if (!stores.containsKey(storeID)) {
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (isStoreOwner(storeID, subscriberUsername)) { //The subscriber is already the store owner
            return Response.error("The user you're trying to nominate is already the store owner.",null);
        }
        if (isStoreManager(storeID, subscriberUsername)) { //The subscriber is already the store manager
            return Response.error("The user you're trying to nominate is already the store manager.",null);
        }
        return stores.get(storeID).makeNominateManagerMessage(subscriberUsername, permissions, currentUsername);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        if (!stores.containsKey(storeID)) {
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The storeCreatorID is not the store owner / creator
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return stores.get(storeID).addManagerPermissions(subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        if (!stores.containsKey(storeID)) {
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The storeCreatorID is not the store owner / creator
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return stores.get(storeID).removeManagerPermissions(subscriberUsername, permission);
    }

    public Response<Map<String, SubscriberState>> requestEmployeesStatus(String storeID){
        try{
            return stores.get(storeID).getSubscribersResponse();
        }
        catch (Exception e){
            return Response.error("Invalid storeID.", null);
        }
    }

    public Response<Map<String, List<Permissions>>> requestManagersPermissions(String storeID){
        try{
            return stores.get(storeID).getManagersPermissionsResponse();
        }
        catch (Exception e){
            return Response.error("Invalid storeID.", null);
        }
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
            Store store = new Store(storeID.toString() ,storeName ,creator);
            stores.put(this.storeID.toString(), store);
            this.storeID++;
            return Response.success("successfully opened the store "+ storeName, Integer.toString(this.storeID-1));
        }
        catch (Exception e) {
            return Response.error("couldn't open store "+ storeName, null);
        }
    }

    public Store getStore(String storeID) {
        return stores.get(storeID);
    }




    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return Response.error("Store with ID: " + storeID + " doesn't exist", null);
            }
            else{
                return Response.error("Store with ID: " + storeID + " is already closed", null);
            }
        }
        if (!isStoreCreator(storeID, currentUsername)) { //The subscriber is not the store manager
            return Response.error("The user trying to do this action is not the store creator.",null);
        }
        Store store = stores.get(storeID);
        if (store == null) {
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        deactivatedStores.put(storeID, store);
        stores.remove(storeID);
        return Response.success("Store with ID: " + storeID + " was closed successfully", new ArrayList<>(deactivatedStores.get(storeID).getSubscribers().keySet()));
    }

    public boolean isClosedStore(String storeID){
        return deactivatedStores.containsKey(storeID);
    }

    public boolean isOpenedStore(String storeID){
        return stores.containsKey(storeID);
    }

    public Response<String> setQuantity(int productID, int newQuantity, String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return Response.error("Store with ID: " + storeID + " doesn't exist", null);
            }
            else{
                return Response.error("Store with ID: " + storeID + " is already closed", null);
            }
        }
        return stores.get(storeID).setQuantity(productID, newQuantity, userName);
    }
}
