package Facades;

import Domain.Repo.StoreRepository;
import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.TYPE;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Store.StoreDTO;
import Domain.Store.StoreData.Permissions;
import Presentation.application.View.UtilitiesView.Broadcaster;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoreFacade {
    private final StoreRepository storeRepository;

    public StoreFacade() {
        storeRepository = new StoreRepository();
    }

    public boolean isStoreManager(Integer storeID, String currentUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.isStoreManager(currentUsername);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.isStoreManager(currentUsername);
        }
        return false;
    }

    public Response<Message> makeNominateOwnerMessage(Integer storeID, String currentUsername, String subscriberUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.makeNominateOwnerMessage(subscriberUsername, currentUsername);
        }
        SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as owner in store: " + storeID + " but the store doesn't exist / is deactivated");
        return Response.error("Store doesn't exist / is deactivated", null);
    }

    public Response<Message> makeNominateManagerMessage(Integer storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.makeNominateManagerMessage(subscriberUsername, permissions, currentUsername);
        }
        SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + subscriberUsername + " as manager in store: " + storeID + " but the store doesn't exist / is deactivated");
        return Response.error("Store doesn't exist / is deactivated", null);
    }

    public Response<String> addManagerPermissions(Integer storeID, String currentUsername, String subscriberUsername, String permission ) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the store is deactivated / doesn't exist");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated / doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but " + currentUsername + " is not the store owner / creator");
            return Response.error("The user trying to do this action is not the store owner.", null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but " + subscriberUsername + " is not the store manager");
            return Response.error("The user you're trying to change permissions for is not the store manager.", null);
        }
        return store.addManagerPermissions(subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(Integer storeID, String currentUsername, String subscriberUsername, String permission) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the store is deactivated / doesn't exist");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated / doesn't exist", null);
        }
        if (!isStoreOwner(storeID, currentUsername) && !isStoreCreator(storeID, currentUsername)) { //The currentUsername is not the store owner / creator
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but " + currentUsername + " is not the store owner / creator");
            return Response.error("The user trying to do this action is not the store owner.", null);
        }
        if (!isStoreManager(storeID, subscriberUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but " + subscriberUsername + " is not the store manager");
            return Response.error("The user you're trying to change permissions for is not the store manager.", null);
        }
        return store.removeManagerPermissions(subscriberUsername, permission);
    }

    public boolean isStoreCreator(Integer storeID, String currentUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.isStoreCreator(currentUsername);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.isStoreCreator(currentUsername);
        }
        return false;
    }

    public Response<List<String>> closeStore(Integer storeID, String currentUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            store = storeRepository.getDeactivatedStore(storeID);
            if (store != null) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to close store: " + storeID + " but the store is already closed");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is already closed", null);
            }
            SystemLogger.error("[ERROR] " + currentUsername + " tried to close store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist", null);
        }
        if (!isStoreCreator(storeID, currentUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to close store: " + storeID + " but the user is not the store creator");
            return Response.error("The user trying to do this action is not the store creator.", null);
        }
        storeRepository.addDeactivatedStore(store);
        storeRepository.removeActiveStore(storeID);
        SystemLogger.info("[SUCCESS] " + currentUsername + " successfully closed the store: " + storeID);
        return Response.success("Store: " + getStoreNameByID(storeID, currentUsername) + " was closed successfully", new ArrayList<>(store.getSubscribers().keySet()));
    }

    public Response<Map<String, String>> requestEmployeesStatus(Integer storeID){
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getSubscribersResponse();
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getSubscribersResponse();
        }
        SystemLogger.error("[ERROR] Store: " + storeID + " doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(Integer storeID){
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getManagersPermissionsResponse();
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getManagersPermissionsResponse();
        }
        SystemLogger.error("[ERROR] Store: " + storeID + " doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<Integer> openStore(String storeName, String creator) {
        try {
            Integer storeId = storeRepository.getStoreID();
            Store store = new Store(storeId, storeName, creator);
            Inventory inventory = new Inventory(storeId);
            store.setInventory(inventory);
            storeRepository.addActiveStore(store);
            SystemLogger.info("[SUCCESS] successfully opened the store " + storeName);
//            Broadcaster.broadcast("successfully opened the store " + storeName,creator);
            return Response.success("successfully opened the store " + storeName, storeId);
        }
        catch (Exception e) {
            SystemLogger.error("[ERROR] couldn't open store " + storeName);
            return Response.error("couldn't open store " + storeName, null);
        }
    }

    public boolean isStoreOwner(Integer storeID, String currentUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.isStoreOwner(currentUsername);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.isStoreOwner(currentUsername);
        }
        return false;
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }



    public Response<String> nominateOwner(Integer storeID, String currentUsername, String nominatorUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as owner in store: " + storeID + " but the store is deactivated / doesn't exist");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated / doesn't exist", null);
        }
        return store.nominateOwner(currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(Integer storeID, String currentUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to remove store subscription in store: " + storeID + " but the store is deactivated / doesn't exist");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated / doesn't exist", null);
        }
        return store.removeStoreSubscription(currentUsername);
    }

    public Response<String> nominateManager(Integer storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to nominate " + nominatorUsername + " as manager in store: " + storeID + " but the store is deactivated / doesn't exist");
            return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is deactivated / doesn't exist", null);
        }
        return store.nominateManager(currentUsername, permissions, nominatorUsername);
    }

    public Response<Set<String>> waiveOwnership(Integer storeID, String currentUsername) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        if (!isStoreOwner(storeID, currentUsername)) {
            SystemLogger.error("[ERROR] " + currentUsername + " tried to waive ownership in store: " + storeID + " but the user is not the store owner");
            return Response.error("The user trying to do this action is not the store owner.", null);
        }
        return store.waiveOwnership(currentUsername);
    }

    public boolean storeExists(Integer storeID) {
        return storeRepository.isStoreExist(storeID).isSuccess();
    }


    public Response<String> setProductQuantity(Integer productID, Integer quantity, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product quantity in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.setProductQuantity(productID, quantity, userName);
    }

    public Response<String> addProductQuantity(Integer productID, Integer amountToAdd, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product quantity in store: " + storeID + " but the store doesn't exist");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.addProductQuantity(productID, amountToAdd, userName);
    }

    public Response<String> getProductName(Integer productID, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getProductName(productID);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getProductName(productID);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get product name in store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<String> setProductName(Integer productID, String newName, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product name in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.setProductName(productID, newName, userName);
    }

    public Response<Double> getProductPrice(Integer productID, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getProductPrice(productID);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getProductPrice(productID);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get product price in store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<String> setProductPrice(Integer productID, Double newPrice, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product price in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.setProductPrice(productID, newPrice, userName);
    }

    public Response<String> getProductDescription(Integer productID, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getProductDescription(productID);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getProductDescription(productID);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get product description in store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<String> setProductDescription(Integer productID, String newDescription, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to set product description in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.setProductDescription(productID, newDescription, userName);
    }

    public Response<String> getProductQuantity(Integer productID, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getProductQuantity(productID);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getProductQuantity(productID);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get product quantity in store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(Integer storeID, String category, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.retrieveProductsByCategoryFrom_OneStore(category);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.retrieveProductsByCategoryFrom_OneStore(category);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to retrieve products by category in store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<String> retrieveProductCategories(Integer productID, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.retrieveProductCategories(productID);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.retrieveProductCategories(productID);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to retrieve product categories in store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<String> assignProductToCategory(Integer productID, String category, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to assign product to category in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.assignProductToCategory(productID, category, userName);
    }

    public Response<String> removeCategoryFromStore(Integer storeID, String category, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to assign product to category in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.removeCategoryFromStore(category, userName);
    }


    public Response<ProductDTO> getProductFromStore(Integer productID, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getProductFromStore(productID);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getProductFromStore(productID);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get product from store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getAllProductsFromStore();
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getAllProductsFromStore();
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get all products from store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }


    public Response<Integer> getStoreIDbyName(String storeName, String userName) {
        Map<Integer, Store> stores = storeRepository.getActiveStores();
        for (Store store : stores.values()) {
            if (store.getName().equals(storeName)) {
                SystemLogger.info("[SUCCESS] " + userName + " successfully retrieved the store ID by name: " + storeName);
                return Response.success("Successfully retrieved the store ID by name.", store.getId());
            }
        }
        Map<Integer, Store> deactivatedStores = storeRepository.getDeactivatedStores();
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
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.addProductToStore(name, desc, price, quantity, userName);
    }

    public Response<String> addProductToStore(Integer storeID, String name, String desc, Double price, Integer quantity, ArrayList<String> categories, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to add product to store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.addProductToStore(name, desc, price, quantity, categories, userName);
    }

    public Response<String> removeProductFromStore(Integer productID, Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + userName + " tried to remove product from store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.removeProductFromStore(productID, userName);
    }

    public Response<ProductDTO> viewProductFromStoreByName(Integer storeID, String productName, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.viewProductFromStoreByName(productName);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.viewProductFromStoreByName(productName);
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get product by name from store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<StoreDTO> getStoreByID(Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getStoreByID();
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getStoreByID();
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get product by name from store: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public Response<String> getStoreNameByID(Integer storeID, String userName) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.getStoreNameByID();
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.getStoreNameByID();
        }
        SystemLogger.error("[ERROR] " + userName + " tried to get store name by ID: " + storeID + " but the store doesn't exist");
        return Response.error("Store doesn't exist", null);
    }

    public boolean isStoreSubscriber(Integer storeID, String username) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.isStoreSubscriber(username);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.isStoreSubscriber(username);
        }
        return false;
    }

    public boolean hasPermission(Integer storeID, String username, String permission) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.hasPermission(username, permission);
        }
        store = storeRepository.getDeactivatedStore(storeID);
        if (store != null) {
            return store.hasPermission(username, permission);
        }
        return false;
    }

    public Set<String> getPermissionsList() {
        return Permissions.getPermissions();
    }
    public Response<String> isProductExist(Integer storeID, Integer productID) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.isProductExist(productID);
        }
        return Response.error("Store with ID: " + storeID + " doesn't exist", null);
    }

    public Response<String> isCategoryExist(Integer storeID, String category){
        Store store = storeRepository.getActiveStore(storeID);
        if (store != null) {
            return store.isCategoryExist(category);
        }
        return Response.error("Store with ID: " + storeID + " doesn't exist", null);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByName(String productName) {
        Map<Integer, Store> stores = storeRepository.getActiveStores();
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Store store : stores.values()) {
            Response<ProductDTO> response = store.viewProductByName(productName);
            if (response.isSuccess()) {
                products.add(response.getData());
            }
        }
        return Response.success("All products with name: " + productName + " were retrieved successfully", products);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByCategory(String category) {
        Map<Integer, Store> stores = storeRepository.getActiveStores();
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Store store : stores.values()) {
            Response<ArrayList<ProductDTO>> response = store.viewProductByCategory(category);
            if (response.isSuccess()) {
                products.addAll(response.getData());
            }
        }
        return Response.success("All products with category: " + category + " were retrieved successfully", products);
    }

    public Response<List<ProductDTO>> LockProducts(Map<Integer, Map<Integer, Integer>> shoppingCart,Boolean isOverEighteen) {
        ArrayList<Integer> storeLocked = new ArrayList<>();
        if (shoppingCart.isEmpty()) {
            return Response.error("Shopping cart is empty", null);
        }
        List<ProductDTO> output = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            Store store = storeRepository.getActiveStore(storeID);
            if (store == null) { //The store doesn't exist
                for (Integer storeId : storeLocked) {
                    Store store1 = storeRepository.getActiveStore(storeId);
                    store1.unlockShoppingCart(shoppingCart.get(storeId)); //Unlock all the stores that were locked
                }
                SystemLogger.error("[ERROR] Store with ID: " + storeID + " doesn't exist / is deactivated");
                return Response.error("Store: " + getStoreNameByID(storeID, "") + " doesn't exist / is deactivated", null);
            }
            Response<List<ProductDTO>> resProductDTO = store.LockProducts(productsInStore,isOverEighteen); //Lock the shopping cart
            if (resProductDTO.isSuccess()) {
                storeLocked.add(storeID);
                output.addAll(resProductDTO.getData());
            } else {
                for (Integer storeId : storeLocked) {
                    Store store1 = storeRepository.getActiveStore(storeId);
                    store1.unlockShoppingCart(shoppingCart.get(storeId));
                }
                return Response.error(resProductDTO.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully locked the shopping cart.", output);
    }

    public Response<String> CreateDiscount(Integer storeID, String username, Double percent, String type, String value) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to create discount in store: " + storeID + " but the store doesn't exist / is deactivated");
            return Response.error("Store doesn't exist / is deactivated", null);
        }
        if (!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to edit discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit discounts", null);
        }
        return store.CreateDiscount(percent, username, type, value);
    }

    public Response<Double> CalculateDiscounts(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        double discount = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            Store store = storeRepository.getActiveStore(storeID);
            Response<Double> discountShop;
            if (store == null) {
                Store deactivatedStore = storeRepository.getDeactivatedStore(storeID);
                if (deactivatedStore == null) {
                    return Response.error("Store with ID: " + storeID + " doesn't exist", null);
                }
                discountShop = deactivatedStore.CalculateDiscounts(productsInStore);
            }
            else {
                discountShop = store.CalculateDiscounts(productsInStore);
            }
            if (discountShop.isSuccess()) {
                discount += discountShop.getData();
            } else {
                return Response.error(discountShop.getMessage(), null);
            }
        }
        return Response.success("[SUCCESS] Successfully calculated the discount.", discount);
    }

    public Response<String> unlockProductsBackToStore(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        if (shoppingCart.isEmpty()) {
            return Response.error("Shopping cart is empty", null);
        }
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            Store store = storeRepository.getActiveStore(storeID);
            if (store != null) { //The store exist
                Response<String> resProductDTO = store.unlockProductsBackToStore(productsInStore);
                if (!resProductDTO.isSuccess()) {
                    return Response.error(resProductDTO.getMessage(), null);
                }
            }
        }
        return Response.success("[SUCCESS] Successfully released the shopping cart and calculated the price.", null);
    }

    public Response<List<DiscountDTO>> getDiscountsFromStore(Integer storeID, String username) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            Store deactivatedStore = storeRepository.getDeactivatedStore(storeID);
            if (deactivatedStore != null) {
                return deactivatedStore.getDiscounts();
            }
            return Response.error("Store: " + getStoreNameByID(storeID, username) + " doesn't exist", null);
        }
        return store.getDiscounts();
    }

    public Response<String> removeDiscount(Integer storeID, String username, Integer discountID) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the store doesn't exist / is deactivated");
            return Response.error("Store doesn't exist / is deactivated", null);
        }
        if (!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to remove discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to remove discount", null);
        }
        return store.removeDiscount(discountID);
    }

    public Response<String> RemoveOrderFromStoreAfterSuccessfulPurchase(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        if (shoppingCart.isEmpty()) {
            return Response.error("Shopping cart is empty", null);
        }
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            Store store = storeRepository.getActiveStore(storeID);
            if (store == null) {
                Store deactivatedStore = storeRepository.getDeactivatedStore(storeID);
                if (deactivatedStore == null) {
                    return Response.error("Store with ID: " + storeID + " doesn't exist", null);
                }
                Response<String> res = deactivatedStore.RemoveOrderFromStoreAfterSuccessfulPurchase(productsInStore);
                if (!res.isSuccess()) {
                    return Response.error(res.getMessage(), null);
                }
            }
            else {
                Response<String> res = store.RemoveOrderFromStoreAfterSuccessfulPurchase(productsInStore);
                if (!res.isSuccess()) {
                    return Response.error(res.getMessage(), null);
                }
            }
        }
        return Response.success("[SUCCESS] Successfully released the shopping cart.", null);
    }

    public Response<Double> calculateShoppingCartPrice(Map<Integer, Map<Integer, Integer>> shoppingCartContents) {
        double price = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCartContents.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            Response<String> res;
            Store store = storeRepository.getActiveStore(storeID);
            if (store == null) {
                Store deactivatedStore = storeRepository.getDeactivatedStore(storeID);
                if (deactivatedStore == null) {
                    return Response.error("Store with ID: " + storeID + " doesn't exist", null);
                }
                res = deactivatedStore.calculateShoppingCartPrice(productsInStore);
            }
            else {
                res = store.calculateShoppingCartPrice(productsInStore);
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
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to make complex discount in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        if (!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to edit discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit discounts", null);
        }
        return store.makeComplexDiscount(username, discountId1, discountId2, discountType);
    }

    public Response<String> makeConditionDiscount(String username, Integer storeID, Integer discountId, Integer conditionId) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to make condition discount in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        if (!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to edit discount in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit discounts", null);
        }
        return store.makeConditionDiscount(username, discountId, conditionId);
    }

    public Response<String> addSimplePolicyToStore(String username, Integer storeID, Double amount, Double minAmount, Double maxAmount, String type,String value) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to add simple policy to store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        if (!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to edit policy in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit policies", null);
        }
        return store.addSimplePolicyToStore(username, amount, minAmount, maxAmount, type, value);
    }

    public Response<String> removeProductFromCategory(Integer productId, String category, Integer storeId, String username) {
        Store store = storeRepository.getActiveStore(storeId);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to remove product from category in store: " + storeId + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        return store.removeProductFromCategory(productId, category, username);
    }

    public Response<String> makeComplexPolicy(String username, Integer storeID, Integer policyId1, Integer policyId2, String conditionType) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to make complex policy in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        if (!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to edit policy in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit policies", null);
        }
        return store.makeComplexPolicy(username, policyId1, policyId2, conditionType);
    }

    public Response<List<ConditionDTO>> getPoliciesFromStore(Integer storeID) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            Store deactivatedStore = storeRepository.getDeactivatedStore(storeID);
            if (deactivatedStore != null) {
                return deactivatedStore.getPolicies();
            }
            return Response.error("Store doesn't exist", null);
        }
        return store.getPolicies();
    }

    public Response<String> makeConditionPolicy(String username, Integer storeID, Integer policyId, Integer conditionId) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to make condition policy in store: " + storeID + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        if (!hasPermission(storeID, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to edit policy in store: " + storeID + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to edit policies", null);
        }
        return store.makeConditionPolicy(username, policyId, conditionId);
    }

    public Response<String> removePolicy(Integer storeId, String username, Integer policyId) {
        Store store = storeRepository.getActiveStore(storeId);
        if (store == null) {
            SystemLogger.error("[ERROR] " + username + " tried to remove policy in store: " + storeId + " but the store doesn't exist / deactivated");
            return Response.error("Store doesn't exist / deactivated", null);
        }
        if (!hasPermission(storeId, username, "MANAGE_DISCOUNTS_POLICIES")) {
            SystemLogger.error("[ERROR] " + username + " tried to remove policy in store: " + storeId + " but the user is not the store owner or manager");
            return Response.error("You don't have permission to remove policies", null);
        }
        return store.removePolicy(policyId);
    }

    public boolean isNominatorOf(Integer storeId, String username, String manager) {
        Store store = storeRepository.getActiveStore(storeId);
        if (store == null) {
            return false;
        }
        return store.isNominatorOf(username, manager);
    }

    public Response<List<String>> reopenStore(Integer storeID, String currentUsername) {
        Store deactivatedStore = storeRepository.getDeactivatedStore(storeID);
        if (deactivatedStore == null) {
            Store store = storeRepository.getActiveStore(storeID);
            if (store != null) {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to reopen store: " + storeID + " but the store doesn't exist");
                return Response.error("Store doesn't exist", null);
            }
            else {
                SystemLogger.error("[ERROR] " + currentUsername + " tried to reopen store: " + storeID + " but the store is already opened");
                return Response.error("Store: " + getStoreNameByID(storeID, currentUsername) + " is already opened", null);
            }
        }
        if (!isStoreCreator(storeID, currentUsername)) { //The subscriber is not the store manager
            SystemLogger.error("[ERROR] " + currentUsername + " tried to reopen store: " + storeID + " but the user is not the store creator");
            return Response.error("The user trying to do this action is not the store creator.", null);
        }
        storeRepository.removeDeactivatedStore(storeID);
        storeRepository.addActiveStore(deactivatedStore);
        SystemLogger.info("[SUCCESS] " + currentUsername + " reopened store: " + storeID + " successfully");
        return Response.success("Store: " + getStoreNameByID(storeID, currentUsername) + " was reopened successfully", new ArrayList<>(deactivatedStore.getSubscribers().keySet()));
    }

    public boolean isStoreActive(Integer storeID) {
        return storeRepository.isOpenedStore(storeID);
    }

    public Response<ArrayList<String>> retrieveAllCategoriesFromAllStore() {
        Map<Integer, Store> stores = storeRepository.getActiveStores();
        ArrayList<String> categories = new ArrayList<>();
        for (Store store : stores.values()) {
            Response<ArrayList<String>> response = store.retrieveAllCategoriesFromAllStore();
            if (response.isSuccess()) {
                for (String category : response.getData()) {
                    if (!categories.contains(category)) {
                        categories.add(category);
                    }
                }
            } else {
                SystemLogger.error("[ERROR] Couldn't retrieve categories from store: " + store.getName());
            }
        }

        return Response.success("All categories were retrieved successfully", categories);
    }

    public Response<List<String>> getAllStores() {
        Map<Integer, Store> stores = storeRepository.getActiveStores();
        List<String> storesList = new ArrayList<>();
        for (Store store : stores.values()) {
            storesList.add(store.getName());
        }
        return Response.success("All stores were retrieved successfully", storesList);
    }

    public Response<Boolean> isExistAlcohol(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        for (Map.Entry<Integer, Map<Integer, Integer>> storeEntry : shoppingCart.entrySet()) {
            Integer storeID = storeEntry.getKey();
            Map<Integer, Integer> productsInStore = storeEntry.getValue();
            Store store = storeRepository.getActiveStore(storeID);
            if (store == null) {
                Store deactivatedStore = storeRepository.getDeactivatedStore(storeID);
                if (deactivatedStore != null) {
                    if (deactivatedStore.isExistAlcohol(productsInStore).getData()) {
                        return Response.success("Alcohol exists in the shopping cart", true);
                    }
                    return Response.success("Alcohol doesn't exist in the shopping cart", false);
                }
                return Response.error("Store with ID: " + storeID + " doesn't exist", false);
            }
            if (store.isExistAlcohol(productsInStore).getData()) {
                return Response.success("Alcohol exists in the shopping cart", true);
            }
        }
        return Response.success("Alcohol doesn't exist in the shopping cart", false);
    }

    public List<String> getDiscountsStrings(int storeID) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            return new ArrayList<>();
        }
        return store.getDiscountsStrings();
    }

    public List<String> getPoliciesString(int storeID) {
        Store store = storeRepository.getActiveStore(storeID);
        if (store == null) {
            return new ArrayList<>();
        }
        return store.getPoliciesString();
    }


}
