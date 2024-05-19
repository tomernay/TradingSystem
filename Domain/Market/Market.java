package Domain.Market;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Utilities.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;

public class Market {
    private  MarketFacade marketFacade = new MarketFacade();

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return marketFacade.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> makeStoreOwner(String storeID, String subscriberUsername) {
        return marketFacade.makeStoreOwner(storeID, subscriberUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return marketFacade.isStoreManager(storeID, currentUsername);
    }

    public Response<String> makeStoreManager(String storeID, String subscriberUsername, List<String> permissions) {
        return marketFacade.makeStoreManager(storeID, subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return marketFacade.addManagerPermissions(storeID, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return marketFacade.removeManagerPermissions(storeID, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return marketFacade.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return marketFacade.messageResponse(subscriberUsername, answer);
    }

    public MarketFacade getMarketFacade() {
        return marketFacade;
    }

    public Store getStore(String StoreID){
        return marketFacade.getStore(StoreID);
    }

    public void setInventoryToStore(Inventory inventory, String storeID){
        marketFacade.setInventoryToStore(inventory, storeID);
    }

    public Inventory getInventory(String storeID){
        return marketFacade.getInventory(storeID);
    }

    public Response<Integer> addNewProduct(Product.Builder builder, Integer productID, String storeID){
        return marketFacade.addNewProduct(builder, productID, storeID);
    }

    public Response<Integer> removeProduct(int productID, String storeID) {
        return marketFacade.removeProduct(productID, storeID);
    }

    public void setProductCategory(int productID, ArrayList<String> categories, String storeID) {
        marketFacade.setProductCategory(productID, categories, storeID);
    }

    public void removeProductFromAllCategories(int productID, String storeID) {
        marketFacade.removeProductFromAllCategories(productID, storeID);
    }

    public ProductDTO getProductInfo(Integer productID, String storeID) {
        return marketFacade.getProductInfo(productID, storeID);
    }

    public boolean isProductExist(Integer productID, String storeID) {
        return marketFacade.isProductExist(productID, storeID);
    }

    public String getProductsByCategory(int productId, String storeID) throws JsonProcessingException {
        return marketFacade.getProductsByCategory(productId, storeID);
    }

    public synchronized ProductDTO getProductCategory(int productID, String storeID) {
        return marketFacade.getProductCategory(productID, storeID);
    }

    public void setProductID(Integer oldProductID, Integer newProductID, String storeID) {
        marketFacade.setProductID(oldProductID, newProductID, storeID);
    }

    public Product getProduct(Integer productID, String storeID) {
        return marketFacade.getProduct(productID, storeID);
    }

    public void setProductName(Integer productID, String newName, String storeID) {
        marketFacade.setProductName(productID, newName, storeID);
    }

    public void setProductDesc(Integer productID, String newDesc, String storeID) {
        marketFacade.setProductDesc(productID, newDesc, storeID);
    }

    public void setPrice(Integer productID, int newPrice, String storeID) {
        marketFacade.setPrice(productID, newPrice, storeID);
    }

    public void setQuantity(Integer productID, int newQuantity, String storeID) {
        marketFacade.setQuantity(productID, newQuantity, storeID);
    }

    public void addQuantity(Integer productID, int valueToAdd, String storeID) {
        marketFacade.addQuantity(productID, valueToAdd, storeID);
    }

    public int getQuantity(int productID, String storeID) throws Exception {
        return marketFacade.getQuantity(productID, storeID);
    }

    public String getProductDescription(int productID, String storeID) throws Exception {
        return marketFacade.getProductDescription(productID, storeID);
    }

    public int getProductPrice(int productID, String storeID) throws Exception {
        return marketFacade.getProductPrice(productID, storeID);
    }

    public void setStoreIDToProduct(int productID, String storeID){
        marketFacade.setStoreIDToProduct(productID ,storeID);
    }

    public void getProductName(int productID, String storeID) {
        marketFacade.getProductName(productID, storeID);
    }

    public void setProductName(String storeID ,int productID ,String storeName) {
        marketFacade.setProductName(storeID ,productID ,storeName);
    }

    public Response<String> removeCategoryFromProduct(int productID ,String category, String storeID) {
        return marketFacade.removeCategoryFromProduct(productID, category, storeID);
    }

    public void setStoreNameToProduct(int productID ,String storeName, String storeID) {
        marketFacade.setStoreNameToProduct(productID,storeName, storeID);
    }
}
