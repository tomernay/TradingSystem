package Domain.Market;

import Domain.Repo.OrderRepository;
import Domain.Repo.StoreRepository;
import Domain.Repo.UserRepository;
import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.util.ArrayList;
import java.util.List;

public class MarketFacade {
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private OrderRepository orderRepository;

    public MarketFacade() {
        this.userRepository = new UserRepository();
        this.storeRepository = new StoreRepository();
        this.orderRepository = new OrderRepository();
    }


    public boolean isStoreOwner(String storeID, String currentUsername) {
        return storeRepository.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> makeStoreOwner(String storeID, String subscriberUsername) {
        Message message = storeRepository.makeNominateOwnerMessage(storeID, subscriberUsername);
        return userRepository.makeStoreOwner(subscriberUsername, message);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return storeRepository.isStoreManager(storeID, currentUsername);
    }

    public Response<String> makeStoreManager(String storeID, String subscriberUsername, List<String> permissions) {
        Message message = storeRepository.makeNominateManagerMessage(storeID, subscriberUsername, permissions);
        return userRepository.makeStoreManager(subscriberUsername, message);
    }

    public Response<String> addManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return storeRepository.addManagerPermissions(storeID, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return storeRepository.removeManagerPermissions(storeID, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return storeRepository.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return userRepository.messageResponse(subscriberUsername, answer);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }

    public Store getStore(String StoreID){
        return storeRepository.getStore(StoreID);
    }

    public void setInventoryToStore(Inventory inventory, String storeID){
        storeRepository.setInventoryToStore(inventory, storeID);
    }

    public Inventory getInventory(String storeID){
        return storeRepository.getInventory(storeID);
    }

    public Response<Integer> addNewProduct(Product.Builder builder, Integer productID, String storeID){
        return storeRepository.addNewProduct(builder, productID, storeID);
    }

    public Response<Integer> removeProduct(int productID, String storeID) {
        return storeRepository.removeProduct(productID, storeID);
    }

    public void setProductCategory(int productID, ArrayList<String> categories, String storeID) {
        storeRepository.setProductCategory(productID, categories, storeID);
    }

    public void removeProductFromAllCategories(int productID, String storeID) {
        storeRepository.removeProductFromAllCategories(productID, storeID);
    }

    public ProductDTO getProductInfo(Integer productID, String storeID) {
        return storeRepository.getProductInfo(productID, storeID);
    }

    public boolean isProductExist(Integer productID, String storeID) {
        return storeRepository.isProductExist(productID, storeID);
    }

    public String getProductsByCategory(int productId, String storeID) throws JsonProcessingException {
        return storeRepository.getProductsByCategory(productId, storeID);
    }

    public synchronized ProductDTO getProductCategory(int productID, String storeID) {
        return storeRepository.getProductCategory(productID, storeID);
    }

    public void setProductID(Integer oldProductID, Integer newProductID, String storeID) {
        storeRepository.setProductID(oldProductID, newProductID, storeID);
    }

    public Product getProduct(Integer productID, String storeID) {
        return storeRepository.getProduct(productID, storeID);
    }

    public void setProductName(Integer productID, String newName, String storeID) {
        storeRepository.setProductName(productID, newName, storeID);
    }

    public void setProductDesc(Integer productID, String newDesc, String storeID) {
        storeRepository.setProductDesc(productID, newDesc, storeID);
    }

    public void setPrice(Integer productID, int newPrice, String storeID) {
        storeRepository.setPrice(productID, newPrice, storeID);
    }

    public void setQuantity(Integer productID, int newQuantity, String storeID) {
        storeRepository.setQuantity(productID, newQuantity, storeID);
    }

    public void addQuantity(Integer productID, int valueToAdd, String storeID) {
        storeRepository.addQuantity(productID, valueToAdd, storeID);
    }

    public int getQuantity(int productID, String storeID) throws Exception {
        return storeRepository.getQuantity(productID, storeID);
    }

    public String getProductDescription(int productID, String storeID) throws Exception {
        return storeRepository.getProductDescription(productID, storeID);
    }

    public int getProductPrice(int productID, String storeID) throws Exception {
        return storeRepository.getProductPrice(productID, storeID);
    }

    public void setStoreIDToProduct(int productID, String storeID){
        storeRepository.setStoreIDToProduct(productID ,storeID);
    }

    public void getProductName(int productID, String storeID) {
        storeRepository.getProductName(productID, storeID);
    }

    public void setProductName(String storeID ,int productID ,String storeName) {
        storeRepository.setProductName(storeID ,productID ,storeName);
    }

    public Response<String> removeCategoryFromProduct(int productID ,String category, String storeID) {
        return storeRepository.removeCategoryFromProduct(productID, category, storeID);
    }

    public void setStoreNameToProduct(int productID ,String storeName, String storeID) {
        storeRepository.setStoreNameToProduct(productID,storeName, storeID);
    }



}
