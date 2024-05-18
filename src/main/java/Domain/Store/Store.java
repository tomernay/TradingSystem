package src.main.java.Domain.Store;

import src.main.java.Domain.Store.Inventory.Inventory;
import src.main.java.Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import src.main.java.Domain.Store.StoreData.Permissions;
import src.main.java.Domain.Users.StateOfSubscriber.*;
import src.main.java.Domain.Users.Subscriber.Messages.Message;
import src.main.java.Utilities.Response;
import src.main.java.Utilities.SystemLogger;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private String id;
    private String name;
    private Inventory inventory;
    private Map<String, SubscriberState> subscribers; //<SubscriberUsername, SubscriberState>
    private Map<String, List<Permissions>> managerPermissions; //<ManagerUsername, List<Permissions>>
    //yair added
    private HashMap<String,PayByBid> payByBids;

    // Constructor
    public Store(String id, String name, Inventory inventory, String creator) {
        this.id = id;
        this.name = name;
        this.inventory = inventory;
        SubscriberState create=new StoreCreator(this,creator);
        subscribers = new HashMap<>();
        subscribers.put(creator,create);
        managerPermissions = new HashMap<>();
        payByBids=new HashMap<>();
    }

    // Getter and setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isStoreOwner(String currentUsername) {
        if (subscribers.get(currentUsername) == null) {
            return false;
        }
        return subscribers.get(currentUsername) instanceof StoreOwner;
    }

    public boolean isStoreManager(String currentUsername) {
        if (subscribers.get(currentUsername) == null) {
            return false;
        }
        return subscribers.get(currentUsername) instanceof StoreManager;
    }

    public void setState(String subscriberName, SubscriberState newState) {
        subscribers.put(subscriberName, newState);
    }

    public Response<Message> makeNominateOwnerMessage(String subscriberUsername) {
        if (subscribers.get(subscriberUsername) == null) {
            subscribers.put(subscriberUsername, new NormalSubscriber(this, subscriberUsername));
        }
        return subscribers.get(subscriberUsername).makeNominateOwnerMessage(subscriberUsername);
    }

    public Response<Message> makeNominateManagerMessage(String subscriberUsername, List<String> permissions) {
        if (subscribers.get(subscriberUsername) == null) {
            subscribers.put(subscriberUsername, new NormalSubscriber(this, subscriberUsername));
        }
        return subscribers.get(subscriberUsername).makeNominateManagerMessage(subscriberUsername, permissions);
    }

    public void nominateOwner(String subscriberName) {
        subscribers.get(subscriberName).changeState(this, subscriberName, new StoreOwner(this, subscriberName));
    }

    public void nominateManager(String subscriberName, List<Permissions> permissions) {
        subscribers.get(subscriberName).changeState(this, subscriberName, new StoreManager(this, subscriberName));
        managerPermissions.put(subscriberName, permissions);
    }

    public Response<String> addManagerPermissions(String subscriberName, String permission) {
        List<Permissions> permissions = managerPermissions.get(subscriberName);
        if (permissions == null || !Permissions.exists(permission)) {
            SystemLogger.error("[ERROR] The permission: " + permission + " doesn't exist");
            return Response.error("The permission: " + permission + " doesn't exist", null);
        }
        if (permissions.contains(Permissions.valueOf(permission))) {
            SystemLogger.error("[ERROR] The manager: " + subscriberName + " already has the permission: " + permission + " on the store: " + name);
            return Response.error("The manager: " + subscriberName + " already has the permission: " + permission + " on the store: " + name, null);
        }
        permissions.add(Permissions.valueOf(permission));
        managerPermissions.put(subscriberName, permissions);
        SystemLogger.info("[INFO] Added the permission: " + permission + " to the manager: " + subscriberName + " of store: " + name);
        return Response.success("Added the permission: " + permission + " to the manager: " + subscriberName + " of store: " + name, null);

    }

    public Response<String> removeManagerPermissions(String subscriberName, String permission) {
        List<Permissions> permissions = managerPermissions.get(subscriberName);
        if (permissions == null || !Permissions.exists(permission)) {
            return Response.error("The permission: " + permission + " doesn't exist", null);
        }
        if (!permissions.remove(Permissions.valueOf(permission))) {
            return Response.error("The manager: " + subscriberName + " doesn't have the permission: " + permission + " on the store: " + name, null);
        }
        managerPermissions.put(subscriberName, permissions);
        SystemLogger.info("[INFO] Removed the permission: " + permission + " from the manager: " + subscriberName + " of store: " + name);
        return Response.success("Removed the permission: " + permission + " from the manager: " + subscriberName + " of store: " + name, null);

    }

    public boolean isStoreCreator(String currentUsername) {
        if (subscribers.get(currentUsername) == null) {
            return false;
        }
        return subscribers.get(currentUsername) instanceof StoreCreator;
    }

//yair added
    public Map<String, SubscriberState> getSubscribers() {
        return subscribers;
    }

    public void addPayByBid(PayByBid p,String user){
        payByBids.put(user,p);
    }
    public void removePayByBid(String user){
        payByBids.remove(user);
    }


}
