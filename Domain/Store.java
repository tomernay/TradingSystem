package Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
  
    private int id;
    private String name;
    private Inventory inventory;
    private Map<String, SubscriberState> subscribers; //<SubscriberID, SubscriberState>
    private Map<String, List<Permissions>> managerPermissions; //<ManagerID, List<Permissions>>

    // Constructor
    public Store(int id, String name, Inventory inventory) {
        this.id = id;
        this.name = name;
        this.inventory = inventory;
        subscribers = new HashMap<>();
        managerPermissions = new HashMap<>();
    }

    // Getter and setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for inventory
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public boolean isStoreOwner(String storeCreatorID) {
        return subscribers.get(storeCreatorID) instanceof StoreOwner;
    }

    public boolean isStoreManager(String storeOwnerID) {
        return subscribers.get(storeOwnerID) instanceof StoreManager;
    }

    public void setState(String subscriberID, SubscriberState newState) {
        subscribers.put(subscriberID, newState);
    }

    public Message makeNominateOwnerMessage(String subscriberID) {
        if (subscribers.get(subscriberID) == null) {
            subscribers.put(subscriberID, new NormalSubscriber(this, subscriberID));
        }
        return subscribers.get(subscriberID).makeNominateOwnerMessage(subscriberID);
    }

    public Message makeNominateManagerMessage(String subscriberID, List<String> permissions) {
        if (subscribers.get(subscriberID) == null) {
            subscribers.put(subscriberID, new NormalSubscriber(this, subscriberID));
        }
        return subscribers.get(subscriberID).makeNominateManagerMessage(subscriberID, permissions);
    }

    public void nominateOwner(String subscriberID) {
        subscribers.get(subscriberID).changeState(this, subscriberID, new StoreOwner(this, subscriberID));
    }

    public void nominateManager(String subscriberID, List<Permissions> permissions) {
        subscribers.get(subscriberID).changeState(this, subscriberID, new StoreManager(this, subscriberID));
        managerPermissions.put(subscriberID, permissions);
    }

    public boolean addManagerPermissions(String storeManagerID, String permission) {
        if (managerPermissions.get(storeManagerID) != null) {
            List<Permissions> permissions = managerPermissions.get(storeManagerID);
            if (permissions.contains(Permissions.valueOf(permission))) {
                return false;
            }
            permissions.add(Permissions.valueOf(permission));
            managerPermissions.put(storeManagerID, permissions);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeManagerPermissions(String storeManagerID, String permission) {
        if (managerPermissions.get(storeManagerID) != null) {
            List<Permissions> permissions = managerPermissions.get(storeManagerID);
            if (!permissions.remove(Permissions.valueOf(permission))) {
                return false;
            }
            managerPermissions.put(storeManagerID, permissions);
            return true;
        } else {
            return false;
        }
    }

    public boolean isStoreCreator(String storeCreatorID) {
        return subscribers.get(storeCreatorID) instanceof StoreCreator;
    }
}
