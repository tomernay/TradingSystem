package Domain.Store;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.*;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;
import java.util.concurrent.atomic.AtomicInteger;



import java.util.*;

public class Store {

    //private Integer id = 0;
    //make store ID volatile

    //atomic integer
//    private String storeID;
    AtomicInteger storeID = new AtomicInteger(0);


    private String storeName;
    private Inventory inventory;
    private Map<String, SubscriberState> subscribers; //<SubscriberUsername, SubscriberState>
    private Map<String, List<Permissions>> managerPermissions; //<ManagerUsername, List<Permissions>>
    private Map<String, List<String>> nominationGraph;
    private Map<String, String> reverseNominationMap;

    // Constructor
    public Store(String storeID, String name, String creator) {

        this.storeID.set(Integer.parseInt(storeID));
        this.storeName = name;
        SubscriberState create = new StoreCreator(this, creator);
        subscribers = new HashMap<>();
        subscribers.put(creator, create);
        managerPermissions = new HashMap<>();
        nominationGraph = new HashMap<>();
        reverseNominationMap = new HashMap<>();
    }

    public Store() {
    }

    // Getter and setter for id
    public String getId() {
        return storeID.toString();
    }

    public void setId(String storeID) {

        this.storeID.set(Integer.parseInt(storeID));
    }

    // Getter and setter for name
    public String getName() {
        return storeName;
    }

    public void setName(String name) {
        this.storeName = name;
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
            return Response.error("You're not the store owner / creator", null);
        }
        if (isStoreOwner(subscriberUsername)) { //The subscriber is already the store owner
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store owner but " + subscriberUsername + " is already the store owner");
            return Response.error(subscriberUsername + " is already the store owner", null);
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
            return Response.error("You're not the store owner / creator", null);
        }
        if (isStoreOwner(subscriberUsername)) { //The subscriber is already the store owner
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store manager but " + subscriberUsername + " is already the store owner");
            return Response.error(subscriberUsername + " is already the store owner", null);
        }
        if (isStoreManager(subscriberUsername)) { //The subscriber is already the store manager
            SystemLogger.error("[ERROR] " + nominatorUsername + " tried to nominate: " + subscriberUsername + " to the store manager but " + subscriberUsername + " is already the store manager");
            return Response.error(subscriberUsername + " is already the store manager", null);
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
        SystemLogger.info("[SUCCESS] The user: " + subscriberUsername + " has been nominated as the store owner in store: " + storeName);
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
        SystemLogger.info("[SUCCESS] The user: " + subscriberUsername + " has been nominated as the store manager in store: " + storeName);
        return Response.success("The user: " + subscriberUsername + " has been nominated as the store manager", null);
    }

    public synchronized Response<String> addManagerPermissions(String subscriberName, String permission) {
        List<Permissions> permissions = managerPermissions.get(subscriberName);
        if (permissions == null || !Permissions.exists(permission)) {
            SystemLogger.error("[ERROR] Tried to add the permission: " + permission + " to the manager: " + subscriberName + " of store: " + storeName + " but the permission doesn't exist");
            return Response.error("The permission: " + permission + " doesn't exist", null);
        }
        if (permissions.contains(Permissions.valueOf(permission))) {
            SystemLogger.error("[ERROR] Tried to add the permission: " + permission + " to the manager: " + subscriberName + " of store: " + storeName + " but the manager already has the permission");
            return Response.error("The manager: " + subscriberName + " already has the permission: " + permission + " on the store: " + storeName, null);
        }
        permissions.add(Permissions.valueOf(permission));
        managerPermissions.put(subscriberName, permissions);
        SystemLogger.info("[SUCCESS] Added the permission: " + permission + " to the manager: " + subscriberName + " of store: " + storeName);
        return Response.success("Added the permission: " + permission + " to the manager: " + subscriberName + " of store: " + storeName, null);

    }

