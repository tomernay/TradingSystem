package Domain.Store;

import Domain.Store.Inventory.Inventory;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.*;
import Domain.Users.Subscriber.Messages.Message;
import Domain.Users.Subscriber.Subscriber;
import Utilities.Response;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
  
    private String id;
    private String name;
    private Inventory inventory;
    private Map<String, SubscriberState> subscribers; //<SubscriberID, SubscriberState>
    private Map<String, List<Permissions>> managerPermissions; //<ManagerID, List<Permissions>>
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

    public boolean isStoreOwner(String storeOwnerID) {
        if (subscribers.get(storeOwnerID) == null) {
            return false;
        }
        return subscribers.get(storeOwnerID) instanceof StoreOwner;
    }

    public boolean isStoreManager(String storeManagerID) {
        if (subscribers.get(storeManagerID) == null) {
            return false;
        }
        return subscribers.get(storeManagerID) instanceof StoreManager;
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

    public Response<String> addManagerPermissions(String storeManagerID, String permission) {
        List<Permissions> permissions = managerPermissions.get(storeManagerID);
        if (permissions.contains(Permissions.valueOf(permission))) {
            return Response.error("The manager: " + storeManagerID + " already has the permission: " + permission + " on the store: " + name, null);
        }
        permissions.add(Permissions.valueOf(permission));
        managerPermissions.put(storeManagerID, permissions);
        return Response.success("Added the permission: " + permission + " to the manager: " + storeManagerID + " of store: " + name, null);

    }

    public Response<String> removeManagerPermissions(String storeManagerID, String permission) {
        List<Permissions> permissions = managerPermissions.get(storeManagerID);
        if (!permissions.remove(Permissions.valueOf(permission))) {
            return Response.error("The manager: " + storeManagerID + " doesn't have the permission: " + permission + " on the store: " + name, null);
        }
        managerPermissions.put(storeManagerID, permissions);
        return Response.success("Removed the permission: " + permission + " from the manager: " + storeManagerID + " of store: " + name, null);

    }

    public boolean isStoreCreator(String storeCreatorID) {
        if (subscribers.get(storeCreatorID) == null) {
            return false;
        }
        return subscribers.get(storeCreatorID) instanceof StoreCreator;
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
