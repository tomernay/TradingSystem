package Domain.Store;

import Domain.Store.Inventory.Inventory;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.*;
import Utilities.Messages.Message;
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
        if (!isStoreOwner(nominatorUsername) && !isStoreCreator(nominatorUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store owner but " + nominatorUsername + " is not the store owner / creator");
            return Response.error("You're not the store owner / creator",null);
        }
        if (isStoreOwner(subscriberUsername)) { //The subscriber is already the store owner
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store owner but " + subscriberUsername + " is already the store owner");
            return Response.error(subscriberUsername + " is already the store owner",null);
        }
        if (subscribers.get(subscriberUsername) == null) {
            subscribers.put(subscriberUsername, new NormalSubscriber(this, subscriberUsername));
            return subscribers.get(subscriberUsername).makeNominateOwnerMessage(subscriberUsername, false, nominatorUsername);
        }
        return subscribers.get(subscriberUsername).makeNominateOwnerMessage(subscriberUsername, true, nominatorUsername);
    }

    public Response<Message> makeNominateManagerMessage(String subscriberUsername, List<String> permissions, String nominatorUsername) {
        if (!isStoreOwner(nominatorUsername) && !isStoreCreator(nominatorUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store manager but " + nominatorUsername + " is not the store owner / creator");
            return Response.error("You're not the store owner / creator",null);
        }
        if (isStoreOwner(subscriberUsername)) { //The subscriber is already the store owner
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store manager but " + subscriberUsername + " is already the store owner");
            return Response.error(subscriberUsername + " is already the store owner",null);
        }
        if (isStoreManager(subscriberUsername)) { //The subscriber is already the store manager
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store manager but " + subscriberUsername + " is already the store manager");
            return Response.error(subscriberUsername + " is already the store manager",null);
        }
        if (subscribers.get(subscriberUsername) == null) {
            subscribers.put(subscriberUsername, new NormalSubscriber(this, subscriberUsername));
            return subscribers.get(subscriberUsername).makeNominateManagerMessage(subscriberUsername, permissions, false, nominatorUsername);
        }
        return subscribers.get(subscriberUsername).makeNominateManagerMessage(subscriberUsername, permissions, true, nominatorUsername);
    }

    public Response<String> nominateOwner(String subscriberUsername, String nominatorUsername) {
        nominationGraph.putIfAbsent(nominatorUsername, new ArrayList<>());
        nominationGraph.get(nominatorUsername).add(subscriberUsername);
        reverseNominationMap.put(subscriberUsername, nominatorUsername);
        Response<String> response = subscribers.get(subscriberUsername).changeState(this, subscriberUsername, new StoreOwner(this, subscriberUsername, nominatorUsername));
        if (!response.isSuccess()) {
            return response;
        }
        SystemLogger.info("[SUCCESS] The user: " + subscriberUsername + " has been nominated as the store owner in store: " + name);
        return Response.success("The user: " + subscriberUsername + " has been nominated as the store owner", null);
    }

    public Response<String> nominateManager(String subscriberUsername, List<String> permissions, String nominatorUsername) {
        nominationGraph.putIfAbsent(nominatorUsername, new ArrayList<>());
        nominationGraph.get(nominatorUsername).add(subscriberUsername);
        reverseNominationMap.put(subscriberUsername, nominatorUsername);
        Response<String> response = subscribers.get(subscriberUsername).changeState(this, subscriberUsername, new StoreManager(this, subscriberUsername, nominatorUsername));
        if (!response.isSuccess()) {
            return response;
        }
        managerPermissions.put(subscriberUsername, Permissions.convertStringList(permissions));
        SystemLogger.info("[SUCCESS] The user: " + subscriberUsername + " has been nominated as the store manager in store: " + name);
        return Response.success("The user: " + subscriberUsername + " has been nominated as the store manager", null);
    }

    public synchronized Response<String> addManagerPermissions(String subscriberName, String permission) {
        List<Permissions> permissions = managerPermissions.get(subscriberName);
        if (permissions == null || !Permissions.exists(permission)) {
            SystemLogger.error("[ERROR] Tried to add the permission: " + permission + " to the manager: " + subscriberName + " of store: " + name + " but the permission doesn't exist");
            return Response.error("The permission: " + permission + " doesn't exist", null);
        }
        if (permissions.contains(Permissions.valueOf(permission))) {
            SystemLogger.error("[ERROR] Tried to add the permission: " + permission + " to the manager: " + subscriberName + " of store: " + name + " but the manager already has the permission");
            return Response.error("The manager: " + subscriberName + " already has the permission: " + permission + " on the store: " + name, null);
        }
        permissions.add(Permissions.valueOf(permission));
        managerPermissions.put(subscriberName, permissions);
        SystemLogger.info("[SUCCESS] Added the permission: " + permission + " to the manager: " + subscriberName + " of store: " + name);
        return Response.success("Added the permission: " + permission + " to the manager: " + subscriberName + " of store: " + name, null);

    }

    public synchronized Response<String> removeManagerPermissions(String subscriberName, String permission) {
        List<Permissions> permissions = managerPermissions.get(subscriberName);
        if (permissions == null || !Permissions.exists(permission)) {
            SystemLogger.error("[ERROR] Tried to remove the permission: " + permission + " from the manager: " + subscriberName + " of store: " + name + " but the permission doesn't exist");
            return Response.error("The permission: " + permission + " doesn't exist", null);
        }
        if (!permissions.remove(Permissions.valueOf(permission))) {
            SystemLogger.error("[ERROR] Tried to remove the permission: " + permission + " from the manager: " + subscriberName + " of store: " + name + " but the manager doesn't have the permission");
            return Response.error("The manager: " + subscriberName + " doesn't have the permission: " + permission + " on the store: " + name, null);
        }
        managerPermissions.put(subscriberName, permissions);
        SystemLogger.info("[SUCCESS] Removed the permission: " + permission + " from the manager: " + subscriberName + " of store: " + name);
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

    public Response<Map<String,String>> getSubscribersResponse(){
        Map<String, String> subscribers = new HashMap<>();
        for (Map.Entry<String, SubscriberState> entry : this.subscribers.entrySet()) {
            subscribers.put(entry.getKey(), entry.getValue().toString());
        }
        return Response.success("successfuly fetched the subscribers states of the store", subscribers);
    }

    public Response<Map<String,List<String>>> getManagersPermissionsResponse(){
        Map<String, List<String>> managerPermissions = new HashMap<>();
        for (Map.Entry<String, List<Permissions>> entry : this.managerPermissions.entrySet()) {
            managerPermissions.put(entry.getKey(), Permissions.convertPermissionList(entry.getValue()));
        }
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
            SystemLogger.info("[SUCCESS] " + currentUsername + " successfully waived ownership of the store and removed all of his nominees");
            return Response.success("Successfully waived ownership of the store and remove all of nominees", toRemove);
        }
        SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership but is not the store owner");
        return Response.error("You're not the store owner", null);
    }

    public Response<String> removeStoreSubscription(String currentUsername) {
        if (subscribers.get(currentUsername) == null) {
            SystemLogger.error("[ERROR] tried to remove the store subscription for " + currentUsername + " from the store: " + name + " but he's not a subscriber of the store");
            return Response.error("The user you're trying to remove the store subscription for is not a subscriber of the store.", null);
        }
        subscribers.remove(currentUsername);
        SystemLogger.info("[SUCCESS] Successfully removed the store subscription for the user: " + currentUsername + " from the store: " + name);
        return Response.success("Successfully removed the store subscription for the user: " + currentUsername, null);
    }
}
