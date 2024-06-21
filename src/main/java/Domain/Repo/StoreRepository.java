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
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreRepository {
    private final Map<Integer, Store> stores;
    private final Map<Integer, Store> deactivatedStores; // <StoreID, Store>
    private final AtomicInteger storeID = new AtomicInteger(0);


    public StoreRepository() {
        this.stores = new HashMap<>();
        this.deactivatedStores = new HashMap<>();
    }

    public boolean isStoreOwner(Integer storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreOwner(currentUsername);
        }
        return stores.get(storeID).isStoreOwner(currentUsername);
    }

    public boolean isStoreManager(Integer storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreManager(currentUsername);
        }
        return stores.get(storeID).isStoreManager(currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(Integer storeID, String currentUsername, String subscriberUsername) {
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

    public Response<Message> makeNominateManagerMessage(Integer storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
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

    public Response<String> addManagerPermissions(Integer storeID, String currentUsername, String subscriberUsername, String permission) {
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

    public Response<String> removeManagerPermissions(Integer storeID, String currentUsername, String subscriberUsername, String permission) {
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

    public Response<Map<String, String>> requestEmployeesStatus(Integer storeID){
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] Store: " + storeID + " doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            return deactivatedStores.get(storeID).getSubscribersResponse();
        }
        return stores.get(storeID).getSubscribersResponse();
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(Integer storeID){
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] Store: " + storeID + " doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            return deactivatedStores.get(storeID).getManagersPermissionsResponse();
        }
        return stores.get(storeID).getManagersPermissionsResponse();
    }

    public boolean isStoreCreator(Integer storeID, String currentUsername) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreCreator(currentUsername);
        }
        return stores.get(storeID).isStoreCreator(currentUsername);
    }

    public Response<Integer> addStore(String storeName,String creator) {

        try {
            Store store = new Store(storeID.get() ,storeName ,creator);
            Inventory inventory = new Inventory(storeID.get());
            store.setInventory(inventory);
            stores.put(this.storeID.get(), store);
            this.storeID.getAndIncrement();
            SystemLogger.info("[SUCCESS] successfully opened the store "+ storeName);
            return Response.success("successfully opened the store "+ storeName, this.storeID.get()-1);
        }
        catch (Exception e) {
            SystemLogger.error("[ERROR] couldn't open store "+ storeName);
            return Response.error("couldn't open store "+ storeName, null);
        }
    }

    public Store getStore(Integer storeID) {
        return stores.get(storeID);
    }




    public Response<List<String>> closeStore(Integer storeID, String currentUsername) {
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

    public boolean isClosedStore(Integer storeID){
        return deactivatedStores.containsKey(storeID);
    }

    public boolean isOpenedStore(Integer storeID){
        return stores.containsKey(storeID);
    }

    public Response<Set<String>> waiveOwnership(Integer storeID, String currentUsername) {
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

    public Response<String> nominateOwner(Integer storeID, String currentUsername, String nominatorUsername) {
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

    public Response<String> removeStoreSubscription(Integer storeID, String currentUsername) {
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

    public Response<String> nominateManager(Integer storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
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


    public boolean storeExists(Integer storeID) {
        return isStoreExist(storeID).isSuccess();
    }

    private synchronized Response<String> isStoreExist(Integer storeID) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                return Response.success("Store with ID: " + storeID + " is deactivated", null);
            }
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return Response.success("Store with ID: " + storeID + " is active", null);
    }


    public Response<String> setProductQuantity(Integer productID, Integer newQuantity, Integer storeID, String userName) {
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

    public Response<String> addProductQuantity(Integer productID, Integer amountToAdd, Integer storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product quantity in store: " + storeID + " but the store doesn't exist");
            return response;
        }
        return stores.get(storeID).addProductQuantity(productID, amountToAdd, userName);
    }

    public Response<String> getProductName(Integer productID, Integer storeID, String userName) {
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

    public Response<String> setProductName(Integer productID, String newName, Integer storeID, String userName) {
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

    public Response<Double> getProductPrice(Integer productID, Integer storeID, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product price in store: " + storeID + " but the store doesn't exist");
            return Response.error(response.getMessage(), null);
        }
        if (!stores.containsKey(storeID)) {
            return deactivatedStores.get(storeID).getProductPrice(productID);
        }
        return stores.get(storeID).getProductPrice(productID);
    }

    public Response<String> setProductPrice(Integer productID, Double newPrice, Integer storeID, String userName) {
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

    public Response<String> getProductDescription(Integer productID, Integer storeID, String userName) {
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

    public Response<String> setProductDescription(Integer productID, String newDescription, Integer storeID, String userName) {
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

    public Response<String> getProductQuantity(Integer productID, Integer storeID, String userName) {
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

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(Integer storeID, String category, String userName) {
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

    public Response<String> retrieveProductCategories(Integer productID, Integer storeID, String userName) {
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

    public Response<String> assignProductToCategory(Integer productID, String category, Integer storeID, String userName) {
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

    public Response<String> removeCategoryFromStore(Integer storeID, String category, String userName) {
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


    public Response<ProductDTO> getProductFromStore(Integer productID, Integer storeID, String userName) {
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

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(Integer storeID, String userName) {
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

    public Response<Integer> getStoreIDbyName(String storeName, String userName) {
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

    public Response<String> addProductToStore(Integer storeID, String name, String desc, Double price, Integer quantity, String userName) {
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

    public Response<String> addProductToStore(Integer storeID, String name, String desc, Double price, Integer quantity, ArrayList<String> categories, String userName) {
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

    public Response<String> removeProductFromStore(Integer productID, Integer storeID, String userName) {
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

    public Response<ProductDTO> viewProductFromStoreByName(Integer storeID, String productName, String userName) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            SystemLogger.error("[ERROR] " + userName + " tried to get product by name from store: " + storeID + " but the store doesn't exist / is inactive");
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).viewProductFromStoreByName(productName);
    }

    public Response<StoreDTO> getStoreByID(Integer storeID, String userName) {
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

    public Response<String> getStoreNameByID(Integer storeID, String userName) {
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

    public boolean isStoreSubscriber(Integer storeID, String userName) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                return false;
            }
            return deactivatedStores.get(storeID).isStoreSubscriber(userName);
        }
        return stores.get(storeID).isStoreSubscriber(userName);
    }

    public boolean hasPermission(Integer storeID, String username, String permission) {
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

    public Response<String> isProductExist(Integer storeID, Integer productID) {
        if (!stores.containsKey(storeID)) {
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return stores.get(storeID).isProductExist(productID);
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

    public Response<String> isCategoryExist(Integer storeID, String category) {
        Response<String> response = isStoreExist(storeID);
        if (!response.isSuccess()) {
            return Response.error(response.getMessage(), null);
        }
        return stores.get(storeID).isCategoryExist(category);
    }
    public Response<String> unlockProductsBackToStore(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        if(shoppingCart.isEmpty()){
            return Response.error("Shopping cart is empty", null);
        }
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            if (stores.containsKey(storeID)) { //The store exist
                Response<String> resProductDTO = stores.get(storeID).unlockProductsBackToStore(productsInStore);
                if (!resProductDTO.isSuccess()) {
                    return Response.error(resProductDTO.getMessage(), null);
                }
            }
        }
        return Response.success("[SUCCESS] Successfully released the shopping cart and calculated the price.", null);
    }


    public Response<List<ProductDTO>> LockProducts(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        ArrayList<Integer> storeLocked = new ArrayList<>();
        if(shoppingCart.isEmpty()){
            return Response.error("Shopping cart is empty", null);
        }
        List<ProductDTO> output = new ArrayList<>();
        Response<List<ProductDTO>> resProductDTO = Response.error("Couldn't lock the shopping cart", null);
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            if (!stores.containsKey(storeID)) { //The store doesn't exist
                for (Integer store : storeLocked) {
                    stores.get(store).unlockShoppingCart(shoppingCart.get(store)); //Unlock all the stores that were locked
                }
                if (!deactivatedStores.containsKey(storeID)) {
                    SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist");
                    return Response.error("One of the stores in your shopping cart doesn't exist", null);
                }
                SystemLogger.error("[ERROR] Store with ID: " + storeID + " is deactivated");
                return Response.error("Store: " + deactivatedStores.get(storeID).getName() + " is deactivated and can't be purchased from.", null);
            }
            resProductDTO = stores.get(storeID).LockProducts(productsInStore); //Lock the shopping cart
            if (resProductDTO.isSuccess()) {
                storeLocked.add(storeID);
                output.addAll(resProductDTO.getData());
            }
            else {
                for (Integer store : storeLocked) {
                    stores.get(store).unlockShoppingCart(shoppingCart.get(store));
                }
                return Response.error(resProductDTO.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully locked the shopping cart.", output);
    }

    public Response<String> CreateDiscount(Integer productID, Integer storeID, String category, Double percent, String username) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to create discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to create discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to edit discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit discounts", null);
        }
        return stores.get(storeID).CreateDiscount(productID, category, percent,"simple", username);
    }

    public Response<String> CalculateDiscounts(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        double discount = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
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


    public Response<List<DiscountDTO>> getDiscountsFromStore(Integer storeID, String username) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                return deactivatedStores.get(storeID).getDiscounts();
            }
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " doesn't exist", null);
        }
        return stores.get(storeID).getDiscounts();
    }

    public Response<String> removeDiscount(Integer storeID, String username, Integer discountID) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to remove discount", null);
        }
        return stores.get(storeID).removeDiscount(discountID);
    }

    public Response<String> RemoveOrderFromStoreAfterSuccessfulPurchase(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        if(shoppingCart.isEmpty()){
            return Response.error("Shopping cart is empty", null);
        }
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            Response<String> res = stores.get(storeID).RemoveOrderFromStoreAfterSuccessfulPurchase(productsInStore);
            if (!res.isSuccess()) {
                return Response.error(res.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully released the shopping cart.", null);
    }

    public Map<Integer, Store> getStores() {
        return stores;
    }

    public Response<Double> calculateShoppingCartPrice(Map<Integer, Map<Integer, Integer>> shoppingCartContents) {
        double price = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCartContents.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
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

    public Response<String> makeComplexDiscount(String username, Integer storeID, Integer discountId1, Integer discountId2, String discountType) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make complex discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make complex discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to edit discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit discounts", null);
        }
        return stores.get(storeID).makeComplexDiscount(username, discountId1, discountId2, discountType);
    }



    public Response<String> makeConditionDiscount(String username, Integer storeID, Integer discountId, Integer conditionId) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make condition discount in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make condition discount in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to edit discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit discounts", null);
        }
        return stores.get(storeID).makeConditionDiscount(username, discountId, conditionId);
    }

    public Response<String> addSimplePolicyToStore(String username, Integer storeID, String category, Integer productID, Double amount, Double minAmount, Double maxAmount, Boolean price) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to add simple policy to store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to add simple policy to store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to edit policy in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit policies", null);
        }
        return stores.get(storeID).addSimplePolicyToStore(username,category,productID, amount, minAmount, maxAmount, price);
    }

    public Response<String> removeProductFromCategory(Integer productId, String category, Integer storeId, String username) {
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

    public Response<String> makeComplexPolicy(String username, Integer storeID, Integer policyId1, Integer policyId2, String conditionType) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make complex policy in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make complex policy in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to edit policy in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit policies", null);
        }
        return stores.get(storeID).makeComplexPolicy(username, policyId1, policyId2, conditionType);
    }

    public Response<List<ConditionDTO>> getPoliciesFromStore(Integer storeID) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                return deactivatedStores.get(storeID).getPolicies();
            }
            return Response.error("Store doesn't exist", null);
        }
        return stores.get(storeID).getPolicies();
    }

    public Response<String> makeConditionPolicy(String username, Integer storeID, Integer policyId, Integer conditionId) {
        if (!stores.containsKey(storeID)) {
            if (!deactivatedStores.containsKey(storeID)) {
                SystemLogger.error("[ERROR] " + username + " tried to make condition policy in store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to make condition policy in store: " + storeID + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " is deactivated", null);
        }
        if(!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to edit policy in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit policies", null);
        }
        return stores.get(storeID).makeConditionPolicy(username, policyId, conditionId);
    }

    public Response<String> removePolicy(Integer storeId, String username, Integer policyId) {
        if (!stores.containsKey(storeId)) {
            if (!deactivatedStores.containsKey(storeId)) {
                SystemLogger.error("[ERROR] " + username + " tried to remove policy in store: " + storeId + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            SystemLogger.error("[ERROR] " + username + " tried to remove policy in store: " + storeId + " but the store is deactivated");
            return Response.error("Store: " + getStoreNameByID(storeId, username) + " is deactivated", null);
        }
        if(!hasPermission(storeId, username, "MANAGE_DISCOUNTS_POLICIES")){
            SystemLogger.error("[ERROR] " + username + " tried to remove policy in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to remove policies", null);
        }
        return stores.get(storeId).removePolicy(policyId);
    }

    public boolean isNominatorOf(Integer storeId, String username, String manager) {
        if (!stores.containsKey(storeId)) {
            return false;
        }
        return stores.get(storeId).isNominatorOf(username, manager);
    }

    public Response<List<String>> reopenStore(Integer storeID, String currentUsername) {
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

    public boolean isStoreActive(Integer storeID) {
        return stores.containsKey(storeID);
    }

    public Response<String> retrieveAllCategoriesFromAllStore(String username) {
        ArrayList<String> categories = new ArrayList<>();
        for (Store store : stores.values()) {
            Response<ArrayList<String>> response = store.retrieveAllCategoriesFromAllStore();
            if (response.isSuccess()) {
                for(String category : response.getData()){
                    if(!categories.contains(category)){
                        categories.add(category);
                    }
                }
            }
            return Response.error("Couldn't retrieve all categories from all stores", null);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Response.success("All categories were retrieved successfully", objectMapper.convertValue(categories, String.class));
        } catch (Exception e) {
            SystemLogger.error("[ERROR] Couldn't retrieve all categories from all stores");
        }
        return Response.error("Couldn't retrieve all categories from all stores", null);
    }
}
