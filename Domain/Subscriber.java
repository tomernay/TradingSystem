package Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subscriber extends User{
    private Map<String, SubscriberState> storeStates = new HashMap<>(); //<StoreID, State>
    private Map<String, List<Permissions>> storePermissions = new HashMap<>(); //<StoreID, List(permissions)>

    public void addStore(String storeID, SubscriberState state, List<String> permissions) {
        storeStates.put(storeID, state);
        storePermissions.put(storeID, Permissions.convertStringList(permissions));
    }

    public void changeState(String storeID, SubscriberState newState) {
        if (storeStates.containsKey(storeID)) {
            SubscriberState currentState = storeStates.get(storeID);
            currentState.changeState(this, storeID, newState);
        } else {
            System.out.println("Subscriber is not associated with storeID: " + storeID);
        }
    }

    public void setState(String storeID, SubscriberState state) {
        storeStates.put(storeID, state);
    }

    public boolean isStoreOwner(String storeID) {
        return storeStates.get(storeID) instanceof StoreOwner;
    }

    public boolean makeStoreOwner(String storeID) {
        storeStates.put(storeID, new StoreOwner());
        return true;
    }

    public boolean isStoreManager(String storeID) {
        return storeStates.get(storeID) instanceof StoreManager;
    }

    public boolean makeStoreManager(String storeID, List<String> permissions) {
        storeStates.put(storeID, new StoreManager());
        storePermissions.put(storeID, Permissions.convertStringList(permissions));
        return true;
    }

    public boolean addManagerPermissions(String storeID, String permission) {
        if (storePermissions.containsKey(storeID)) {
            List<Permissions> permissions = storePermissions.get(storeID);
            if (permissions.contains(Permissions.valueOf(permission))) {
                return false;
            }
            permissions.add(Permissions.valueOf(permission));
            storePermissions.put(storeID, permissions);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeManagerPermissions(String storeID, String permission) {
        if (storePermissions.containsKey(storeID)) {
            List<Permissions> permissions = storePermissions.get(storeID);
            if (!permissions.remove(Permissions.valueOf(permission))) {
                return false;
            }
            storePermissions.put(storeID, permissions);
            return true;
        } else {
            return false;
        }

    }

    public boolean isStoreCreator(String storeID) {
        return storeStates.get(storeID) instanceof StoreCreator;
    }
}