    public synchronized Response<String> removeManagerPermissions(String subscriberName, String permission) {
        List<Permissions> permissions = managerPermissions.get(subscriberName);
        if (permissions == null || !Permissions.exists(permission)) {
            SystemLogger.error("[ERROR] Tried to remove the permission: " + permission + " from the manager: " + subscriberName + " of store: " + storeName + " but the permission doesn't exist");
            return Response.error("The permission: " + permission + " doesn't exist", null);
        }
        if (!permissions.remove(Permissions.valueOf(permission))) {
            SystemLogger.error("[ERROR] Tried to remove the permission: " + permission + " from the manager: " + subscriberName + " of store: " + storeName + " but the manager doesn't have the permission");
            return Response.error("The manager: " + subscriberName + " doesn't have the permission: " + permission + " on the store: " + storeName, null);
        }
        managerPermissions.put(subscriberName, permissions);
        SystemLogger.info("[SUCCESS] Removed the permission: " + permission + " from the manager: " + subscriberName + " of store: " + storeName);
        return Response.success("Removed the permission: " + permission + " from the manager: " + subscriberName + " of store: " + storeName, null);

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

    public Response<Map<String, String>> getSubscribersResponse() {
        //lock

        Map<String, String> subscribers = new HashMap<>();
        for (Map.Entry<String, SubscriberState> entry : this.subscribers.entrySet()) {
            subscribers.put(entry.getKey(), entry.getValue().toString());
        }
        return Response.success("successfuly fetched the subscribers states of the store", subscribers);
    }

    public Response<Map<String, List<String>>> getManagersPermissionsResponse() {
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
            SystemLogger.info("[SUCCESS] " + subscriberUsername + " successfully removed from the store: " + storeName);
            subscribers.remove(subscriberUsername);
        }
        SystemLogger.error("[ERROR] tried to remove " + subscriberUsername + " from the store: " + storeName + " but he's not a subscriber of the store");
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
            SystemLogger.error("[ERROR] tried to remove the store subscription for " + currentUsername + " from the store: " + storeName + " but he's not a subscriber of the store");
            return Response.error("The user you're trying to remove the store subscription for is not a subscriber of the store.", null);
        }
        subscribers.remove(currentUsername);
        SystemLogger.info("[SUCCESS] Successfully removed the store subscription for the user: " + currentUsername + " from the store: " + storeName);
        return Response.success("Successfully removed the store subscription for the user: " + currentUsername, null);
    }

    private Response<String> checkUserPermission(String userName, Permissions requiredPermission) {
        if (!subscribers.containsKey(userName)) {
            SystemLogger.error("[ERROR] The user: " + userName + " is not a subscriber of the store: " + storeName);
            return Response.error("The user: " + userName + " can't perform this action", null);
        }

        if (isStoreOwner(userName) || isStoreCreator(userName)) {
            SystemLogger.info("[SUCCESS] Permission granted to the user: " + userName + " to " + requiredPermission.toString().toLowerCase().replace('_', ' '));
            return Response.success("Permission granted", null);
        }

        String s = "The user: " + userName + " doesn't have the permission to " + requiredPermission.toString().toLowerCase().replace('_', ' ');
        if (isStoreManager(userName)) {
            if (managerPermissions.get(userName).contains(requiredPermission)) {
                SystemLogger.info("[SUCCESS] Permission granted to the user: " + userName + " to " + requiredPermission.toString().toLowerCase().replace('_', ' '));
                return Response.success("Permission granted", null);
            }
            else {
                SystemLogger.error("[ERROR] The user: " + userName + " tried to " + requiredPermission.toString().toLowerCase().replace('_', ' ') + " but he doesn't have the permission");
                return Response.error(s, null);
            }
        }
        SystemLogger.error("[ERROR] The user: " + userName + " tried to " + requiredPermission.toString().toLowerCase().replace('_', ' ') + " but he doesn't have the permission");
        return Response.error(s, null);
    }


    public Response<String> setProductQuantity(int productID, int newQuantity, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.EDIT_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the quantity of product: " + productID + " to: " + newQuantity + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductQuantity(productID, newQuantity);
    }



    public Response<String> addProductQuantity(int productID, int amountToAdd, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.EDIT_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add " + amountToAdd + " to the quantity of product: " + productID + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductQuantity(productID, amountToAdd);
    }


    public Response<String> getProductName(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the name of product: " + productID + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.getProductName(productID);
    }

    public Response<String> setProductName(int productID, String newName, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.EDIT_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the name of product: " + productID + " to: " + newName + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductName(productID, newName);
    }

    public Response<String> getProductPrice(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the price of product: " + productID + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.getProductPrice(productID);
    }

    public Response<String> setProductPrice(int productID, int newPrice, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.EDIT_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the price of product: " + productID + " to: " + newPrice + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductPrice(productID, newPrice);
    }

    public Response<String> getProductDescription(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the description of product: " + productID + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.getProductDescription(productID);
    }

    public Response<String> setProductDescription(int productID, String newDescription, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.EDIT_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the description of product: " + productID + " to: " + newDescription + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductDescription(productID, newDescription);
    }

    public Response<String> getProductQuantity(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the quantity of product: " + productID + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.getProductQuantity(productID);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategory(String category, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to retrieve the products by category: " + category + " but he doesn't have the permission");
            return Response.error(permissionCheck.getMessage(), null);
        }
        return inventory.retrieveProductsByCategory(category);
    }

    public Response<String> retrieveProductCategories(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to retrieve the categories of product: " + productID + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.retrieveProductCategories(productID);
    }

    public Response<String> assignProductToCategory(int productID, String category, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.EDIT_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to assign the product: " + productID + " to the category: " + category + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.assignProductToCategory(productID, category);
    }

    public Response<String> removeCategoryFromStore(String category, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.REMOVE_PRODUCT_CATEGORY);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove the category: " + category + " from the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.removeCategoryFromStore(category);
    }


    public Response<ProductDTO> getProductFromStore(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the product: " + productID + " but he doesn't have the permission");
            return Response.error(permissionCheck.getMessage(), null);
        }
        return inventory.getProductFromStore(productID);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get all the products of the store but he doesn't have the permission");
            return Response.error(permissionCheck.getMessage(), null);
        }
        return inventory.getAllProductsFromStore();
    }

    public Response<String> getStoreIDbyName(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_DETAILS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the store ID but he doesn't have the permission");
            return permissionCheck;
        }
        return Response.success("The store ID is: " + storeID.toString(), storeID.toString());
    }

    public Response<String> addProductToStore(String name, String desc, int price, int quantity, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.ADD_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add the product: " + name + " to the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductToStore(storeID.toString(), storeName,name, desc, price, quantity);
    }

    public Response<String> addProductToStore(String name, String desc, int price, int quantity, ArrayList<String> categories, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.ADD_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add the product: " + name + " to the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductToStore(storeID.toString(), storeName, name, desc, price, quantity, categories);
    }

    public Response<String> removeProductFromStore(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.REMOVE_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove the product: " + productID + " from the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.removeProductFromStore(productID);
    }

    public Response<ProductDTO> getProductByName(String productName, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the product: " + productName + " but he doesn't have the permission");
            return Response.error(permissionCheck.getMessage(), null);
        }
        return inventory.getProductByName(productName);
    }

    public Response<String> getStoreIDByName(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_DETAILS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the store ID but he doesn't have the permission");
            return permissionCheck;
        }
        return Response.success("The store ID is: " + storeID.toString(), storeID.toString());
    }

    public Response<StoreDTO> getStoreByID(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_DETAILS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the store by ID but he doesn't have the permission");
            return Response.error(permissionCheck.getMessage(), null);
        }
        return Response.success("The store ID is: " + storeID, new StoreDTO(storeID.toString(), storeName));
    }

    public Response<String> getStoreNameByID(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_DETAILS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the store name by ID but he doesn't have the permission");
            return permissionCheck;
        }
        return Response.success("The store name is: " + storeName, storeName);
    }

    public boolean isStoreSubscriber(String userName) {
        if (subscribers.get(userName) == null) {
            return false;
        }
        return subscribers.get(userName) instanceof NormalSubscriber;
    }

    public boolean hasPermission(String username, String permission) {
        if (subscribers.get(username) == null) {
            return false;
        }
        if (isStoreOwner(username) || isStoreCreator(username)) {
            return true;
        }
        if (isStoreManager(username)) {
            return managerPermissions.get(username).contains(Permissions.valueOf(permission));
        }
        return false;
    }

    public List<String> getManagerPermissions(String username) {
        List<String> permissions = new ArrayList<>();
        if (isStoreManager(username)) {
            for (Permissions p : managerPermissions.get(username)) {
                permissions.add(p.toString());
            }
        }
        return permissions;
    }
}
