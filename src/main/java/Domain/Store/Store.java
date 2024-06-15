package Domain.Store;

import Domain.Store.Discounts.Discount;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Discounts.SimpleDiscount;
import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Policys.Policys;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.*;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {

    private String storeID;
    private String storeName;
    private Inventory inventory;
    private Map<String, SubscriberState> subscribers; //<SubscriberUsername, SubscriberState>
    private Map<String, List<Permissions>> managerPermissions; //<ManagerUsername, List<Permissions>>
    private Map<String, List<String>> nominationGraph;
    private Map<String, String> reverseNominationMap;
    private Map<Integer, Discount> discounts = new HashMap<>();///
    private List<Policys> policys = new ArrayList<>();
    private final AtomicInteger productIDGenerator = new AtomicInteger(1);



    // Constructor
    public Store(String storeID, String name, String creator) {

        this.storeID = storeID;
        this.storeName = name;
        SubscriberState create = new StoreCreator(this, creator);
        subscribers = new HashMap<>();
        subscribers.put(creator, create);
        managerPermissions = new HashMap<>();
        nominationGraph = new HashMap<>();
        reverseNominationMap = new HashMap<>();
        discounts = new HashMap<>();
        policys = new ArrayList<>();


    }

    public Store() {
    }

    // Getter and setter for id
    public String getId() {
        return storeID;
    }

    public void setId(String storeID) {

        this.storeID = storeID;
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

    private synchronized Response<String> checkUserPermission(String userName, Permissions requiredPermission) {
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
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to get the name of product: " + productID + " but he doesn't have the permission");
//            return permissionCheck;
//        }
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
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to get the price of product: " + productID + " but he doesn't have the permission");
//            return permissionCheck;
//        }
        return inventory.getProductPrice(productID);
    }

    public Response<String> setProductPrice(int productID, double newPrice, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.EDIT_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the price of product: " + productID + " to: " + newPrice + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductPrice(productID, newPrice);
    }

    public Response<String> getProductDescription(int productID, String userName) {
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to get the description of product: " + productID + " but he doesn't have the permission");
//            return permissionCheck;
//        }
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
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to get the quantity of product: " + productID + " but he doesn't have the permission");
//            return permissionCheck;
//        }
        return inventory.getProductQuantity(productID);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(String category) {
        return inventory.retrieveProductsByCategoryFrom_OneStore(category);
    }

    public Response<String> retrieveProductCategories(int productID, String userName) {
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to retrieve the categories of product: " + productID + " but he doesn't have the permission");
//            return permissionCheck;
//        }
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
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.REMOVE_CATEGORY);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove the category: " + category + " from the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.removeCategoryFromStore(category);
    }


    public Response<ProductDTO> getProductFromStore(int productID, String userName) {
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to get the product: " + productID + " but he doesn't have the permission");
//            return Response.error(permissionCheck.getMessage(), null);
//        }
        return inventory.getProductFromStore(productID);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(String userName) {
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to get all the products of the store but he doesn't have the permission");
//            return Response.error(permissionCheck.getMessage(), null);
//        }
        return inventory.getAllProductsFromStore();
    }

    public Response<String> getStoreIDbyName(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_DETAILS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the store ID but he doesn't have the permission");
            return permissionCheck;
        }
        return Response.success("The store ID is: " + storeID, storeID);
    }

    public synchronized Response<String> addProductToStore(String name, String desc, double price, int quantity, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.ADD_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add the product: " + name + " to the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductToStore(storeID, storeName,name, desc, price, quantity);
    }

    public Response<String> addProductToStore(String name, String desc, double price, int quantity, ArrayList<String> categories, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.ADD_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add the product: " + name + " to the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductToStore(storeID, storeName, name, desc, price, quantity, categories);
    }

    public synchronized Response<String> removeProductFromStore(int productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.REMOVE_PRODUCT);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove the product: " + productID + " from the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.removeProductFromStore(productID);
    }

    public Response<ProductDTO> viewProductFromStoreByName(String productName, String userName) {
//        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_PRODUCT);
//        if (!permissionCheck.isSuccess()) {
//            SystemLogger.error("[ERROR] " + userName + " tried to get the product: " + productName + " but he doesn't have the permission");
//            return Response.error(permissionCheck.getMessage(), null);
//        }
        return inventory.getProductByName(productName);
    }

    public Response<String> getStoreIDByName(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_DETAILS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the store ID but he doesn't have the permission");
            return permissionCheck;
        }
        return Response.success("The store ID is: " + storeID, storeID);
    }

    public Response<StoreDTO> getStoreByID(String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.VIEW_STORE_DETAILS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get the store by ID but he doesn't have the permission");
            return Response.error(permissionCheck.getMessage(), null);
        }
        return Response.success("The store ID is: " + storeID, new StoreDTO(storeID, storeName));
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

    public Response<String> isProductExist(String productID) {
        if (inventory.isProductExist(Integer.valueOf(productID))){
            return Response.success("The product exists in the store", null);
        }
        return Response.error("The product doesn't exist in the store", null);
    }

    public Response<ArrayList<ProductDTO>> viewProductByCategory(String category) {
        return inventory.viewProductByCategory(category);
    }

    public Response<ProductDTO> viewProductByName(String productName) {
        return inventory.viewProductByName(productName);
    }

    public Response<String> isCategoryExist(String category) {
        return inventory.isCategoryExist(category);
    }



    public Response<List<ProductDTO>> lockShoppingCart(Map<String, Integer> productsInStore) {
        return inventory.lockShoppingCart(productsInStore);
    }


    public void unlockShoppingCart(Map<String, Integer> stringIntegerMap) {
        inventory.unlockShoppingCart(stringIntegerMap);
    }

    public Response<String> CreateDiscount(String productID, String category, String percent, String type, String username) {
        Discount discount;
        if (Double.parseDouble(percent) < 0 ||Double.parseDouble(percent) > 1) {
            return new Response<>(false, "Discount percent must be between 0 and 1");
        }
        if (storeID != null || !isStoreOwner(username) || !isStoreManager(username)) {
            return new Response<>(false, "Only store owners and managers can create discounts");
        }
        if (productID != null || isProductExist(productID).isSuccess()) {
            return new Response<>(false, "Product does not exist in store");
        }
        if (category == null && productID == null)
        {
            return new Response<>(false, "productID and category can't be null at the same time");
        }
        int IdDiscount;
        if (type.equals("simple")) {
            IdDiscount = productIDGenerator.getAndIncrement();
            discount = new SimpleDiscount(percent, storeID, productID, category, IdDiscount);
            discounts.put(IdDiscount, discount);
        } else {
            return new Response<>(false, "Failed to create discount");
        }
        return new Response<>(true, "Discount created successfully");
    }

    public Response<String> CalculateDiscounts(Map<String, Integer> productsInStore) {
        double discount = 0;
        List<ProductDTO> products = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : productsInStore.entrySet()) {
            Response<ProductDTO> response = inventory.getProductFromStore(Integer.parseInt(entry.getKey()));
            if (!response.isSuccess()) {
                return new Response<>(false, "Failed to calculate discount");
            }
            products.add(response.getData());
        }
            for (Discount d : discounts.values()) {
                Response<String> responseDiscount = d.CalculatorDiscount(products);
                if (!responseDiscount.isSuccess()) {
                    return new Response<>(false, "Failed to calculate discount");
                }
                discount += Double.parseDouble(responseDiscount.getData());
            }
        return new Response<>(true,"calculate discounts successfull", String.valueOf(discount));
    }

    public synchronized Response<String> ReleaseShoppingCart(Map<String, Integer> productsInStore) {
        return inventory.unlockShoppingCart(productsInStore);
    }

    public Response<String> removeDiscount(String discountID) {
        if (discounts.containsKey(Integer.parseInt(discountID))) {
            discounts.remove(Integer.parseInt(discountID));
            return new Response<>(true, "Discount removed successfully");
        }
        return new Response<>(false, "Failed to remove discount");
    }

    public Response<List<DiscountDTO>> getDiscounts(String username) {
        List<DiscountDTO> discounts = new ArrayList<>();
        for (Discount d : this.discounts.values()) {
            discounts.add(new DiscountDTO(d.getDiscountID(), d.getStoreID(), d.getDiscountType(), d.getPercent(), d.getProductID(), d.getCategory()));
        }
        return Response.success("Successfully fetched the discounts", discounts);
    }

    public synchronized Response<String> ReleaseShoppingCartfromlock(Map<String, Integer> productsInStore) {
        return inventory.ReleaseShoppSingCartfromlock(productsInStore);
    }

    public Response<String> calculatedPriceShoppingCart(Map<String, Integer> productsInStore) {
        return inventory.calculatedPriceShoppingCart(productsInStore);
    }
}

