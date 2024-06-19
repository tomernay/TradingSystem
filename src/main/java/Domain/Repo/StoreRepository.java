package Domain.Repo;

import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Store.StoreDTO;
import Domain.Store.StoreData.Permissions;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreRepository {
    private final Map<String, Store> stores;
    private final Map<String, Store> deactivatedStores; // <StoreID, Store>
    private final AtomicInteger storeID = new AtomicInteger(0);


    public StoreRepository() {
        this.stores = new HashMap<>();
        this.deactivatedStores = new HashMap<>();
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreOwner(currentUsername);
        }
        return stores.get(storeID).isStoreOwner(currentUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreManager(currentUsername);
        }
        return stores.get(storeID).isStoreManager(currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(String storeID, String currentUsername, String subscriberUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as owner in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as owner in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);

        }
        return stores.get(storeID).makeNominateOwnerMessage(subscriberUsername, currentUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as manager in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as manager in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);

        }
        return stores.get(storeID).makeNominateManagerMessage(subscriberUsername, permissions, currentUsername);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);

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
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);
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
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] Store: " + storeID + " doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            return deactivatedStores.get(storeID).getSubscribersResponse();
        }
        return stores.get(storeID).getSubscribersResponse();
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID){
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] Store: " + storeID + " doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            return deactivatedStores.get(storeID).getManagersPermissionsResponse();
        }
        return stores.get(storeID).getManagersPermissionsResponse();
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreCreator(currentUsername);
        }
        return stores.get(storeID).isStoreCreator(currentUsername);
    }

    public Response<String> addStore(String storeName,String creator) {

        try {
            Store store = new Store(storeID.toString() ,storeName ,creator);
            Inventory inventory = new Inventory(storeID.toString());
            store.setInventory(inventory);
            stores.put(this.storeID.toString(), store);
            this.storeID.getAndIncrement();
            SystemLogger.info("[SUCCESS] successfully opened the store "+ storeName);
            return Response.success("successfully opened the store "+ storeName, Integer.toString(this.storeID.get()-1));
        }
        catch (Exception e) {
            SystemLogger.error("[ERROR] couldn't open store "+ storeName);
            return Response.error("couldn't open store "+ storeName, null);
        }
    }

    public Store getStore(String storeID) {
        return stores.get(storeID);
    }




    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to close store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            else{
                SystemLogger.error("[ERROR] " + currentUsername + " tried to close store: " + storeID + " but the store is already closed");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is already closed", null);
            }
        }
        if (!isStoreCreator(storeID, currentUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to close store: " + storeID + " but the user is not the store creator");
            return Response.error("The user trying to do this action is not the store creator.",null);
        }
        Store store = stores.get(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to close store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist", null);
        }
        deactivatedStores.put(storeID, store);
        stores.remove(storeID);
        SystemLogger.info("[SUCCESS] " + currentUsername + " successfully closed the store: " + storeID);
        return Response.success("Store: " + getStoreNameByID(storeID, currentUsername) + " was closed successfully", new ArrayList<>(deactivatedStores.get(storeID).getSubscribers().keySet()));
    }

    public boolean isClosedStore(String storeID){
        return deactivatedStores.containsKey(storeID);
    }

    public boolean isOpenedStore(String storeID){
        return stores.containsKey(storeID);
    }

    public Response<Set<String>> waiveOwnership(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership in store: " + storeID + " but the store is deactivated");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership in store: " + storeID + " but the user is not the store owner");
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        return stores.get(storeID).waiveOwnership(currentUsername);
    }

    public Response<String> nominateOwner(String storeID, String currentUsername, String nominatorUsername) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as owner in store: " + storeID + " but the store is deactivated");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as owner in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist", null);
        }
        return stores.get(storeID).nominateOwner(currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(String storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to remove store subscription in store: " + storeID + " but the store is deactivated");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove store subscription in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist", null);
        }
        return stores.get(storeID).removeStoreSubscription(currentUsername);
    }

    public Response<String> nominateManager(String storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as manager in store: " + storeID + " but the store is deactivated");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as manager in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist", null);
        }
        return stores.get(storeID).nominateManager(currentUsername, permissions, nominatorUsername);
    }


    public boolean storeExists(String storeID) {
        return isStoreExist(storeID).isSuccess();
    }

    private synchronized Response<String> isStoreExist(String storeID) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                return Response.success("Store with ID: " + storeID + " is deactivated", storeID);
            }
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return Response.success("Store with ID: " + storeID + " is active", storeID);
    }


    public Response<String> setProductQuantity(int productID, int newQuantity, String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to set product quantity in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to set product quantity in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).setProductQuantity(productID, newQuantity, userName);
    }

    public Response<String> addProductQuantity(int productID, int amountToAdd, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product quantity in store: " + storeID + " but the store doesn't exist");
            return response;
        }
        return stores.get(storeID).addProductQuantity(productID, amountToAdd, userName);
    }

    public Response<String> getProductName(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product name in store: " + storeID + " but the store doesn't exist");
            return response;
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getProductName(productID);
        }
        return stores.get(storeID).getProductName(productID);
    }

    public Response<String> setProductName(int productID, String newName, String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to set product name in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to set product name in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).setProductName(productID, newName, userName);
    }

    public Response<String> getProductPrice(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product price in store: " + storeID + " but the store doesn't exist");
            return response;
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getProductPrice(productID);
        }
        return stores.get(storeID).getProductPrice(productID);
    }

    public Response<String> setProductPrice(int productID, double newPrice, String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to set product price in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to set product price in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).setProductPrice(productID, newPrice, userName);

    }

    public Response<String> getProductDescription(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product description in store: " + storeID + " but the store doesn't exist");
            return response;
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getProductDescription(productID);
        }
        return stores.get(storeID).getProductDescription(productID);
    }

    public Response<String> setProductDescription(int productID, String newDescription, String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to set product description in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to set product description in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).setProductDescription(productID, newDescription, userName);
    }

    public Response<String> getProductQuantity(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product quantity in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getProductQuantity(productID);
        }
        return stores.get(storeID).getProductQuantity(productID);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(String storeID, String category, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to retrieve products by category in store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).retrieveProductsByCategoryFrom_OneStore(category);
        }
        return stores.get(storeID).retrieveProductsByCategoryFrom_OneStore(category);
    }

    public Response<String> retrieveProductCategories(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to retrieve product categories in store: " + storeID + " but the store doesn't exist / is inactive");
            return response;
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).retrieveProductCategories(productID);
        }
        return stores.get(storeID).retrieveProductCategories(productID);
    }

    public Response<String> assignProductToCategory(int productID, String category, String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to assign product to category in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to assign product to category in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).assignProductToCategory(productID, category, userName);
    }

    public Response<String> removeCategoryFromStore(String storeID, String category, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to remove category from store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to remove category from store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).removeCategoryFromStore(category, userName);
    }


    public Response<ProductDTO> getProductFromStore(int productID, String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getProductFromStore(productID);
        }
        return stores.get(storeID).getProductFromStore(productID);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get all products from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getAllProductsFromStore();
        }
        return stores.get(storeID).getAllProductsFromStore();
    }

    public Response<String> getStoreIDbyName(String storeName, String userName) {
        for (Store store : stores.values()) {
            if (store.getName().equals(storeName)) {
                SystemLogger.info("[SUCCESS] " + userName + " successfully retrieved the store ID by name: " + storeName);
                return Response.success("Successfully retrieved the store ID by name.", store.getId());
            }
        }
        for (Store store : deactivatedStores.values()) {
            if (store.getName().equals(storeName)) {
                SystemLogger.info("[SUCCESS] " + userName + " successfully retrieved the store ID by name: " + storeName);
                return Response.success("Successfully retrieved the store ID by name.", store.getId());
            }
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get store ID by name: " + storeName + " but the store doesn't exist");
        return Response.error("Store with name: " + storeName + " doesn't exist", null);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, double price, int quantity, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).addProductToStore(name, desc, price, quantity, userName);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, double price, int quantity, ArrayList<String> categories, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).addProductToStore(name, desc, price, quantity, categories, userName);
    }

    public Response<String> removeProductFromStore(int productID, String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + userName + " tried to remove product from store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + userName + " tried to remove product from store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, userName) + " is deactivated", null);
        }
        return stores.get(storeID).removeProductFromStore(productID, userName);
    }

    public Response<ProductDTO> viewProductFromStoreByName(String storeID, String productName, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product by name from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).viewProductFromStoreByName(productName);
    }

    public Response<StoreDTO> getStoreByID(String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get store by ID: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getStoreByID();
        }
        return stores.get(storeID).getStoreByID();
    }

    public Response<String> getStoreNameByID(String storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get store name by ID: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getStoreNameByID();
        }
        return stores.get(storeID).getStoreNameByID();
    }

    public boolean isStoreSubscriber(String storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreSubscriber(userName);
        }
        return stores.get(storeID).isStoreSubscriber(userName);
    }

    public boolean hasPermission(String storeID, String username, String permission) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).hasPermission(username, permission);
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
        SystemLogger.info("[SUCCESS] Successfully retrieved the user's stores roles.");
        return Response.success("[SUCCESS] Successfully retrieved the user's stores roles.", storesRoleWithName);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByCategory(String category) {
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Store store : stores.values()) {
            Response<ArrayList<ProductDTO>> response = store.viewProductByCategory(category);
            if (response.isSuccess()) {
                products.addAll(response.getData());
            }
        }
        return Response.success("All products with category: " + category + " were retrieved successfully", products);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByName(String productName) {
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Store store : stores.values()) {
            Response<ProductDTO> response = store.viewProductByName(productName);
            if (response.isSuccess()) {
                products.add(response.getData());
            }
        }
        return Response.success("All products with name: " + productName + " were retrieved successfully", products);

    }

    public Response<String> isCategoryExist(String storeID, String category) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).isCategoryExist(category);
    }
    public Response<String> unlockProductsBackToStore(Map<String, Map<String, Integer>> shoppingCart) {
        if(shoppingCart.isEmpty()){
            return Response.error("Shopping cart is empty", null);
        }
        for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCart.entrySet()) {
            String storeID = storeEntry.getKey();
            Map<String, Integer> productsInStore = storeEntry.getValue();
            if (stores.containsKey(storeID)) { //The store exist
                Response<String> resProductDTO = stores.get(storeID).unlockProductsBackToStore(productsInStore);
                if (!resProductDTO.isSuccess()) {
                    return Response.error(resProductDTO.getMessage(), null);
                }
            }
        }
        return Response.success("[SUCCESS] Successfully released the shopping cart and calculated the price.", null);
    }


    public Response<String> LockProducts(Map<String, Map<String, Integer>> shoppingCart) {
        ArrayList<String> storeLocked = new ArrayList<>();
        if(shoppingCart.isEmpty()){
            return Response.error("Shopping cart is empty", null);
        }
        for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCart.entrySet()) {
            String storeID = storeEntry.getKey();
            Map<String, Integer> productsInStore = storeEntry.getValue();
            if (!stores.containsKey(storeID)) { //The store doesn't exist
                for (String store : storeLocked) {
                    stores.get(store).unlockShoppingCart(shoppingCart.get(store)); //Unlock all the stores that were locked
                }
                if (!deactivatedStores.containsKey(storeID)) {
                    SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist");
                    return Response.error("One of the stores in your shopping cart doesn't exist", null);
                }
                SystemLogger.error("[ERROR] Store with ID: " + storeID + " is deactivated");
                return Response.error("Store: " + deactivatedStores.get(storeID).getName() + " is deactivated and can't be purchased from.", null);
            }
            Response<Map<ProductDTO,Integer>> resProductDTO = stores.get(storeID).LockProducts(productsInStore); //Lock the shopping cart
            if (resProductDTO.isSuccess()) {
                storeLocked.add(storeID);
            }
            else {
                for (String store : storeLocked) {
                    stores.get(store).unlockShoppingCart(shoppingCart.get(store));
                }
                return Response.error(resProductDTO.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully locked the shopping cart and calculated the price.", null);
    }

    public Response<String> CreateDiscount(String productID, String storeID, String category, String percent, String username) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to create discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to create discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        return stores.get(storeID).CreateDiscount(productID, category, percent,"simple", username);
    }

    public Response<String> CalculateDiscounts(Map<String, Map<String, Integer>> shoppingCart) {
        double discount = 0;
        for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCart.entrySet()) {
            String storeID = storeEntry.getKey();
            Map<String, Integer> productsInStore = storeEntry.getValue();
            Response<Double> discountShop;
            if (!stores.containsKey(storeID)) {
                discountShop = deactivatedStores.get(storeID).CalculateDiscounts(productsInStore);
            }
            else {
                discountShop = stores.get(storeID).CalculateDiscounts(productsInStore);
            }
            if (discountShop.isSuccess()) {

                discount += discountShop.getData();
            }
            else {
                return Response.error(discountShop.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully calculated the discount.", String.valueOf(discount));
    }


    public Response<List<DiscountDTO>> getDiscountsFromStore(String storeID, String username) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                return deactivatedStores.get(storeID).getDiscounts(username);
            }
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " doesn't exist", null);
        }
        return stores.get(storeID).getDiscounts(username);
    }

    public Response<String> removeDiscount(String storeID, String username, String discountID) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!stores.get(storeID).isStoreOwner(username) && !stores.get(storeID).isStoreManager(username)){
            SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to remove discount", null);
        }
        return stores.get(storeID).removeDiscount(discountID);
    }

    public Response<String> RemoveOrderFromStoreAfterSuccessfulPurchase(Map<String, Map<String, Integer>> shoppingCart) {
        if(shoppingCart.isEmpty()){
            return Response.error("Shopping cart is empty", null);
        }
        for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCart.entrySet()) {
            String storeID = storeEntry.getKey();
            Map<String, Integer> productsInStore = storeEntry.getValue();
            Response<String> res = stores.get(storeID).RemoveOrderFromStoreAfterSuccessfulPurchase(productsInStore);
            if (!res.isSuccess()) {
                return Response.error(res.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully released the shopping cart.", null);
    }

    public Map<String, Store> getStores() {
        return stores;
    }

    public Response<Double> calculateShoppingCartPrice(Map<String, Map<String, Integer>> shoppingCartContents) {
        double price = 0;
        for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCartContents.entrySet()) {
            String storeID = storeEntry.getKey();
            Map<String, Integer> productsInStore = storeEntry.getValue();
            Response<String> res;
            if (!stores.containsKey(storeID)) {
                res = deactivatedStores.get(storeID).calculateShoppingCartPrice(productsInStore);
            }
            else {
                res = stores.get(storeID).calculateShoppingCartPrice(productsInStore);
            }
            if (res.isSuccess()) {
                price += Double.parseDouble(res.getData());
            }
            else {
                return Response.error(res.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully calculated the price.", price);
    }

    public Response<String> makeComplexDiscount(String username, String storeID, int discountId1, int discountId2, String discountType) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make complex discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make complex discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        return stores.get(storeID).makeComplexDiscount(username, discountId1, discountId2, discountType);
    }



    public Response<String> makeConditionDiscount(String username, String storeID, int discountId, int conditionId) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make condition discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make condition discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        return stores.get(storeID).makeConditionDiscount(username, discountId, conditionId);
    }

    public Response<String> addSimplePolicyToStore(String username, String storeID, String category, Integer productID, Double amount, Double minAmount, Double maxAmount, Double price) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to add simple policy to store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to add simple policy to store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        return stores.get(storeID).addSimplePolicyToStore(username,category,productID, amount, minAmount, maxAmount, price);
    }

    public Response<String> removeProductFromCategory(int productId, String category, String storeId, String username) {
        if (!stores.containsKey(storeId)) {
            if (!deactivatedStores.containsKey(storeId)) {
                SystemLogger.error("[ERROR] " + username + " tried to remove product from category in store: " + storeId + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to remove product from category in store: " + storeId + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeId, username) + " is deactivated", null);
        }
        return stores.get(storeId).removeProductFromCategory(productId, category, username);
    }

    public Response<String> makeComplexPolicy(String username, String storeID, int policyId1, int policyId2, String conditionType) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make complex policy in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make complex policy in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        return stores.get(storeID).makeComplexPolicy(username, policyId1, policyId2, conditionType);
    }

    public Response<List<ConditionDTO>> getPoliciesFromStore(String storeID, String username) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                return deactivatedStores.get(storeID).getPolicies(username);
            }
            return Response.error("Store doesn't exist", null);
        }
        return stores.get(storeID).getPolicies(username);
    }

    public Response<String> makeConditionPolicy(String username, String storeID, int policyId, int conditionId) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make condition policy in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make condition policy in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        return stores.get(storeID).makeConditionPolicy(username, policyId, conditionId);
    }

    public Response<String> removePolicy(String storeId, String username, String policyId) {
        if (!stores.containsKey(storeId)) {
            if (!deactivatedStores.containsKey(storeId)) {
                SystemLogger.error("[ERROR] " + username + " tried to remove policy in store: " + storeId + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to remove policy in store: " + storeId + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeId, username) + " is deactivated", null);
        }
        return stores.get(storeId).removePolicy(policyId);
    }

    public boolean isNominatorOf(String storeId, String username, String manager) {
        if (!stores.containsKey(storeId)) {
            return false;
        }
        return stores.get(storeId).isNominatorOf(username, manager);
    }

    public Response<List<String>> reopenStore(String storeID, String currentUsername) {
        if (!deactivatedStores.containsKey(storeID)) {
            if (!stores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to reopen store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            else{
                SystemLogger.error("[ERROR] " + currentUsername + " tried to reopen store: " + storeID + " but the store is already opened");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is already opened", null);
            }
        }
        if (!isStoreCreator(storeID, currentUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to reopen store: " + storeID + " but the user is not the store creator");
            return Response.error("The user trying to do this action is not the store creator.",null);
        }
        Store store = deactivatedStores.get(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to reopen store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist", null);
        }
        deactivatedStores.remove(storeID);
        stores.put(storeID, store);
        SystemLogger.info("[SUCCESS] " + currentUsername + " reopened store: " + storeID + " successfully");
        return Response.success("Store: " + getStoreNameByID(storeID, currentUsername) + " was reopened successfully", new ArrayList<>(stores.get(storeID).getSubscribers().keySet()));
    }

    public boolean isStoreActive(String storeID, String username, String token) {
        return stores.containsKey(storeID);
    }
}
