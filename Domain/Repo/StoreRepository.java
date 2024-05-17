package Domain.Repo;

import Domain.Store.Inventory.Inventory;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;
import Domain.Store.Store;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRepository {
    private Map<String, Store> stores;
    private Integer storeID;

    public StoreRepository() {
        this.stores = new HashMap<>();
        this.storeID = 0;
    }

    public boolean isStoreOwner(String storeID, String storeCreatorID) {
        return stores.get(storeID).isStoreOwner(storeCreatorID);
    }

    public boolean isStoreManager(String storeID, String storeOwnerID) {
        return stores.get(storeID).isStoreManager(storeOwnerID);
    }

    public Message makeNominateOwnerMessage(String storeID, String subscriberID) {
        return stores.get(storeID).makeNominateOwnerMessage(subscriberID);
    }

    public Message makeNominateManagerMessage(String storeID, String subscriberID, List<String> permissions) {
        return stores.get(storeID).makeNominateManagerMessage(subscriberID, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String storeManagerID, String permission) {
        return stores.get(storeID).addManagerPermissions(storeManagerID, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String storeManagerID, String permission) {
        return stores.get(storeID).removeManagerPermissions(storeManagerID, permission);
    }

    public boolean isStoreCreator(String storeID, String storeCreatorID) {
        return stores.get(storeID).isStoreCreator(storeCreatorID);
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
            storeID++;
            stores.put(storeName, store);
            return Response.success("successfully opened the store "+ storeName, null);
        }
        catch (Exception e) {
            return Response.error("couldn't open store "+ storeName, null);
        }
    }

    public Store getStore(String name) {
        return stores.get(name);
    }
}
