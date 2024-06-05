package Facades;

import Domain.Repo.StoreRepository;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.StoreDTO;
import Utilities.Messages.Message;
import Utilities.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoreFacade {
    private StoreRepository storeRepository;

    public StoreFacade() {
        storeRepository = new StoreRepository();
    }


    public boolean isStoreManager(String storeID, String currentUsername) {
        return storeRepository.isStoreManager(storeID, currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(String storeID, String currentUsername, String subscriberUsername) {
        return storeRepository.makeNominateOwnerMessage(storeID, currentUsername, subscriberUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return storeRepository.makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission ) {
        return storeRepository.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return storeRepository.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return storeRepository.isStoreCreator(storeID, currentUsername);
    }

    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        return storeRepository.closeStore(storeID, currentUsername);
    }

    public Response<Map<String, String>> requestEmployeesStatus(String storeID){
        return storeRepository.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID){
        return storeRepository.requestManagersPermissions(storeID);
    }

    public Response<String> openStore(String storeID, String creator) {
        return storeRepository.addStore(storeID, creator);
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return storeRepository.isStoreOwner(storeID, currentUsername);
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }


    public Response<String> nominateOwner(String storeID, String currentUsername, String nominatorUsername) {
        return storeRepository.nominateOwner(storeID, currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(String storeID, String currentUsername) {
        return storeRepository.removeStoreSubscription(storeID, currentUsername);
    }

    public Response<String> nominateManager(String storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        return storeRepository.nominateManager(storeID, currentUsername, permissions, nominatorUsername);
    }

    public Response<Set<String>> waiveOwnership(String storeID, String currentUsername) {
        return storeRepository.waiveOwnership(storeID, currentUsername);
    }

    public boolean storeExists(String storeID) {
        return storeRepository.storeExists(storeID);
    }


    public Response<String> setProductQuantity(int productID, int quantity, String storeID, String userName) {
        return storeRepository.setProductQuantity(productID, quantity, storeID, userName);
    }

    public Response<String> addProductQuantity(int productID, int amountToAdd, String storeID, String userName) {
        return storeRepository.addProductQuantity(productID, amountToAdd, storeID, userName);
    }

    public Response<String> getProductName(int productID, String storeID, String userName) {
        return storeRepository.getProductName(productID, storeID, userName);
    }

    public Response<String> setProductName(int productID, String newName, String storeID, String userName) {
        return storeRepository.setProductName(productID, newName, storeID, userName);
    }

    public Response<String> getProductPrice(int productID, String storeID, String userName) {
        return storeRepository.getProductPrice(productID, storeID, userName);
    }

    public Response<String> setProductPrice(int productID, double newPrice, String storeID, String userName) {
        return storeRepository.setProductPrice(productID, newPrice, storeID, userName);
    }

    public Response<String> getProductDescription(int productID, String storeID, String userName) {
        return storeRepository.getProductDescription(productID, storeID, userName);
    }

    public Response<String> setProductDescription(int productID, String newDescription, String storeID, String userName) {
        return storeRepository.setProductDescription(productID, newDescription, storeID, userName);
    }

    public Response<String> getProductQuantity(int productID, String storeID, String userName) {
        return storeRepository.getProductQuantity(productID, storeID, userName);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(String storeID, String category, String userName) {
        return storeRepository.retrieveProductsByCategoryFrom_OneStore(storeID, category, userName);
    }

    public Response<String> retrieveProductCategories(int productID, String storeID, String userName) {
        return storeRepository.retrieveProductCategories(productID, storeID, userName);
    }

    public Response<String> assignProductToCategory(int productID, String category, String storeID, String userName) {
        return storeRepository.assignProductToCategory(productID, category, storeID, userName);
    }

    public Response<String> removeCategoryFromStore(String storeID, String category, String userName) {
        return storeRepository.removeCategoryFromStore(storeID, category, userName);
    }


    public Response<ProductDTO> getProductFromStore(int productID, String storeID, String userName) {
        return storeRepository.getProductFromStore(productID, storeID, userName);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(String storeID, String userName) {
        return storeRepository.getAllProductsFromStore(storeID, userName);
    }


    public Response<String> getStoreIDbyName(String storeName, String userName) {
        return storeRepository.getStoreIDbyName(storeName, userName);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, double price, int quantity, String userName) {
        return storeRepository.addProductToStore(storeID, name, desc, price, quantity, userName);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, double price, int quantity, ArrayList<String> categories, String userName) {
        return storeRepository.addProductToStore(storeID, name, desc, price, quantity, categories, userName);
    }

    public Response<String> removeProductFromStore(int productID, String storeID, String userName) {
        return storeRepository.removeProductFromStore(productID, storeID, userName);
    }

    public Response<ProductDTO> viewProductFromStoreByName(String storeID, String productName, String userName) {
        return storeRepository.viewProductFromStoreByName(storeID, productName, userName);
    }

    public Response<String> getStoreIDByName(String storeName, String userName) {
        return storeRepository.getStoreIDByName(storeName, userName);
    }

    public Response<StoreDTO> getStoreByID(String storeID, String userName) {
        return storeRepository.getStoreByID(storeID, userName);
    }

    public Response<String> getStoreNameByID(String storeID, String userName) {
        return storeRepository.getStoreNameByID(storeID, userName);
    }

    public boolean isStoreSubscriber(String storeID, String UserName) {
        return storeRepository.isStoreSubscriber(storeID, UserName);
    }

    public boolean hasPermission(String storeID, String username, String permission) {
        return storeRepository.hasPermission(storeID, username, permission);
    }

    public Set<String> getPermissionsList() {
        return storeRepository.getPermissionsList();
    }
    public Response<String> isProductExist(String storeID, String productID) {
        return storeRepository.isProductExist(storeID, productID);
    }
    
    public Response<Map<String, String>> getStoresRoleWithName(Map<String, String> storesRole) {
        return storeRepository.getStoresRoleWithName(storesRole);
    }

    public Response<ArrayList<ProductDTO>> getProductsFromAllStoresByName(String productName) {
        return storeRepository.viewProductFromAllStoresByName(productName);
    }

    public Response<ArrayList<ProductDTO>> getProductsFromAllStoresByCategory(String category) {
        return storeRepository.viewProductFromAllStoresByCategory(category);
    }

    public Response<String> isCategoryExist(String storeID, String category){
        return storeRepository.isCategoryExist(storeID, category);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByName(String productName) {
        return storeRepository.viewProductFromAllStoresByName(productName);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByCategory(String category) {
        return storeRepository.viewProductFromAllStoresByCategory(category);
    }




}
