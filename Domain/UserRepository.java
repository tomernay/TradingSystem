package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    //Hashmap of id and User
    private Map<Integer, Subscriber> users = new HashMap<>();

    public boolean isStoreOwner(String storeID, String storeCreatorID) {
        return users.get(Integer.parseInt(storeCreatorID)).isStoreOwner(storeID);
    }

    public boolean makeStoreOwner(String storeID, String subscriberID) {
        return users.get(Integer.parseInt(subscriberID)).makeStoreOwner(storeID);
    }

    public boolean isStoreManager(String storeID, String storeOwnerID) {
        return users.get(Integer.parseInt(storeOwnerID)).isStoreManager(storeID);
    }

    public boolean makeStoreManager(String storeID, String subscriberID, List<String> permissions) {
        return users.get(Integer.parseInt(subscriberID)).makeStoreManager(storeID, permissions);
    }

    public boolean addManagerPermissions(String storeID, String storeManagerID, String permission) {
        return users.get(Integer.parseInt(storeManagerID)).addManagerPermissions(storeID, permission);
    }

    public boolean removeManagerPermissions(String storeID, String storeManagerID, String permission) {
        return users.get(Integer.parseInt(storeManagerID)).removeManagerPermissions(storeID, permission);
    }

    public boolean isStoreCreator(String storeID, String storeCreatorID) {
        return users.get(Integer.parseInt(storeCreatorID)).isStoreCreator(storeID);
    }
}
