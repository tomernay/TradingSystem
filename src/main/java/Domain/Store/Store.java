package Domain.Store;

import Domain.Store.Inventory.Inventory;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.*;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;


import java.util.*;

public class Store  {

    //private Integer id = 0;
    private String storeID;
    private String name;
    private Inventory inventory;
    private Map<String, SubscriberState> subscribers; //<SubscriberUsername, SubscriberState>
    private Map<String, List<Permissions>> managerPermissions; //<ManagerUsername, List<Permissions>>
    private Map<String, List<String>> nominationGraph;
    private Map<String, String> reverseNominationMap;
    //yair added
    private HashMap<String,PayByBid> payByBids;

    // Constructor
    public Store(String storeID ,String name, Inventory inventory, String creator) {

        this.storeID = storeID;
        this.name = name;
        this.inventory = inventory;
        SubscriberState create=new StoreCreator(this,creator);
        subscribers = new HashMap<>();
        subscribers.put(creator,create);
        managerPermissions = new HashMap<>();
        payByBids=new HashMap<>();
        nominationGraph = new HashMap<>();
        reverseNominationMap = new HashMap<>();
    }

    public  Store(){}

    // Getter and setter for id
    public String getId() {
        return storeID;
    }

    public void setId(String storeID) {

        this.storeID = storeID;
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

    public Response<Message> makeNominateOwnerMessage(String subscriberUsername, String nominatorUsername) {
        if (subscribers.get(subscriberUsername) == null) {
            subscribers.put(subscriberUsername, new NormalSubscriber(this, subscriberUsername));
            return subscribers.get(subscriberUsername).makeNominateOwnerMessage(subscriberUsername, false, nominatorUsername);
        }
        return subscribers.get(subscriberUsername).makeNominateOwnerMessage(subscriberUsername, true, nominatorUsername);
    }

    public Response<Message> makeNominateManagerMessage(String subscriberUsername, List<String> permissions, String nominatorUsername) {
        if (subscribers.get(subscriberUsername) == null) {
            subscribers.put(subscriberUsername, new NormalSubscriber(this, subscriberUsername));
            return subscribers.get(subscriberUsername).makeNominateManagerMessage(subscriberUsername, permissions, false, nominatorUsername);
        }
        return subscribers.get(subscriberUsername).makeNominateManagerMessage(subscriberUsername, permissions, true, nominatorUsername);
    }

    public void nominateOwner(String subscriberName, String nominatorUsername) {
        nominationGraph.putIfAbsent(nominatorUsername, new ArrayList<>());
        nominationGraph.get(nominatorUsername).add(subscriberName);
        reverseNominationMap.put(subscriberName, nominatorUsername);
        subscribers.get(subscriberName).changeState(this, subscriberName, new StoreOwner(this, subscriberName, nominatorUsername));
    }

    public void nominateManager(String subscriberName, List<Permissions> permissions, String nominatorUsername) {
        nominationGraph.putIfAbsent(nominatorUsername, new ArrayList<>());
        nominationGraph.get(nominatorUsername).add(subscriberName);
        reverseNominationMap.put(subscriberName, nominatorUsername);
        subscribers.get(subscriberName).changeState(this, subscriberName, new StoreManager(this, subscriberName, nominatorUsername));
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

    public Response<Map<String,SubscriberState>> getSubscribersResponse(){
        return Response.success("successfuly fetched the subscribers states of the store", subscribers);
    }

    public Response<Map<String,List<Permissions>>> getManagersPermissionsResponse(){
        return Response.success("successfuly fetched the managers permissions of the store", managerPermissions);
    }

    public Map<String, SubscriberState> getSubscribersMap() {
        return subscribers;
    }

    public Map<String, List<Permissions>> getManagersPermissions() {
        return managerPermissions;
    }

    public void removeSubscriber(String subscriberUsername) {
        if (subscribers.containsKey(subscriberUsername)) {
            subscribers.remove(subscriberUsername);
        }
    }

    public Response<Set<String>> waiveOwnership(String currentUsername) {
        if (subscribers.get(currentUsername) == null) {
            return Response.error("The user you're trying to waive ownership for is not the store owner.", null);
        }
        if (subscribers.get(currentUsername) instanceof StoreOwner) {
            Set<String> toRemove = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            queue.add(currentUsername);
            while (!queue.isEmpty()) {
                String current = queue.poll();
                if (nominationGraph.containsKey(current)) {
                    for (String nominee : nominationGraph.get(current)) {
                        queue.add(nominee);
                    }
                }
                toRemove.add(current);
            }
            // Remove all collected nodes
            for (String subscriber : toRemove) {
                subscribers.remove(subscriber);
                if (managerPermissions.containsKey(subscriber)) {
                    managerPermissions.remove(subscriber);
                }
                nominationGraph.remove(subscriber);
                reverseNominationMap.remove(subscriber);
                // Remove from any nominator's list
                if (reverseNominationMap.containsKey(subscriber)) {
                    String nominator = reverseNominationMap.get(subscriber);
                    nominationGraph.get(nominator).remove(subscriber);
                }
            }
            return Response.success("Successfully waived ownership of the store and remove all of nominees", toRemove);
        }
        return Response.error("The user you're trying to waive ownership for is not the store owner.", null);
    }
}
