package Facades;

import Domain.Repo.StoreRepository;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
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

    public Store getStore(String storeID) {
        return storeRepository.getStore(storeID);
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

    public Response<String> setProductPrice(int productID, int newPrice, String storeID, String userName) {
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

    public Response<String> retrieveProductsByCategory(String storeID, String category, String userName) {
        return storeRepository.retrieveProductsByCategory(storeID, category, userName);
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
}
