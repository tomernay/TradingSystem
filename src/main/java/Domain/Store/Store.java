package Domain.Store;

import Domain.Store.Conditions.*;
import Domain.Store.Discounts.*;
import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.*;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;


//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import javax.persistence.Transient;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

//@Entity
//@Table(name = "stores")
public class Store {

    //    @Id
    private Integer storeID;
    private String storeName;
    private Inventory inventory;
    private Map<String, SubscriberState> subscribers; //<SubscriberUsername, SubscriberState>
    private Map<String, List<Permissions>> managerPermissions; //<ManagerUsername, List<Permissions>>
    private Map<String, List<String>> nominationGraph;
    private Map<String, String> reverseNominationMap;
    private Map<Integer, Discount> discounts = new HashMap<>();///
    private Map<Integer, Condition> policies = new HashMap<>();
    private final AtomicInteger productIDGeneratorPolicy = new AtomicInteger(1);
    private final AtomicInteger productIDGeneratorDiscount = new AtomicInteger(1);


    // Constructor
    public Store(Integer storeID, String name, String creator) {

        this.storeID = storeID;
        this.storeName = name;
        SubscriberState create = new StoreCreator(this, creator);
        subscribers = new HashMap<>();
        subscribers.put(creator, create);
        managerPermissions = new HashMap<>();
        nominationGraph = new HashMap<>();
        reverseNominationMap = new HashMap<>();
        discounts = new HashMap<>();
        policies = new HashMap<>();
    }

    public Store() {
    }

    // Getter and setter for id
    public Integer getId() {
        return storeID;
    }

    public void setId(Integer storeID) {
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
        return Response.success("successfully fetched the managers permissions of the store", managerPermissions);
    }

    public Response<Set<String>> waiveOwnership(String currentUsername) {
        if (subscribers.get(currentUsername) instanceof StoreOwner) {
            Set<String> toRemove = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            queue.add(currentUsername);
            while (!queue.isEmpty()) {
                String current = queue.poll();
                if (nominationGraph.containsKey(current)) {
                    queue.addAll(nominationGraph.get(current));
                }
                toRemove.add(current);
            }
            // Remove all collected nodes
            for (String subscriber : toRemove) {
                subscribers.remove(subscriber);
                managerPermissions.remove(subscriber);
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
            } else {
                SystemLogger.error("[ERROR] The user: " + userName + " tried to " + requiredPermission.toString().toLowerCase().replace('_', ' ') + " but he doesn't have the permission");
                return Response.error(s, null);
            }
        }
        SystemLogger.error("[ERROR] The user: " + userName + " tried to " + requiredPermission.toString().toLowerCase().replace('_', ' ') + " but he doesn't have the permission");
        return Response.error(s, null);
    }


    public Response<String> setProductQuantity(Integer productID, Integer newQuantity, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the quantity of product: " + productID + " to: " + newQuantity + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductQuantity(productID, newQuantity);
    }


    public Response<String> addProductQuantity(Integer productID, Integer amountToAdd, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add " + amountToAdd + " to the quantity of product: " + productID + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductQuantity(productID, amountToAdd);
    }


    public Response<String> getProductName(Integer productID) {
        return inventory.getProductName(productID);
    }

    public Response<String> setProductName(Integer productID, String newName, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the name of product: " + productID + " to: " + newName + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductName(productID, newName);
    }

    public Response<Double> getProductPrice(Integer productID) {
        return inventory.getProductPrice(productID);
    }

    public Response<String> setProductPrice(Integer productID, Double newPrice, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the price of product: " + productID + " to: " + newPrice + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductPrice(productID, newPrice);
    }

    public Response<String> getProductDescription(Integer productID) {
        return inventory.getProductDescription(productID);
    }

    public Response<String> setProductDescription(Integer productID, String newDescription, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set the description of product: " + productID + " to: " + newDescription + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.setProductDescription(productID, newDescription);
    }

