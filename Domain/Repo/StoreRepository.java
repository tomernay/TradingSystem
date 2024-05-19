package Domain.Repo;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.Store;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRepository {
    private Map<String, Store> stores; // <storeID, Store>

    public StoreRepository() {
        this.stores = new HashMap<>();
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return stores.get(storeID).isStoreOwner(currentUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return stores.get(storeID).isStoreManager(currentUsername);
    }

    public Message makeNominateOwnerMessage(String storeID, String subscriberID) {
        return stores.get(storeID).makeNominateOwnerMessage(subscriberID);
    }

    public Message makeNominateManagerMessage(String storeID, String subscriberID, List<String> permissions) {
        return stores.get(storeID).makeNominateManagerMessage(subscriberID, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String storeManagerID, String permission) {
        return stores.get(storeID).addManagerPermissions(storeManagerID, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String storeManagerID, String permission) {
        return stores.get(storeID).removeManagerPermissions(storeManagerID, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return stores.get(storeID).isStoreCreator(currentUsername);
    }

    //yair added
    public void addPayByBid(HashMap<Integer,Integer> products, double fee, String store,String user){
        Store s=stores.get(store);
        PayByBid pay=new PayByBid(products,fee,s);
        s.addPayByBid(pay,user);
    }

    public void addStore(String storeID,String creator) {
        Store store=new Store("0",storeID,new Inventory(storeID),creator);
        stores.put(storeID, store);
    }

//    public Store getStore(String name) {
//        return stores.get(name);
//    }

    public Store getStore(String StoreID){
        return stores.get(StoreID);
    }

    public Response<String> setInventoryToStore(Inventory inventory, String storeID){
        if(getStore(storeID) != null){
            return getStore(storeID).setInventoryToStore(inventory);
        }
        else {
            return new Response<>(false, "The store doesn't exist", "");
        }
    }

    public Inventory getInventory(String storeID){
        return getStore(storeID).getInventory();
    }


    public Response<Integer> addNewProduct(Product.Builder builder, Integer productID, String storeID){
        return getStore(storeID).addNewProduct(builder, productID);
    }

    public Response<Integer> removeProduct(int productID, String storeID) {
        return getStore(storeID).removeProduct(productID);
    }

    public Response<Integer> setProductCategory(int productID, ArrayList<String> categories, String storeID) {
        return getStore(storeID).setProductCategory(productID, categories);
    }

    public void removeProductFromAllCategories(int productID, String storeID) {
        getStore(storeID).removeProductFromAllCategories(productID);
    }

    public ProductDTO getProductInfo(Integer productID, String storeID) {
        return getStore(storeID).getProductInfo(productID);
    }

    public boolean isProductExist(Integer productID, String storeID) {
        return getStore(storeID).isProductExist(productID);
    }

    public Response<String> getProductsByCategory(int productId, String storeID) throws JsonProcessingException {
        return getStore(storeID).getProductsByCategory(productId);
    }

    public synchronized ProductDTO getProductCategory(int productID, String storeID) {
        return getStore(storeID).getProductCategory(productID);
    }

    public Response<Integer> setProductID(Integer oldProductID, Integer newProductID, String storeID) {
        return getStore(storeID).setProductID(oldProductID, newProductID);
    }

    public Response<Product> getProduct(Integer productID, String storeID) {
        return getStore(storeID).getProduct(productID);
    }

    public Response<Integer> setProductName(Integer productID, String newName, String storeID) {
        return getStore(storeID).setProductName(productID, newName);
    }

    public Response<Integer> setProductDesc(Integer productID, String newDesc, String storeID) {
        return getStore(storeID).setProductDesc(productID, newDesc);
    }

    public Response<Integer> setPrice(Integer productID, int newPrice, String storeID) {
        return getStore(storeID).setPrice(productID, newPrice);
    }

    public Response<Integer> setQuantity(Integer productID, int newQuantity, String storeID) {
        return getStore(storeID).setQuantity(productID, newQuantity);
    }

    public Response<Integer> addQuantity(Integer productID, int valueToAdd, String storeID) {
        return getStore(storeID).addQuantity(productID, valueToAdd);
    }

    public Response<Integer> getQuantity(int productID, String storeID) throws Exception {
        return getStore(storeID).getQuantity(productID);
    }

    public Response<String> getProductDescription(int productID, String storeID) throws Exception {
        return getStore(storeID).getProductDescription(productID);
    }

    public Response<Integer> getProductPrice(int productID, String storeID) throws Exception {
        return getStore(storeID).getProductPrice(productID);
    }

    public Response<Integer> setStoreIDToProduct(int productID, String storeID){
        return getStore(storeID).setStoreIDToProduct(productID ,storeID);
    }

    public Response<String> getProductName(int productID, String storeID) {
        return getStore(storeID).getProductName(productID);
    }

    public Response<Integer> setProductName(String storeID ,int productID ,String storeName) {
        return getStore(storeID).setProductName(productID ,storeName);
    }

    public Response<String> removeCategoryFromProduct(int productID ,String category, String storeID) {
        return getStore(storeID).removeCategoryFromProduct(productID, category);
    }

    public Response<Integer> setStoreNameToProduct(int productID ,String storeName, String storeID) {
        return getStore(storeID).setStoreNameToProduct(productID,storeName);
    }








}
