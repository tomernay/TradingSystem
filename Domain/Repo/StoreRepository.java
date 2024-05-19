package Domain.Repo;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;
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

    public void setInventoryToStore(Inventory inventory, String storeID){
        getStore(storeID).setInventoryToStore(inventory);
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

    public void setProductCategory(int productID, ArrayList<String> categories, String storeID) {
        getStore(storeID).setProductCategory(productID, categories);
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

    public String getProductsByCategory(int productId, String storeID) throws JsonProcessingException {
        return getStore(storeID).getProductsByCategory(productId);
    }

    public synchronized ProductDTO getProductCategory(int productID, String storeID) {
        return getStore(storeID).getProductCategory(productID);
    }

    public void setProductID(Integer oldProductID, Integer newProductID, String storeID) {
        getStore(storeID).setProductID(oldProductID, newProductID);
    }

    public Product getProduct(Integer productID, String storeID) {
        return getStore(storeID).getProduct(productID);
    }

    public void setProductName(Integer productID, String newName, String storeID) {
        getStore(storeID).setProductName(productID, newName);
    }

    public void setProductDesc(Integer productID, String newDesc, String storeID) {
        getStore(storeID).setProductDesc(productID, newDesc);
    }

    public void setPrice(Integer productID, int newPrice, String storeID) {
        getStore(storeID).setPrice(productID, newPrice);
    }

    public void setQuantity(Integer productID, int newQuantity, String storeID) {
        getStore(storeID).setQuantity(productID, newQuantity);
    }

    public void addQuantity(Integer productID, int valueToAdd, String storeID) {
        getStore(storeID).addQuantity(productID, valueToAdd);
    }

    public int getQuantity(int productID, String storeID) throws Exception {
        return getStore(storeID).getQuantity(productID);
    }

    public String getProductDescription(int productID, String storeID) throws Exception {
        return getStore(storeID).getProductDescription(productID);
    }

    public int getProductPrice(int productID, String storeID) throws Exception {
        return getStore(storeID).getProductPrice(productID);
    }








}
