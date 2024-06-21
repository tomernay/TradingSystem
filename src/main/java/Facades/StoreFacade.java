package Facades;

import Domain.Repo.StoreRepository;
import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.StoreDTO;
import Utilities.Messages.Message;
import Utilities.Response;

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
        return storeRepository.isStoreManager(storeID, currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(Integer storeID, String currentUsername, String subscriberUsername) {
        return storeRepository.makeNominateOwnerMessage(storeID, currentUsername, subscriberUsername);
    }

    public Response<Message> makeNominateManagerMessage(Integer storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return storeRepository.makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(Integer storeID, String currentUsername, String subscriberUsername, String permission ) {
        return storeRepository.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(Integer storeID, String currentUsername, String subscriberUsername, String permission) {
        return storeRepository.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public boolean isStoreCreator(Integer storeID, String currentUsername) {
        return storeRepository.isStoreCreator(storeID, currentUsername);
    }

    public Response<List<String>> closeStore(Integer storeID, String currentUsername) {
        return storeRepository.closeStore(storeID, currentUsername);
    }

    public Response<Map<String, String>> requestEmployeesStatus(Integer storeID){
        return storeRepository.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(Integer storeID){
        return storeRepository.requestManagersPermissions(storeID);
    }

    public Response<Integer> openStore(String storeName, String creator) {
        return storeRepository.addStore(storeName, creator);
    }

    public boolean isStoreOwner(Integer storeID, String currentUsername) {
        return storeRepository.isStoreOwner(storeID, currentUsername);
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }


    public Response<String> nominateOwner(Integer storeID, String currentUsername, String nominatorUsername) {
        return storeRepository.nominateOwner(storeID, currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(Integer storeID, String currentUsername) {
        return storeRepository.removeStoreSubscription(storeID, currentUsername);
    }

    public Response<String> nominateManager(Integer storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        return storeRepository.nominateManager(storeID, currentUsername, permissions, nominatorUsername);
    }

    public Response<Set<String>> waiveOwnership(Integer storeID, String currentUsername) {
        return storeRepository.waiveOwnership(storeID, currentUsername);
    }

    public boolean storeExists(Integer storeID) {
        return storeRepository.storeExists(storeID);
    }


    public Response<String> setProductQuantity(Integer productID, Integer quantity, Integer storeID, String userName) {
        return storeRepository.setProductQuantity(productID, quantity, storeID, userName);
    }

    public Response<String> addProductQuantity(Integer productID, Integer amountToAdd, Integer storeID, String userName) {
        return storeRepository.addProductQuantity(productID, amountToAdd, storeID, userName);
    }

    public Response<String> getProductName(Integer productID, Integer storeID, String userName) {
        return storeRepository.getProductName(productID, storeID, userName);
    }

    public Response<String> setProductName(Integer productID, String newName, Integer storeID, String userName) {
        return storeRepository.setProductName(productID, newName, storeID, userName);
    }

    public Response<Double> getProductPrice(Integer productID, Integer storeID, String userName) {
        return storeRepository.getProductPrice(productID, storeID, userName);
    }

    public Response<String> setProductPrice(Integer productID, Double newPrice, Integer storeID, String userName) {
        return storeRepository.setProductPrice(productID, newPrice, storeID, userName);
    }

    public Response<String> getProductDescription(Integer productID, Integer storeID, String userName) {
        return storeRepository.getProductDescription(productID, storeID, userName);
    }

    public Response<String> setProductDescription(Integer productID, String newDescription, Integer storeID, String userName) {
        return storeRepository.setProductDescription(productID, newDescription, storeID, userName);
    }

    public Response<String> getProductQuantity(Integer productID, Integer storeID, String userName) {
        return storeRepository.getProductQuantity(productID, storeID, userName);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(Integer storeID, String category, String userName) {
        return storeRepository.retrieveProductsByCategoryFrom_OneStore(storeID, category, userName);
    }

    public Response<String> retrieveProductCategories(Integer productID, Integer storeID, String userName) {
        return storeRepository.retrieveProductCategories(productID, storeID, userName);
    }

    public Response<String> assignProductToCategory(Integer productID, String category, Integer storeID, String userName) {
        return storeRepository.assignProductToCategory(productID, category, storeID, userName);
    }

    public Response<String> removeCategoryFromStore(Integer storeID, String category, String userName) {
        return storeRepository.removeCategoryFromStore(storeID, category, userName);
    }


    public Response<ProductDTO> getProductFromStore(Integer productID, Integer storeID, String userName) {
        return storeRepository.getProductFromStore(productID, storeID, userName);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(Integer storeID, String userName) {
        return storeRepository.getAllProductsFromStore(storeID, userName);
    }


    public Response<Integer> getStoreIDbyName(String storeName, String userName) {
        return storeRepository.getStoreIDbyName(storeName, userName);
    }

    public Response<String> addProductToStore(Integer storeID, String name, String desc, Double price, Integer quantity, String userName) {
        return storeRepository.addProductToStore(storeID, name, desc, price, quantity, userName);
    }

    public Response<String> addProductToStore(Integer storeID, String name, String desc, Double price, Integer quantity, ArrayList<String> categories, String userName) {
        return storeRepository.addProductToStore(storeID, name, desc, price, quantity, categories, userName);
    }

    public Response<String> removeProductFromStore(Integer productID, Integer storeID, String userName) {
        return storeRepository.removeProductFromStore(productID, storeID, userName);
    }

    public Response<ProductDTO> viewProductFromStoreByName(Integer storeID, String productName, String userName) {
        return storeRepository.viewProductFromStoreByName(storeID, productName, userName);
    }

//    public Response<String> getStoreIDByName(String storeName, String userName) {
//        return storeRepository.getStoreIDByName(storeName, userName);
//    }

    public Response<StoreDTO> getStoreByID(Integer storeID, String userName) {
        return storeRepository.getStoreByID(storeID, userName);
    }

    public Response<String> getStoreNameByID(Integer storeID, String userName) {
        return storeRepository.getStoreNameByID(storeID, userName);
    }

    public boolean isStoreSubscriber(Integer storeID, String UserName) {
        return storeRepository.isStoreSubscriber(storeID, UserName);
    }

    public boolean hasPermission(Integer storeID, String username, String permission) {
        return storeRepository.hasPermission(storeID, username, permission);
    }

    public Set<String> getPermissionsList() {
        return storeRepository.getPermissionsList();
    }
    public Response<String> isProductExist(Integer storeID, Integer productID) {
        return storeRepository.isProductExist(storeID, productID);
    }

    public Response<ArrayList<ProductDTO>> getProductsFromAllStoresByName(String productName) {
        return storeRepository.viewProductFromAllStoresByName(productName);
    }

    public Response<ArrayList<ProductDTO>> getProductsFromAllStoresByCategory(String category) {
        return storeRepository.viewProductFromAllStoresByCategory(category);
    }

    public Response<String> isCategoryExist(Integer storeID, String category){
        return storeRepository.isCategoryExist(storeID, category);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByName(String productName) {
        return storeRepository.viewProductFromAllStoresByName(productName);
    }

    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByCategory(String category) {
        return storeRepository.viewProductFromAllStoresByCategory(category);
    }

    public Response<List<ProductDTO>> LockProducts(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        return storeRepository.LockProducts(shoppingCart);
    }

    public Response<String> CreateDiscount(Integer productID, Integer storeID, String username, String category, Double percent) {
        return storeRepository.CreateDiscount(productID, storeID, category, percent, username);
    }

    public Response<String> CalculateDiscounts(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        return storeRepository.CalculateDiscounts(shoppingCart);
    }

    public Response<String> unlockProductsBackToStore(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        return storeRepository.unlockProductsBackToStore(shoppingCart);
    }

    public Response<List<DiscountDTO>> getDiscountsFromStore(Integer storeID, String username) {
        return storeRepository.getDiscountsFromStore(storeID, username);
    }

    public Response<String> removeDiscount(Integer storeID, String username, Integer discountID) {
        return storeRepository.removeDiscount(storeID, username, discountID);
    }

    public Response<String> RemoveOrderFromStoreAfterSuccessfulPurchase(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        return storeRepository.RemoveOrderFromStoreAfterSuccessfulPurchase(shoppingCart);
    }

    public Response<Double> calculateShoppingCartPrice(Map<Integer, Map<Integer, Integer>> shoppingCartContents) {
        return storeRepository.calculateShoppingCartPrice(shoppingCartContents);
    }

    public Response<String> makeComplexDiscount(String username, Integer storeID, Integer discountId1, Integer discountId2, String discountType) {
        return storeRepository.makeComplexDiscount(username, storeID, discountId1, discountId2, discountType);
    }

    public Response<String> makeConditionDiscount(String username, Integer storeID, Integer discountId, Integer conditionId) {
        return storeRepository.makeConditionDiscount(username, storeID, discountId, conditionId);
    }

    public Response<String> addSimplePolicyToStore(String username, String category, Integer storeID, Integer productID, Double amount, Double minAmount, Double maxAmount, Boolean price) {
        return storeRepository.addSimplePolicyToStore(username, storeID, category,productID, amount, minAmount, maxAmount, price);
    }

    public Response<String> removeProductFromCategory(Integer productId, String category, Integer storeId, String username) {
        return storeRepository.removeProductFromCategory(productId, category, storeId, username);
    }

    public Response<String> makeComplexPolicy(String username, Integer storeID, Integer policyId1, Integer policyId2, String conditionType) {
        return storeRepository.makeComplexPolicy(username, storeID, policyId1, policyId2, conditionType);
    }

    public Response<List<ConditionDTO>> getPoliciesFromStore(Integer storeID) {
        return storeRepository.getPoliciesFromStore(storeID);
    }

    public Response<String> makeConditionPolicy(String username, Integer storeID, Integer policyId, Integer conditionId) {
        return storeRepository.makeConditionPolicy(username, storeID, policyId, conditionId);
    }

    public Response<String> removePolicy(Integer storeId, String username, Integer policyId) {
        return storeRepository.removePolicy(storeId, username, policyId);
    }

    public boolean isNominatorOf(Integer storeId, String username, String manager) {
        return storeRepository.isNominatorOf(storeId, username, manager);
    }

    public Response<List<String>> reopenStore(Integer storeId, String username) {
        return storeRepository.reopenStore(storeId, username);
    }

    public boolean isStoreActive(Integer storeID) {
        return storeRepository.isStoreActive(storeID);
    }

    public Response<String> retrieveAllCategoriesFromStore(String username) {
        return storeRepository.retrieveAllCategoriesFromStore(username);
    }
}
