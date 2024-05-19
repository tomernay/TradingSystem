package Domain.Store;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.*;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
  
    private String id;
    private String name;
    private Inventory inventory; //Dependency Injection via constructor
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

    public Response<String> setInventoryToStore(Inventory inventory) {
        this.inventory = inventory;
        return new Response<>(true, "", "" );
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


    /**
     * Creates a new product for this inventory.
     *
     * @param builder    Product.Builder object containing product details.
     * @param productID  The ID of the product.
     * @return           A Response object containing the result of the addition operation.
     */
    public synchronized Response<Integer> addNewProduct(Product.Builder builder, Integer productID) {
        return inventory.addProduct(builder, productID);
    }


    /**
     * Adds a product's ID to the appropriate categories in the categories map.
     * If a category does not already exist in the map, it is created.
     *
     * @param product The product to add to the categories.
     */
    public void addProductToCategory(Product product) {
        inventory.addProductToCategory(product);
    }

    /**
     * Removes a product from the inventory.
     * This method removes the product with the specified product ID from the inventory.
     *
     * @param productID The ID of the product to be removed.
     * @return A Response object containing the result of the removal operation.
     */
    public synchronized Response<Integer> removeProduct(int productID) {
        return inventory.removeProduct(productID);
    }

    /**
     * Sets the categories for the product with the specified product ID.
     *
     * @param productID  The ID of the product to set categories for.
     * @param categories The list of categories to be set for the product.
     * @throws IllegalArgumentException if the provided productID is not valid.
     */
    public Response<Integer> setProductCategory(int productID, ArrayList<String> categories) {
        return inventory.setProductCategory(productID, categories);
    }

    /**
     * Removes the specified product ID from all categories in the categories map.
     * The method iterates through the categories map and removes the product ID from each list.
     * If a category becomes empty after the removal, the category is also removed from the map.
     *
     * @param productID The ID of the product to be removed from all categories.
     * @throws IllegalArgumentException if the provided productID is not valid.
     */
    public void removeProductFromAllCategories(int productID) {
        inventory.removeProductFromAllCategories(productID);
    }

    /**
     * Retrieves product information in the form of a DTO (Data Transfer Object) given a product ID.
     *
     * @param productID The unique ID of the product to retrieve information for.
     * @return A DTO representing the product information.
     * @throws IllegalArgumentException If the provided product ID is null or if the product does not exist.
     */
    public ProductDTO getProductInfo(Integer productID) {
        return inventory.getProductInfo(productID);
    }

    /**
     * Checks if a product exists in the inventory.
     *
     * @param productID The ID of the product to check.
     * @return true if the product exists, false otherwise.
     */
    public boolean isProductExist(Integer productID) {
        return inventory.isProductExist(productID);
    }

    /**
     * Retrieves the categories that contain the specified product ID from the inventory.
     *
     * @param productId The ID of the product to search for in the categories.
     * @return A JSON string representing the list of category names that include the given product ID.
     * @throws JsonProcessingException If there is an error during JSON processing.
     */
    public synchronized Response<String> getProductsByCategory(int productId) throws JsonProcessingException {
        return inventory.getProductsByCategory(productId);
    }

    /**
     * Retrieves the product with the specified product ID and converts it to a ProductDTO.
     *
     * @param productID The ID of the product to retrieve.
     * @return A ProductDTO object representing the product with the specified product ID.
     */
    public synchronized ProductDTO getProductCategory(int productID) {
        return inventory.getProductCategory(productID);
    }

    /**
     * Sets the product ID of a product.
     *
     * @param oldProductID The current ID of the product.
     * @param newProductID The new ID to set for the product.
     */
    public Response<Integer> setProductID(Integer oldProductID, Integer newProductID) {
        return inventory.setProductID(oldProductID, newProductID);
    }

    /**
     * Retrieves the product with the specified product ID.
     *
     * @param productID The ID of the product to retrieve.
     * @return The Product object corresponding to the given product ID.
     */
    public Response<Product> getProduct(Integer productID) {
        return inventory.getProduct(productID);
    }

    /**
     * Sets the name of the product with the specified product ID.
     *
     * @param productID The ID of the product.
     * @param newName   The new name to set for the product.
     */
    public Response<Integer> setProductName(Integer productID, String newName) {
        return inventory.setProductName(productID, newName);
    }

    /**
     * Sets the description of the product with the specified product ID.
     * @param productID The ID of the product.
     * @param newDesc The new description to set for the product.
     */
    public Response<Integer> setProductDesc(Integer productID, String newDesc) {
        return inventory.setProductDesc(productID, newDesc);
    }

    /**
     * Sets the price of the product with the specified product ID.
     * @param productID The ID of the product.
     * @param newPrice The new price to set for the product.
     */
    public Response<Integer> setPrice(Integer productID, int newPrice) {
        return inventory.setPrice(productID, newPrice);
    }

    /**
     * Sets the quantity of the product with the specified product ID.
     * @param productID The ID of the product.
     * @param newQuantity The new quantity to set for the product.
     */
    public Response<Integer> setQuantity(Integer productID, int newQuantity) {
        return inventory.setQuantity(productID, newQuantity);
    }

    /**
     * Adds the specified value to the quantity of the product with the specified product ID.
     * @param productID The ID of the product.
     * @param valueToAdd The value to add to the quantity of the product.
     */
    public Response<Integer> addQuantity(Integer productID, int valueToAdd) {
        return inventory.addQuantity(productID, valueToAdd);
    }

    /**
     * Retrieves the quantity of the product with the specified product ID.
     *
     * @param productID The ID of the product.
     * @return The quantity of the product.
     * @throws Exception If the product does not exist.
     */
    public Response<Integer> getQuantity(int productID) throws Exception {
        return inventory.getQuantity(productID);
    }

    /**
     * Retrieves the description of the product with the specified product ID.
     *
     * @param productID The ID of the product.
     * @return The description of the product.
     * @throws Exception If the product does not exist.
     */
    public Response<String> getProductDescription(int productID) throws Exception {
        return inventory.getProductDescription(productID);
    }

    /**
     * Retrieves the price of the product with the specified product ID.
     *
     * @param productID The ID of the product.
     * @return The price of the product.
     * @throws Exception If the product does not exist.
     */
    public Response<Integer> getProductPrice(int productID) throws Exception {
        return inventory.getProductPrice(productID);
    }

    public Response<Integer> setStoreIDToProduct(int productID, String storeID){
        return inventory.setStoreIDToProduct(productID ,storeID);
    }

    public Response<String> getProductName(int productID) {
        return inventory.getProductName(productID);
    }

    public Response<Integer> setProductName(int productID ,String storeName) {
        return inventory.setProductName(productID ,storeName);
    }

    public Response<String> removeCategoryFromProduct(int productID ,String category) {
        return inventory.removeCategoryFromProduct(productID, category);
    }

    public Response<Integer> setStoreNameToProduct(int productID ,String storeName) {
        return inventory.setStoreNameToProduct(productID,storeName);
    }

    public ConcurrentHashMap<Integer, Product> getProductsList() {
        return inventory.getProductsList();
    }

    public ConcurrentHashMap<String, ArrayList<Integer>> getCategories() {
        return inventory.getCategories();
    }

















}
