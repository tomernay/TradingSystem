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

    public StoreRepository() {
        this.stores = new HashMap<>();
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

    public void addStore(String storeID,String creator) {
        Store store=new Store(0,storeID,new Inventory(),creator);
        stores.put(storeID, store);
    }

    public Store getStore(String name) {
        return stores.get(name);
    }
}
