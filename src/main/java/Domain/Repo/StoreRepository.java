package Domain.Repo;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Store.StoreDTO;
import Domain.Store.StoreData.Permissions;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;


import java.util.*;

public class StoreRepository {
    private Map<String, Store> stores;
    private Map<String, Store> deactivatedStores; // <StoreID, Store>
    private Integer storeID = 0;

    public StoreRepository() {
        this.stores = new HashMap<>();
        this.deactivatedStores = new HashMap<>();
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            return false;
        }
        return stores.get(storeID).isStoreOwner(currentUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            return false;
        }
        return stores.get(storeID).isStoreManager(currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(String storeID, String currentUsername, String subscriberUsername) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as owner in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return stores.get(storeID).makeNominateOwnerMessage(subscriberUsername, currentUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as manager in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return stores.get(storeID).makeNominateManagerMessage(subscriberUsername, permissions, currentUsername);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but " + currentUsername + " is not the store owner / creator");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but " + subscriberUsername + " is not the store manager");
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return stores.get(storeID).addManagerPermissions(subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but " + currentUsername + " is not the store owner / creator");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but " + subscriberUsername + " is not the store manager");
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return stores.get(storeID).removeManagerPermissions(subscriberUsername, permission);
    }

    public Response<Map<String, String>> requestEmployeesStatus(String storeID){
        try{
            return stores.get(storeID).getSubscribersResponse();
        }
        catch (Exception e){
            return Response.error("Invalid storeID.", null);
        }
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID){
        try{
            return stores.get(storeID).getManagersPermissionsResponse();
        }
        catch (Exception e){
            return Response.error("Invalid storeID.", null);
        }
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            return false;
        }
        return stores.get(storeID).isStoreCreator(currentUsername);
    }

    public Response<String> addStore(String storeName,String creator) {

        try {
            Store store = new Store(storeID.toString() ,storeName ,creator);
            Inventory inventory = new Inventory(storeID.toString());
            store.setInventory(inventory);
            stores.put(this.storeID.toString(), store);
            this.storeID++;
            return Response.success("successfully opened the store "+ storeName, Integer.toString(this.storeID-1));
        }
        catch (Exception e) {
            return Response.error("couldn't open store "+ storeName, null);
        }
    }

    public Store getStore(String storeID) {
        return stores.get(storeID);
    }




    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return Response.error("Store with ID: " + storeID + " doesn't exist", null);
            }
            else{
                return Response.error("Store with ID: " + storeID + " is already closed", null);
            }
        }
        if (!isStoreCreator(storeID, currentUsername)) { //The subscriber is not the store manager
            return Response.error("The user trying to do this action is not the store creator.",null);
        }
        Store store = stores.get(storeID);
        if (store == null) {
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        deactivatedStores.put(storeID, store);
        stores.remove(storeID);
        return Response.success("Store with ID: " + storeID + " was closed successfully", new ArrayList<>(deactivatedStores.get(storeID).getSubscribers().keySet()));
    }

    public boolean isClosedStore(String storeID){
        return deactivatedStores.containsKey(storeID);
    }

    public boolean isOpenedStore(String storeID){
        return stores.containsKey(storeID);
    }

    public Response<Set<String>> waiveOwnership(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership in store: " + storeID + " but the user is not the store owner");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        return stores.get(storeID).waiveOwnership(currentUsername);
    }

    public Response<String> nominateOwner(String storeID, String currentUsername, String nominatorUsername) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as owner in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return stores.get(storeID).nominateOwner(currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove store subscription in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return stores.get(storeID).removeStoreSubscription(currentUsername);
    }

    public Response<String> nominateManager(String storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        if (!stores.containsKey(storeID)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as manager in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return stores.get(storeID).nominateManager(currentUsername, permissions, nominatorUsername);
    }


    public boolean storeExists(String storeID) {
        return stores.containsKey(storeID);
    }

    private Response<String> isStoreExist(String storeID) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return Response.error("Store with ID: " + storeID + " doesn't exist", null);
            } else {
                return Response.error("Store with ID: " + storeID + " is already closed", null);
            }
        }
        return Response.success("Store with ID: " + storeID + " is active", storeID);
    }


    public Response<String> setProductQuantity(int productID, int newQuantity, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product quantity in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).setProductQuantity(productID, newQuantity, userName);
    }

    public Response<String> addProductQuantity(int productID, int amountToAdd, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product quantity in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).addProductQuantity(productID, amountToAdd, userName);
    }

    public Response<String> getProductName(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product name in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).getProductName(productID, userName);
    }

    public Response<String> setProductName(int productID, String newName, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product name in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).setProductName(productID, newName, userName);
    }

    public Response<String> getProductPrice(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product price in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).getProductPrice(productID, userName);
    }

    public Response<String> setProductPrice(int productID, int newPrice, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product price in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).setProductPrice(productID, newPrice, userName);

    }

    public Response<String> getProductDescription(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product description in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).getProductDescription(productID, userName);
    }

    public Response<String> setProductDescription(int productID, String newDescription, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product description in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).setProductDescription(productID, newDescription, userName);
    }

    public Response<String> getProductQuantity(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product quantity in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).getProductQuantity(productID, userName);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategory(String storeID, String category, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to retrieve products by category in store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).retrieveProductsByCategory(category, userName);
    }

    public Response<String> retrieveProductCategories(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to retrieve product categories in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).retrieveProductCategories(productID, userName);
    }

    public Response<String> assignProductToCategory(int productID, String category, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to assign product to category in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).assignProductToCategory(productID, category, userName);
    }

    public Response<String> removeCategoryFromStore(String storeID, String category, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove category from store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        return stores.get(storeID).removeCategoryFromStore(category, userName);
    }


    public Response<ProductDTO> getProductFromStore(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).getProductFromStore(productID, userName);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get all products from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).getAllProductsFromStore(userName);
    }

    public Response<String> getStoreIDbyName(String storeName, String userName) {
     Response<String> response = isStoreExist(storeName);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get store ID by name: " + storeName + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeName).getStoreIDbyName(userName);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, int price, int quantity, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).addProductToStore(name, desc, price, quantity, userName);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, int price, int quantity, ArrayList<String> categories, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).addProductToStore(name, desc, price, quantity, categories, userName);
    }

    public Response<String> removeProductFromStore(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove product from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).removeProductFromStore(productID, userName);
    }

    public Response<ProductDTO> getProductByName(String storeID, String productName, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product by name from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).getProductByName(productName, userName);
    }

    public Response<String> getStoreIDByName(String storeName, String userName) {
        Response<String> response = isStoreExist(storeName);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get store ID by name: " + storeName + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeName).getStoreIDByName(userName);
    }

    public Response<StoreDTO> getStoreByID(String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get store by ID: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).getStoreByID(userName);
    }

    public Response<String> getStoreNameByID(String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get store name by ID: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).getStoreNameByID(userName);
    }

    public boolean isStoreSubscriber(String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            return false;
        }
        return stores.get(storeID).isStoreSubscriber(userName);
    }

    public boolean hasPermission(String storeID, String username, String permission) {
        if (!stores.containsKey(storeID)) {
            return false;
        }
        return stores.get(storeID).hasPermission(username, permission);
    }

    public Set<String> getPermissionsList() {
        return Permissions.getPermissions();
    }

    public Response<String> isProductExist(String storeID, String productID) {
        if (!stores.containsKey(storeID)) {
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return stores.get(storeID).isProductExist(productID);
    }
    public Response<Map<String, String>> getStoresRoleWithName(Map<String, String> storesRole) {
        Map<String, String> storesRoleWithName = new HashMap<>();
        for (Map.Entry<String, String> entry : storesRole.entrySet()) {
            String storeID = entry.getKey();
            String storeName = stores.get(storeID).getName();
            storesRoleWithName.put(storeID + " - " + storeName, entry.getValue());
        }
        return Response.success("[SUCCESS] Successfully retrieved the user's stores roles.", storesRoleWithName);
    }
}
