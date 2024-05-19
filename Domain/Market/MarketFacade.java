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

    public Response<String> setInventoryToStore(Inventory inventory, String storeID){
        return storeRepository.setInventoryToStore(inventory, storeID);
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

    public Response<Integer> setProductCategory(int productID, ArrayList<String> categories, String storeID) {
        return storeRepository.setProductCategory(productID, categories, storeID);
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

    public Response<String> getProductsByCategory(int productId, String storeID) throws JsonProcessingException {
        return storeRepository.getProductsByCategory(productId, storeID);
    }

    public synchronized ProductDTO getProductCategory(int productID, String storeID) {
        return storeRepository.getProductCategory(productID, storeID);
    }

    public Response<Integer> setProductID(Integer oldProductID, Integer newProductID, String storeID) {
        return storeRepository.setProductID(oldProductID, newProductID, storeID);
    }

    public Response<Product> getProduct(Integer productID, String storeID) {
        return storeRepository.getProduct(productID, storeID);
    }

    public Response<Integer> setProductName(Integer productID, String newName, String storeID) {
        return storeRepository.setProductName(productID, newName, storeID);
    }

    public Response<Integer> setProductDesc(Integer productID, String newDesc, String storeID) {
        return storeRepository.setProductDesc(productID, newDesc, storeID);
    }

    public Response<Integer> setPrice(Integer productID, int newPrice, String storeID) {
        return storeRepository.setPrice(productID, newPrice, storeID);
    }

    public Response<Integer> setQuantity(Integer productID, int newQuantity, String storeID) {
        return storeRepository.setQuantity(productID, newQuantity, storeID);
    }

    public Response<Integer> addQuantity(Integer productID, int valueToAdd, String storeID) {
        return storeRepository.addQuantity(productID, valueToAdd, storeID);
    }

    public Response<Integer> getQuantity(int productID, String storeID) throws Exception {
        return storeRepository.getQuantity(productID, storeID);
    }

    public Response<String> getProductDescription(int productID, String storeID) throws Exception {
        return storeRepository.getProductDescription(productID, storeID);
    }

    public Response<Integer> getProductPrice(int productID, String storeID) throws Exception {
        return storeRepository.getProductPrice(productID, storeID);
    }

    public Response<Integer> setStoreIDToProduct(int productID, String storeID){
        return storeRepository.setStoreIDToProduct(productID ,storeID);
    }

    public Response<String> getProductName(int productID, String storeID) {
        return storeRepository.getProductName(productID, storeID);
    }

    public Response<Integer> setProductName(String storeID ,int productID ,String storeName) {
        return storeRepository.setProductName(storeID ,productID ,storeName);
    }

    public Response<String> removeCategoryFromProduct(int productID ,String category, String storeID) {
        return storeRepository.removeCategoryFromProduct(productID, category, storeID);
    }

    public Response<Integer> setStoreNameToProduct(int productID ,String storeName, String storeID) {
        return storeRepository.setStoreNameToProduct(productID,storeName, storeID);
    }



}
