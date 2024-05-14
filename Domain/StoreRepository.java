package Domain;

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

    public boolean addManagerPermissions(String storeID, String storeManagerID, String permission) {
        return stores.get(storeID).addManagerPermissions(storeManagerID, permission);
    }

    public boolean removeManagerPermissions(String storeID, String storeManagerID, String permission) {
        return stores.get(storeID).removeManagerPermissions(storeManagerID, permission);
    }

    public boolean isStoreCreator(String storeID, String storeCreatorID) {
        return stores.get(storeID).isStoreCreator(storeCreatorID);
    }
}