    public Response<String> getProductQuantity(Integer productID) {
        return inventory.getProductQuantity(productID);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(String category) {
        return inventory.retrieveProductsByCategoryFrom_OneStore(category);
    }

    public Response<String> retrieveProductCategories(Integer productID) {
        return inventory.retrieveProductCategories(productID);
    }

    public Response<String> assignProductToCategory(Integer productID, String category, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to assign the product: " + productID + " to the category: " + category + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.assignProductToCategory(productID, category);
    }

    public Response<String> removeCategoryFromStore(String category, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove the category: " + category + " from the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.removeCategoryFromStore(category);
    }


    public Response<ProductDTO> getProductFromStore(int productID) {
        return inventory.getProductFromStore(productID);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore() {
        return inventory.getAllProductsFromStore();
    }

    public synchronized Response<String> addProductToStore(String name, String desc, Double price, Integer quantity, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add the product: " + name + " to the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductToStore(storeID, storeName, name, desc, price, quantity);
    }

    public Response<String> addProductToStore(String name, String desc, Double price, Integer quantity, ArrayList<String> categories, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add the product: " + name + " to the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.addProductToStore(storeID, storeName, name, desc, price, quantity, categories);
    }

    public synchronized Response<String> removeProductFromStore(Integer productID, String userName) {
        Response<String> permissionCheck = checkUserPermission(userName, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove the product: " + productID + " from the store but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.removeProductFromStore(productID);
    }

    public Response<ProductDTO> viewProductFromStoreByName(String productName) {
        return inventory.getProductByName(productName);
    }

    public Response<StoreDTO> getStoreByID() {
        return Response.success("The store ID is: " + storeID, new StoreDTO(storeID, storeName));
    }

    public Response<String> getStoreNameByID() {
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
        try {
            if (isStoreManager(username)) {
                return managerPermissions.get(username).contains(Permissions.valueOf(permission));
            }
        } catch (IllegalArgumentException e) {
            return false;
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

    public Response<String> isProductExist(Integer productID) {
        if (inventory.isProductExist(productID)) {
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

    public Response<Boolean> checkPolicy(Map<ProductDTO, Integer> productsInShoppingCart,Boolean isOverEighteen) {
        if (isOverEighteen != null && !isOverEighteen) {
            SystemLogger.error("[ERROR] The user is not over 18");
            return Response.error("Can't purchase Alcohol under 18 years old.", false);
        }
        else {
            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.of(23, 0);
            LocalTime endTime = LocalTime.of(5, 0);
            if (isOverEighteen == null || !(now.isAfter(startTime) || now.isBefore(endTime))) {
                for (Condition c : policies.values()) {
                    if (!c.isValid(productsInShoppingCart)) {
                        SystemLogger.error("[ERROR] The condition: " + c.getConditionID() + " is not valid");
                        return Response.error("The condition: " + c.getConditionID() + " is not valid", false);
                    }
                }
                SystemLogger.info("[SUCCESS] All conditions are valid");
                return Response.success("All conditions are valid", true);
            }
            SystemLogger.error("[ERROR] Can't purchase Alcohol between 23:00 and 05:00");
            return Response.error("You can't purchase Alcohol between 23:00 and 05:00", false);
        }
    }


    public Response<List<ProductDTO>> LockProducts(Map<Integer, Integer> productsShoppingCart,Boolean isOverEighteen) {
        Response<List<ProductDTO>> productDTOList = inventory.LockProducts(productsShoppingCart);
        if (!productDTOList.isSuccess()) {
            return productDTOList;
        }
        Map<ProductDTO, Integer> products = new HashMap<>();
        for (ProductDTO productDTO : productDTOList.getData()) {
            products.put(productDTO, productsShoppingCart.get(productDTO.getProductID()));
        }
        Response<Boolean> checkPolicy = checkPolicy(products,isOverEighteen);
        if (checkPolicy.getData()) {
            return productDTOList;
        } else {
            inventory.unlockProductsBackToStore(productsShoppingCart);
            return Response.error(checkPolicy.getMessage(), null);
        }

    }


    public void unlockShoppingCart(Map<Integer, Integer> stringIntegerMap) {
        inventory.unlockProductsBackToStore(stringIntegerMap);
    }

    public Response<String> CreateDiscount(Integer productID, String category, Double percent, String type, String username) {
        Discount discount;
        if (percent <= 0 || percent > 100) {
            return new Response<>(false, "Discount percent must be between 0 and 1");
        }
        if (!isStoreOwner(username) && !isStoreManager(username) && !isStoreCreator(username)) {
            return new Response<>(false, "Only store owners and managers can create discounts");
        }
        if (productID != null && !isProductExist(productID).isSuccess()) {
            return new Response<>(false, "Product does not exist in store");
        }
        int IdDiscount;
        if (type.equals("simple")) {
            IdDiscount = productIDGeneratorDiscount.getAndIncrement();
            if(productID == null)
                discount = new SimpleDiscount(percent, null, category, IdDiscount, null);
            else
                discount = new SimpleDiscount(percent, productID, category, IdDiscount, getProductName(productID).getData());
            discounts.put(IdDiscount, discount);
        } else {
            return new Response<>(false, "Failed to create discount");
        }
        return Response.success("Discount created successfully", String.valueOf(IdDiscount));
    }

    public Response<Double> CalculateDiscounts(Map<Integer, Integer> productsInStore) {
        double discount = 0;
        Map<ProductDTO, Integer> products = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : productsInStore.entrySet()) {
            Response<ProductDTO> response = inventory.getProductFromStore(entry.getKey());
            if (!response.isSuccess()) {
                return new Response<>(false, "Failed to calculate discount");
            }
            products.put(response.getData(), productsInStore.get(entry.getKey()));
        }
        for (Discount d : discounts.values()) {
            Response<Double> responseDiscount = d.CalculatorDiscount(products);
            if (!responseDiscount.isSuccess()) {
                return new Response<>(false, "Failed to calculate discount");
            }
            discount += responseDiscount.getData();
        }
        return new Response<>(true, "calculate discounts successfull", discount);
    }

    public synchronized Response<String> unlockProductsBackToStore(Map<Integer, Integer> productsInStore) {
        return inventory.unlockProductsBackToStore(productsInStore);
    }

    public Response<String> removeDiscount(Integer discountID) {
        if (discounts.containsKey(discountID)) {
            discounts.remove(discountID);
            SystemLogger.info("[SUCCESS] Discount removed successfully");
            return new Response<>(true, "Discount removed successfully");
        }
        SystemLogger.error("[ERROR] Failed to remove discount");
        return new Response<>(false, "Failed to remove discount");
    }

    public Response<List<DiscountDTO>> getDiscounts() {
        List<DiscountDTO> discounts = new ArrayList<>();
        for (Discount d : this.discounts.values()) {
            discounts.add(buildDiscountDTO(d));
        }
        return Response.success("Successfully fetched the discounts", discounts);
    }

    public Response<List<ConditionDTO>> getPolicies() {
        List<ConditionDTO> policies = new ArrayList<>();
        for (Condition c : this.policies.values()) {
            policies.add(buildConditionDTO(c));
        }
        return Response.success("Successfully fetched the policies", policies);
    }

    public DiscountDTO buildDiscountDTO(Discount d) {
        if (d instanceof SimpleDiscount) {
            if (d.getProductID() == null) {
                return new DiscountDTO(d.getDiscountID(), null, null, storeID, "SIMPLE", d.getCategory(), d.getPercent(), null, null, null);
            }
            return new DiscountDTO(d.getDiscountID(), d.getProductID(), getProductName(d.getProductID()).getData(), storeID, "SIMPLE", d.getCategory(), d.getPercent(), null, null, null);
        } else if (d instanceof MaxDiscount) {
            return new DiscountDTO(d.getDiscountID(), null, null, storeID, "MAX", null, null, buildDiscountDTO(d.getDiscount1()), buildDiscountDTO(d.getDiscount2()), null);
        } else if (d instanceof PlusDiscount) {
            return new DiscountDTO(d.getDiscountID(), null, null, storeID, "PLUS", null, null, buildDiscountDTO(d.getDiscount1()), buildDiscountDTO(d.getDiscount2()), null);
        } else {
            return new DiscountDTO(d.getDiscountID(), null, null, storeID, "CONDITION", null, null, buildDiscountDTO(d.getDiscount1()), null, buildConditionDTO(d.getCondition()));
        }
    }

    private ConditionDTO buildConditionDTO(Condition condition) {
        if (condition instanceof SimpleCondition) {
            if (condition.getProductID() == null) {
                return new ConditionDTO(condition.getConditionID(), null, null, condition.getCategory(), "Simple", condition.getAmount(), condition.getMinAmount(), condition.getMaxAmount(), condition.getPriceIndicator(), null, null, null, null);
            }
            return new ConditionDTO(condition.getConditionID(), condition.getProductID(), String.valueOf(getProductName(condition.getProductID()).getData()), condition.getCategory(), "Simple", condition.getAmount(), condition.getMinAmount(), condition.getMaxAmount(), condition.getPriceIndicator(), null, null, null, null);
        } else if (condition instanceof AndCondition) {
            return new ConditionDTO(condition.getConditionID(), null, null, null, "Complex", null, null, null, null, buildConditionDTO(condition.getCondition1()), buildConditionDTO(condition.getCondition2()), null, "AND");
        } else if (condition instanceof OrCondition) {
            return new ConditionDTO(condition.getConditionID(), null, null, null, "Complex", null, null, null, null, buildConditionDTO(condition.getCondition1()), buildConditionDTO(condition.getCondition2()), null, "OR");
        } else if (condition instanceof XorCondition) {
            return new ConditionDTO(condition.getConditionID(), null, null, null, "Complex", null, null, null, null, buildConditionDTO(condition.getCondition1()), buildConditionDTO(condition.getCondition2()), null, "XOR");
        } else {
            return new ConditionDTO(condition.getConditionID(), null, null, null, "Condition", null, null, null, null, buildConditionDTO(condition.getCondition1()), buildConditionDTO(condition.getCondition2()), null, null);
        }
    }

    public synchronized Response<String> RemoveOrderFromStoreAfterSuccessfulPurchase(Map<Integer, Integer> productsInStore) {
        return inventory.RemoveOrderFromStoreAfterSuccessfulPurchase(productsInStore);
    }

    public Response<String> calculateShoppingCartPrice(Map<Integer, Integer> productsInStore) {
        return inventory.calculateShoppingCartPrice(productsInStore);
    }

    public Response<String> makeComplexDiscount(String username, Integer discountId1, Integer discountId2, String discountType) {
        if (!isStoreOwner(username) && !isStoreManager(username) && !isStoreCreator(username)) {
            return new Response<>(false, "Only store owners and managers can create discounts");
        }
        if (!discounts.containsKey(discountId1) || !discounts.containsKey(discountId2)) {
            return new Response<>(false, "Discounts does not exist in store");
        }
        Discount discount1 = discounts.get(discountId1);
        Discount discount2 = discounts.get(discountId2);
        Discount NewDiscount = null;
        int id = productIDGeneratorDiscount.getAndIncrement();
        if (discountType.equals("MAX")) {
            NewDiscount = new MaxDiscount(discount1, discount2, id);
        }
        if (discountType.equals("PLUS")) {
            NewDiscount = new PlusDiscount(discount1, discount2, id);
        }
        discounts.put(id, NewDiscount);
        discounts.remove(discountId1);
        discounts.remove(discountId2);
        return Response.success("Discount created successfully", String.valueOf(id - 1));
    }

    public Response<String> makeConditionDiscount(String username, Integer discountId, Integer conditionId) {
        if (!isStoreOwner(username) && !isStoreManager(username) && !isStoreCreator(username)) {
            return new Response<>(false, "Only store owners and managers can create discounts");
        }
        if (!discounts.containsKey(discountId)) {
            return new Response<>(false, "Discount does not exist in store");
        }

        if (!policies.containsKey(conditionId)) {
            return new Response<>(false, "Condition does not exist in store");
        }
        Condition condition = policies.get(conditionId);
        Discount discount = discounts.get(discountId);
        int id = productIDGeneratorDiscount.getAndIncrement();
        Discount NewDiscount = new DiscountCondition(discount, condition, id);
        discounts.put(id, NewDiscount);
        discounts.remove(discountId);
        policies.remove(conditionId);
        return new Response<>(true, "Discount created successfully");
    }

    public Response<String> addSimplePolicyToStore(String username, String category, Integer productID, Double amount, Double minAmount, Double maxAmount, Boolean price) {
        if (!isStoreOwner(username) && !isStoreManager(username) && !isStoreCreator(username)) {
            return new Response<>(false, "Only store owners and managers can create discounts");
        }
        if ((productID == null && category == null && price == null) || (productID != null && !isProductExist(productID).isSuccess())) {
            return new Response<>(false, "productID,price and category can't be null");
        }
        if ((minAmount != null && minAmount < 0) || (maxAmount != null && maxAmount < 0)) {
            return new Response<>(false, "minAmount can't be null");
        }
        int id = productIDGeneratorPolicy.getAndIncrement();
        if (productID == null) {
            policies.put(id, new SimpleCondition(id, null, category, amount, minAmount, maxAmount, price, null));
        } else {
            policies.put(id, new SimpleCondition(id, productID, category, amount, minAmount, maxAmount, price, getProductName(productID).getData()));
        }
        return new Response<>(true, "Condition created successfully");
    }

    public Response<String> removeProductFromCategory(Integer productId, String category, String username) {
        Response<String> permissionCheck = checkUserPermission(username, Permissions.MANAGE_PRODUCTS);
        if (!permissionCheck.isSuccess()) {
            SystemLogger.error("[ERROR] " + username + " tried to assign the product: " + productId + " to the category: " + category + " but he doesn't have the permission");
            return permissionCheck;
        }
        return inventory.removeProductFromCategory(productId, category);
    }

    public Response<String> makeComplexPolicy(String username, Integer policyId1, Integer policyId2, String conditionType) {
        if (!isStoreOwner(username) && !isStoreManager(username) && !isStoreCreator(username)) {
            return new Response<>(false, "Only store owners and managers can create discounts");
        }
        if (!policies.containsKey(policyId1) || !policies.containsKey(policyId2)) {
            return new Response<>(false, "Discounts does not exist in store");
        }
        Condition policy1 = policies.get(policyId1);
        Condition policy2 = policies.get(policyId2);
        Condition NewPolicy = null;
        ConditionType type = ConditionType.valueOf(conditionType);
        int id = productIDGeneratorPolicy.getAndIncrement();
        if (type.equals(ConditionType.AND)) {
            NewPolicy = new AndCondition(policy1, policy2, id);
        }
        if (type.equals(ConditionType.OR)) {
            NewPolicy = new OrCondition(policy1, policy2, id);
        }
        if (type.equals(ConditionType.XOR)) {
            NewPolicy = new XorCondition(policy1, policy2, id);
        }
        policies.put(id, NewPolicy);
        policies.remove(policyId1);
        policies.remove(policyId2);
        return new Response<>(true, "Discount created successfully");
    }


    public Response<String> makeConditionPolicy(String username, Integer policyId, Integer conditionId) {
        if (!isStoreOwner(username) && !isStoreManager(username) && !isStoreCreator(username)) {
            return new Response<>(false, "Only store owners and managers can create discounts");
        }
        if (!policies.containsKey(policyId)) {
            return new Response<>(false, "Discount does not exist in store");
        }

        if (!policies.containsKey(conditionId)) {
            return new Response<>(false, "Condition does not exist in store");
        }
        Condition condition = policies.get(conditionId);
        Condition policy = policies.get(policyId);
        int Id = productIDGeneratorPolicy.getAndIncrement();
        Condition NewPolicy = new PolicyCondition(policy, condition, Id);
        policies.put(Id, NewPolicy);
        policies.remove(policyId);
        policies.remove(conditionId);
        return Response.success("Discount created successfully", String.valueOf(Id));
    }

    public Response<String> removePolicy(Integer policyId) {
        if (policies.containsKey(policyId)) {
            policies.remove(policyId);
            SystemLogger.info("[SUCCESS] Policy removed successfully");
            return new Response<>(true, "Discount removed successfully");
        }
        SystemLogger.error("[ERROR] Failed to remove discount");
        return new Response<>(false, "Failed to remove discount");
    }

    public boolean isNominatorOf(String username, String manager) {
        return nominationGraph.containsKey(username) && nominationGraph.get(username).contains(manager);
    }

    public Response<ArrayList<String>> retrieveAllCategoriesFromAllStore() {
        return inventory.retrieveAllCategoriesFromAllStore();
    }

    public Response<Boolean> isExistAlcohol(Map<Integer, Integer> productsInStore) {
        return inventory.isExistAlcohol(productsInStore);
    }

    public List<String> getDiscountsStrings() {
        List<String> discounts = new ArrayList<>();
        for (Discount d : this.discounts.values()) {
            discounts.add(d.toString());
        }
        return discounts;
    }

    public List<String> getPoliciesString() {
        List<String> policies = new ArrayList<>();
        for (Condition c : this.policies.values()) {
            policies.add(c.toString());
        }
        return policies;

    }
}

