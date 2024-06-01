package Service;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.StoreDTO;
import Facades.StoreFacade;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoreService {
    private StoreFacade storeFacade;
    private UserService userService;

    public StoreService() {
        storeFacade = new StoreFacade();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Response<String> addStore(String name, String creator, String token) {
        SystemLogger.info("[START] User: " + creator + " is trying to create a store with name: " + name);
        if (userService.isValidToken(token, creator)) {
            return storeFacade.openStore(name, creator);
        }
        SystemLogger.error("[ERROR] User: " + creator + " tried to add a store but the token was invalid");
        return Response.error("Invalid token", null);
    }

//    public Store getStore(String storeID){
//        return market.getMarketFacade().getStoreRepository().getStore(storeID);
//    }


    public boolean isStoreOwner(String storeID, String currentUsername) {
        return storeFacade.isStoreOwner(storeID, currentUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return storeFacade.isStoreManager(storeID, currentUsername);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return storeFacade.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> closeStore(String storeID, String currentUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to close store: " + storeID);
        if (userService.isValidToken(token, currentUsername)) {
            Response<List<String>> storeCloseResponse = storeFacade.closeStore(storeID, currentUsername);
            if (!storeCloseResponse.isSuccess()) {
                return Response.error(storeCloseResponse.getMessage(), null);
            }
            return userService.sendCloseStoreNotification(storeCloseResponse.getData(), storeID);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to close store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to add permissions: " + permission + " to " + subscriberUsername);
        if (userService.isValidToken(token, currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return storeFacade.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to remove permissions: " + permission + " from " + subscriberUsername);
        if (userService.isValidToken(token, currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return storeFacade.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> nominateOwner(String storeID, String currentUsername, String nominatorUsername) {
        return storeFacade.nominateOwner(storeID, currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(String storeID, String currentUsername) {
        return storeFacade.removeStoreSubscription(storeID, currentUsername);
    }

    public Response<String> nominateManager(String storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        return storeFacade.nominateManager(storeID, currentUsername, permissions, nominatorUsername);
    }

    public Response<Map<String, String>> requestEmployeesStatus(String storeID) {
        return storeFacade.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID) {
        return storeFacade.requestManagersPermissions(storeID);
    }

    public Response<Set<String>> waiveOwnership(String storeID, String currentUsername) {
        return storeFacade.waiveOwnership(storeID, currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(String storeID, String currentUsername, String subscriberUsername) {
        return storeFacade.makeNominateOwnerMessage(storeID, currentUsername, subscriberUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return storeFacade.makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
    }

    public boolean storeExists(String storeID) {
        return storeFacade.storeExists(storeID);
    }

    public StoreFacade getStoreFacade() {
        return storeFacade;
    }

    public Response<String> setProductQuantity(int productID, int quantity, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to set quantity of product: " + productID + " to: " + quantity);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.setProductQuantity(productID, quantity, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to set quantity of product: " + productID + " to: " + quantity + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> addProductQuantity(int productID, int amountToAdd, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to add quantity of product: " + productID + " by: " + amountToAdd);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.addProductQuantity(productID, amountToAdd, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to add quantity of product: " + productID + " by: " + amountToAdd + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> getProductQuantity(int productID, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get quantity of product: " + productID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getProductQuantity(productID, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get quantity of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> getProductName(int productID, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get product name of product: " + productID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getProductName(productID, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get product name of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> setProductName(int productID, String newName, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to set product name of product: " + productID + " to: " + newName);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.setProductName(productID, newName, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to set product name of product: " + productID + " to: " + newName + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> getProductPrice(int productID, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get product price of product: " + productID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getProductPrice(productID, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get product price of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> setProductPrice(int productID, int newPrice, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to set product price of product: " + productID + " to: " + newPrice);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.setProductPrice(productID, newPrice, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to set product price of product: " + productID + " to: " + newPrice + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> getProductDescription(int productID, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get product description of product: " + productID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getProductDescription(productID, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get product description of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> setProductDescription(int productID, String newDescription, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to set product description of product: " + productID + " to: " + newDescription);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.setProductDescription(productID, newDescription, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to set product description of product: " + productID + " to: " + newDescription + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategory(String storeID, String category, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to retrieve products by category: " + category);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.retrieveProductsByCategory(storeID, category, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to retrieve products by category: " + category + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> retrieveProductCategories(int productID, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to retrieve categories of product: " + productID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.retrieveProductCategories(productID, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to retrieve categories of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> assignProductToCategory(int productID, String category, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to assign product: " + productID + " to category: " + category);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.assignProductToCategory(productID, category, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to assign product: " + productID + " to category: " + category + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> removeCategoryFromStore(String storeID, String category, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to remove category: " + category);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.removeCategoryFromStore(storeID, category, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to remove category: " + category + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<ProductDTO> getProductFromStoreByID(int productID, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get product: " + productID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getProductFromStore(productID, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get all products from store: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getAllProductsFromStore(storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get all products from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> getStoreIDbyName(String storeName, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get storeID by storeName: " + storeName);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getStoreIDbyName(storeName, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get storeID by storeName: " + storeName + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, int price, int quantity, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to add product: " + name + " to store: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.addProductToStore(storeID, name, desc, price, quantity, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to add product: " + name + " to store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> addProductToStore(String storeID, String name, String desc, int price, int quantity, ArrayList<String> categories, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to add product: " + name + " to store: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.addProductToStore(storeID, name, desc, price, quantity, categories, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to add product: " + name + " to store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> removeProductFromStore(int productID, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to remove product: " + productID + " from store: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.removeProductFromStore(productID, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to remove product: " + productID + " from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<ProductDTO> getProductFromStoreByName(String storeID, String productName, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get product: " + productName + " from store: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getProductByName(storeID, productName, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get product: " + productName + " from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> getStoreIDByName(String storeName, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get storeID by storeName: " + storeName);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getStoreIDByName(storeName, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get storeID by storeName: " + storeName + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> getStoreNameByID(String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to get storeName by storeID: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getStoreNameByID(storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to get storeName by storeID: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<StoreDTO> getStoreByID(String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to search store by storeID: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.getStoreByID(storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to search store by storeID: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }


    public boolean isStoreSubscriber(String storeID, String UserName) {
        return storeFacade.isStoreSubscriber(storeID, UserName);
    }

    public boolean hasPermission(String storeID, String username, String permission) {
        return storeFacade.hasPermission(storeID, username, permission);
    }

    public Set<String> getPermissionsList() {
        return storeFacade.getPermissionsList();
    }
}

