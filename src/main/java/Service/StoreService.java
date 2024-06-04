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

    /**
     * This method creates a new store
     * @param storeName the name of the store
     * @param creatorUsername the username of the creator
     * @param token the token of the creator
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> addStore(String storeName, String creatorUsername, String token) {
        SystemLogger.info("[START] User: " + creatorUsername + " is trying to create a store with name: " + storeName);
        if (userService.isValidToken(token, creatorUsername)) {
            Response<String> response =  storeFacade.openStore(storeName, creatorUsername);
            if (response.isSuccess()) {
                userService.addCreatorRole(creatorUsername, response.getData());
                return response;
            }
        }
        SystemLogger.error("[ERROR] User: " + creatorUsername + " tried to add a store but the token was invalid");
        return Response.error("Invalid token", null);
    }


    /**
     * This method checks if the user is the owner of the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @return If the user is the owner of the store, returns true. <br> If not, returns false.
     */
    public boolean isStoreOwner(String storeID, String username) {
        return storeFacade.isStoreOwner(storeID, username);
    }

    /**
     * This method checks if the user is a manager of the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @return If the user is a manager of the store, returns true. <br> If not, returns false.
     */
    public boolean isStoreManager(String storeID, String username) {
        return storeFacade.isStoreManager(storeID, username);
    }

    /**
     * This method checks if the user is the creator of the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @return If the user is the creator of the store, returns true. <br> If not, returns false.
     */
    public boolean isStoreCreator(String storeID, String username) {
        return storeFacade.isStoreCreator(storeID, username);
    }

    /**
     * This method closes a store, moving it to the inactive stores.
     * @param storeID the ID of the store
     * @param username the username of the user (store's creator)
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> closeStore(String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to close store: " + storeID);
        if (userService.isValidToken(token, username)) {
            Response<List<String>> storeCloseResponse = storeFacade.closeStore(storeID, username);
            if (!storeCloseResponse.isSuccess()) {
                return Response.error(storeCloseResponse.getMessage(), null);
            }
            return userService.sendCloseStoreNotification(storeCloseResponse.getData(), storeID);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to close store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method adds a permission to a manager
     * @param storeID the ID of the store
     * @param username the username of the user (store's creator / owner)
     * @param managerUsername the username of the manager
     * @param permission the permission to add
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> addManagerPermissions(String storeID, String username, String managerUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add permissions: " + permission + " to " + managerUsername);
        if (userService.isValidToken(token, username)) {
            if (!userService.userExists(managerUsername)) {
                SystemLogger.error("[ERROR] User: " + managerUsername + " does not exist");
                return Response.error("User: " + managerUsername + " does not exist", null);
            }
            return storeFacade.addManagerPermissions(storeID, username, managerUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to add permissions: " + permission + " to " + managerUsername + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method removes a permission from a manager
     * @param storeID the ID of the store
     * @param username the username of the user (store's creator / owner)
     * @param managerUsername the username of the manager
     * @param permission the permission to remove
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> removeManagerPermissions(String storeID, String username, String managerUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove permissions: " + permission + " from " + managerUsername);
        if (userService.isValidToken(token, username)) {
            if (!userService.userExists(managerUsername)) {
                SystemLogger.error("[ERROR] User: " + managerUsername + " does not exist");
                return Response.error("User: " + managerUsername + " does not exist", null);
            }
            return storeFacade.removeManagerPermissions(storeID, username, managerUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to remove permissions: " + permission + " from " + managerUsername + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method nominates a new owner for the store
     * @param storeID the ID of the store
     * @param nominee the username of the nominee
     * @param nominator the username of the nominator
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> nominateOwner(String storeID, String nominee, String nominator) {
        return storeFacade.nominateOwner(storeID, nominee, nominator);
    }

    /**
     * This method removes a subscriber from the store
     * @param storeID the ID of the store
     * @param currentUsername the username of the user (store's creator / owner / manager)
     * @param subscriberUsername the username of the subscriber
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> removeStoreSubscription(String storeID, String currentUsername, String subscriberUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to remove " + subscriberUsername + " subscription from store: " + storeID);
        if (userService.isValidToken(token, currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return storeFacade.removeStoreSubscription(storeID, currentUsername);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to remove " + subscriberUsername + " subscription from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method nominates a new manager for the store
     * @param storeID the ID of the store
     * @param nominee the username of the user (store's creator / owner)
     * @param permissions the permissions of the new manager
     * @param nominator the username of the nominator
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> nominateManager(String storeID, String nominee, List<String> permissions, String nominator) {
        return storeFacade.nominateManager(storeID, nominee, permissions, nominator);
    }

    /**
     * This method requests the status of the employees in the store
     * @param storeID the ID of the store
     * @return If successful, returns a map of the employees and their status. <br> If not, returns an error message.
     */
    public Response<Map<String, String>> requestEmployeesStatus(String storeID) {
        return storeFacade.requestEmployeesStatus(storeID);
    }

    /**
     * This method requests the permissions of the managers in the store
     * @param storeID the ID of the store
     * @return If successful, returns a map of the managers and their permissions. <br> If not, returns an error message.
     */
    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID) {
        return storeFacade.requestManagersPermissions(storeID);
    }

    /**
     * This method waives a subscriber's ownership of a store.
     * @param storeID the ID of the store
     * @param username the username of the user (store's owner)
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<Set<String>> waiveOwnership(String storeID, String username) {
        return storeFacade.waiveOwnership(storeID, username);
    }

    /**
     * This method creates an owner nomination message
     * @param storeID the ID of the store
     * @param nominator the username of the user (store's creator / owner)
     * @param nominee the username of the nominee
     * @return If successful, returns an owner nomination message. <br> If not, returns an error message.
     */
    public Response<Message> makeNominateOwnerMessage(String storeID, String nominator, String nominee) {
        return storeFacade.makeNominateOwnerMessage(storeID, nominator, nominee);
    }

    /**
     * This method creates a manager nomination message
     * @param storeID the ID of the store
     * @param currentUsername the username of the user (store's creator / owner)
     * @param subscriberUsername the username of the subscriber
     * @param permissions the permissions of the new manager
     * @return If successful, returns a manager nomination message. <br> If not, returns an error message.
     */
    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return storeFacade.makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
    }

    public boolean storeExists(String storeID) {
        return storeFacade.storeExists(storeID);
    }

    public StoreFacade getStoreFacade() {
        return storeFacade;
    }

    /**
     * This method sets the quantity of a product in the store
     * @param productID the ID of the product
     * @param quantity the new quantity
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> setProductQuantity(int productID, int quantity, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set quantity of product: " + productID + " to: " + quantity);
        if (userService.isValidToken(token, username)) {
            return storeFacade.setProductQuantity(productID, quantity, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to set quantity of product: " + productID + " to: " + quantity + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method adds quantity to a product in the store
     * @param productID the ID of the product
     * @param amountToAdd the amount to add
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> addProductQuantity(int productID, int amountToAdd, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add quantity of product: " + productID + " by: " + amountToAdd);
        if (userService.isValidToken(token, username)) {
            return storeFacade.addProductQuantity(productID, amountToAdd, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to add quantity of product: " + productID + " by: " + amountToAdd + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the quantity of a product in the store
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the quantity of the product. <br> If not, returns an error message.
     */
    public Response<String> getProductQuantity(int productID, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get quantity of product: " + productID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getProductQuantity(productID, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get quantity of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the name of a product in the store
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the name of the product. <br> If not, returns an error message.
     */
    public Response<String> getProductName(int productID, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get product name of product: " + productID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getProductName(productID, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get product name of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method sets the name of a product in the store
     * @param productID the ID of the product
     * @param newName the new name
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> setProductName(int productID, String newName, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set product name of product: " + productID + " to: " + newName);
        if (userService.isValidToken(token, username)) {
            return storeFacade.setProductName(productID, newName, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to set product name of product: " + productID + " to: " + newName + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the price of a product in the store
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the price of the product. <br> If not, returns an error message.
     */
    public Response<String> getProductPrice(int productID, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get product price of product: " + productID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getProductPrice(productID, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get product price of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method sets the price of a product in the store
     * @param productID the ID of the product
     * @param newPrice the new price
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> setProductPrice(int productID, int newPrice, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set product price of product: " + productID + " to: " + newPrice);
        if (userService.isValidToken(token, username)) {
            return storeFacade.setProductPrice(productID, newPrice, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to set product price of product: " + productID + " to: " + newPrice + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the description of a product in the store
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the description of the product. <br> If not, returns an error message.
     */
    public Response<String> getProductDescription(int productID, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get product description of product: " + productID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getProductDescription(productID, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get product description of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method sets the description of a product in the store
     * @param productID the ID of the product
     * @param newDescription the new description
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> setProductDescription(int productID, String newDescription, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set product description of product: " + productID + " to: " + newDescription);
        if (userService.isValidToken(token, username)) {
            return storeFacade.setProductDescription(productID, newDescription, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to set product description of product: " + productID + " to: " + newDescription + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the products of a store by category
     * @param storeID the ID of the store
     * @param category the category of the products
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a list of products. <br> If not, returns an error message.
     */
    public Response<ArrayList<ProductDTO>> retrieveProductsByCategory(String storeID, String category, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to retrieve products by category: " + category);
        if (userService.isValidToken(token, username)) {
            return storeFacade.retrieveProductsByCategory(storeID, category, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to retrieve products by category: " + category + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the categories of a product in the store
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the categories of the product. <br> If not, returns an error message.
     */
    public Response<String> retrieveProductCategories(int productID, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to retrieve categories of product: " + productID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.retrieveProductCategories(productID, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to retrieve categories of product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method assigns a product to a category
     * @param productID the ID of the product
     * @param category the category to assign
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> assignProductToCategory(int productID, String category, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to assign product: " + productID + " to category: " + category);
        if (userService.isValidToken(token, username)) {
            return storeFacade.assignProductToCategory(productID, category, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to assign product: " + productID + " to category: " + category + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method removes a category from the store
     * @param storeID the ID of the store
     * @param category the category to remove
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> removeCategoryFromStore(String storeID, String category, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove category: " + category);
        if (userService.isValidToken(token, username)) {
            return storeFacade.removeCategoryFromStore(storeID, category, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to remove category: " + category + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves a product from the store
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the product. <br> If not, returns an error message.
     */
    public Response<ProductDTO> getProductFromStoreByID(int productID, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get product: " + productID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getProductFromStore(productID, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get product: " + productID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves all products from the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a list of products. <br> If not, returns an error message.
     */
    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get all products from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getAllProductsFromStore(storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get all products from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the store id by its name
     * @param storeName the name of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a list of categories. <br> If not, returns an error message.
     */
    public Response<String> getStoreIDbyName(String storeName, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get storeID by storeName: " + storeName);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getStoreIDbyName(storeName, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get storeID by storeName: " + storeName + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method adds a product to the store
     * @param storeID the ID of the store
     * @param name the name of the product
     * @param desc the description of the product
     * @param price the price of the product
     * @param quantity the quantity of the product
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> addProductToStore(String storeID, String name, String desc, int price, int quantity, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add product: " + name + " to store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.addProductToStore(storeID, name, desc, price, quantity, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to add product: " + name + " to store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method adds a product to the store, assigned to a list of categories
     * @param storeID the ID of the store
     * @param name the name of the product
     * @param desc the description of the product
     * @param price the price of the product
     * @param quantity the quantity of the product
     * @param categories the categories of the product
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> addProductToStore(String storeID, String name, String desc, int price, int quantity, ArrayList<String> categories, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add product: " + name + " to store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.addProductToStore(storeID, name, desc, price, quantity, categories, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to add product: " + name + " to store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method removes a product from the store
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> removeProductFromStore(int productID, String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove product: " + productID + " from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.removeProductFromStore(productID, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to remove product: " + productID + " from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves a product from the store by its name
     * @param storeID the ID of the store
     * @param productName the name of the product
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the product. <br> If not, returns an error message.
     */
    public Response<ProductDTO> getProductFromStoreByName(String storeID, String productName, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get product: " + productName + " from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getProductByName(storeID, productName, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get product: " + productName + " from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

//    public Response<String> getStoreIDByName(String storeName, String username, String token) {
//        SystemLogger.info("[START] User: " + username + " is trying to get storeID by storeName: " + storeName);
//        if (userService.isValidToken(token, username)) {
//            return storeFacade.getStoreIDByName(storeName, username);
//        }
//        SystemLogger.error("[ERROR] User: " + username + " tried to get storeID by storeName: " + storeName + " but the token was invalid");
//        return Response.error("Invalid token", null);
//    }

    /**
     * This method retrieves the name of a store by its ID
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the name of the store. <br> If not, returns an error message.
     */
    public Response<String> getStoreNameByID(String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get storeName by storeID: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getStoreNameByID(storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get storeName by storeID: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the store by its ID
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the store. <br> If not, returns an error message.
     */
    public Response<StoreDTO> getStoreByID(String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to search store by storeID: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getStoreByID(storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to search store by storeID: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves if the user is a subscriber of the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @return If the user is a subscriber of the store, returns true. <br> If not, returns false.
     */
    public boolean isStoreSubscriber(String storeID, String username) {
        return storeFacade.isStoreSubscriber(storeID, username);
    }

    /**
     * This method retrieves if a manager has a permission
     * @param storeID the ID of the store
     * @return If the manager has that permission, returns true. <br> If not, returns false.
     */
    public boolean hasPermission(String storeID, String username, String permission) {
        return storeFacade.hasPermission(storeID, username, permission);
    }

    /**
     * This method retrieves the permissions list
     * @return a set of permissions
     */
    public Set<String> getPermissionsList() {
        return storeFacade.getPermissionsList();
    }

    public Response<Map<String, String>> getStoresRoleWithName(Map<String, String> storesRole) {
        return storeFacade.getStoresRoleWithName(storesRole);
    }
}

