package Service;

import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.DiscountDTO;
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
    private final StoreFacade storeFacade;
    private UserService userService;
    private AdminService adminService;

    public StoreService() {
        storeFacade = new StoreFacade();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * This method creates a new store
     * @param storeName the name of the store
     * @param creatorUsername the username of the creator
     * @param token the token of the creator
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<Integer> addStore(String storeName, String creatorUsername, String token) {
        SystemLogger.info("[START] User: " + creatorUsername + " is trying to create a store with name: " + storeName);
        if (userService.isValidToken(token, creatorUsername)) {
            if (adminService.isSuspended(creatorUsername)) {
                SystemLogger.error("[ERROR] User: " + creatorUsername + " is suspended");
                return Response.error("You're suspended", null);
            }
            Response<Integer> response =  storeFacade.openStore(storeName, creatorUsername);
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
    public boolean isStoreOwner(Integer storeID, String username) {
        return storeFacade.isStoreOwner(storeID, username);
    }

    /**
     * This method checks if the user is a manager of the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @return If the user is a manager of the store, returns true. <br> If not, returns false.
     */
    public boolean isStoreManager(Integer storeID, String username) {
        return storeFacade.isStoreManager(storeID, username);
    }

    /**
     * This method checks if the user is the creator of the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @return If the user is the creator of the store, returns true. <br> If not, returns false.
     */
    public boolean isStoreCreator(Integer storeID, String username) {
        return storeFacade.isStoreCreator(storeID, username);
    }

    /**
     * This method closes a store, moving it to the inactive stores.
     * @param storeID the ID of the store
     * @param username the username of the user (store's creator)
     * @param token the token of the user
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> closeStore(Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to close store: " + storeID);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
            Response<List<String>> storeCloseResponse = storeFacade.closeStore(storeID, username);
            if (!storeCloseResponse.isSuccess()) {
                return Response.error(storeCloseResponse.getMessage(), null);
            }
            return userService.sendCloseStoreNotification(storeCloseResponse.getData(), getStoreNameByID(storeID, username, token).getData());
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
    public Response<String> addManagerPermissions(Integer storeID, String username, String managerUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add permissions: " + permission + " to " + managerUsername);
        if (userService.isValidToken(token, username)) {
            if (!userService.userExists(managerUsername)) {
                SystemLogger.error("[ERROR] User: " + managerUsername + " does not exist");
                return Response.error("User: " + managerUsername + " does not exist", null);
            }
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
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
    public Response<String> removeManagerPermissions(Integer storeID, String username, String managerUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove permissions: " + permission + " from " + managerUsername);
        if (userService.isValidToken(token, username)) {
            if (!userService.userExists(managerUsername)) {
                SystemLogger.error("[ERROR] User: " + managerUsername + " does not exist");
                return Response.error("User: " + managerUsername + " does not exist", null);
            }
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
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
    public Response<String> nominateOwner(Integer storeID, String nominee, String nominator) {
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
    public Response<String> removeStoreSubscription(Integer storeID, String currentUsername, String subscriberUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to remove " + subscriberUsername + " subscription from store: " + storeID);
        if (userService.isValidToken(token, currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            if (adminService.isSuspended(currentUsername)) {
                SystemLogger.error("[ERROR] User: " + currentUsername + " is suspended");
                return Response.error("You're suspended", null);
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
    public Response<String> nominateManager(Integer storeID, String nominee, List<String> permissions, String nominator) {
        return storeFacade.nominateManager(storeID, nominee, permissions, nominator);
    }

    /**
     * This method requests the status of the employees in the store
     * @param storeID the ID of the store
     * @return If successful, returns a map of the employees and their status. <br> If not, returns an error message.
     */
    public synchronized Response<Map<String, String>> requestEmployeesStatus(Integer storeID) {
        return storeFacade.requestEmployeesStatus(storeID);
    }

    /**
     * This method requests the permissions of the managers in the store
     * @param storeID the ID of the store
     * @return If successful, returns a map of the managers and their permissions. <br> If not, returns an error message.
     */
    public synchronized Response<Map<String, List<String>>> requestManagersPermissions(Integer storeID) {
        return storeFacade.requestManagersPermissions(storeID);
    }

    /**
     * This method waives a subscriber's ownership of a store.
     * @param storeID the ID of the store
     * @param username the username of the user (store's owner)
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<Set<String>> waiveOwnership(Integer storeID, String username) {
        return storeFacade.waiveOwnership(storeID, username);
    }

    /**
     * This method creates an owner nomination message
     * @param storeID the ID of the store
     * @param nominator the username of the user (store's creator / owner)
     * @param nominee the username of the nominee
     * @return If successful, returns an owner nomination message. <br> If not, returns an error message.
     */
    public Response<Message> makeNominateOwnerMessage(Integer storeID, String nominator, String nominee) {
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
    public Response<Message> makeNominateManagerMessage(Integer storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return storeFacade.makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
    }

    public boolean storeExists(Integer storeID) {
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
    public Response<String> setProductQuantity(int productID, int quantity, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set quantity of product: " + productID + " to: " + quantity);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<String> addProductQuantity(int productID, int amountToAdd, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add quantity of product: " + productID + " by: " + amountToAdd);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<String> getProductQuantity(int productID, Integer storeID, String username, String token) {
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
    public Response<String> getProductName(int productID, Integer storeID, String username, String token) {
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
    public Response<String> setProductName(int productID, String newName, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set product name of product: " + productID + " to: " + newName);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<Double> getProductPrice(Integer productID, Integer storeID, String username, String token) {
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
    public Response<String> setProductPrice(int productID, double newPrice, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set product price of product: " + productID + " to: " + newPrice);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<String> getProductDescription(int productID, Integer storeID, String username, String token) {
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
    public Response<String> setProductDescription(int productID, String newDescription, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to set product description of product: " + productID + " to: " + newDescription);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(Integer storeID, String category, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to retrieve products by category: " + category);
        if (userService.isValidToken(token, username)) {
            return storeFacade.retrieveProductsByCategoryFrom_OneStore(storeID, category, username);
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
    public Response<String> retrieveProductCategories(int productID, Integer storeID, String username, String token) {
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
    public Response<String> assignProductToCategory(int productID, String category, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to assign product: " + productID + " to category: " + category);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<String> removeCategoryFromStore(Integer storeID, String category, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove category: " + category);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<ProductDTO> viewProductFromStoreByID(int productID, Integer storeID, String username, String token) {
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
    public Response<ArrayList<ProductDTO>> getAllProductsFromStore(Integer storeID, String username, String token) {
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
    public Response<Integer> getStoreIDbyName(String storeName, String username, String token) {
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
     * category = "General", by default
     */
    public Response<String> addProductToStore(Integer storeID, String name, String desc, double price, int quantity, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add product: " + name + " to store: " + storeID);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<String> addProductToStore(Integer storeID, String name, String desc, double price, int quantity, ArrayList<String> categories, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to add product: " + name + " to store: " + storeID);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<String> removeProductFromStore(int productID, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove product: " + productID + " from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
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
    public Response<ProductDTO> viewProductFromStoreByName(Integer storeID, String productName, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get product: " + productName + " from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.viewProductFromStoreByName(storeID, productName, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get product: " + productName + " from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }


    /**
     * This method retrieves the name of a store by its ID
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the name of the store. <br> If not, returns an error message.
     */
    public Response<String> getStoreNameByID(Integer storeID, String username, String token) {
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
    public Response<StoreDTO> getStoreByID(Integer storeID, String username, String token) {
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
    public boolean isStoreSubscriber(Integer storeID, String username) {
        return storeFacade.isStoreSubscriber(storeID, username);
    }

    /**
     * This method retrieves if a manager has a permission
     * @param storeID the ID of the store
     * @return If the manager has that permission, returns true. <br> If not, returns false.
     */
    public boolean hasPermission(Integer storeID, String username, String permission) {
        return storeFacade.hasPermission(storeID, username, permission);
    }

    /**
     * This method retrieves the permissions list
     * @return a set of permissions
     */
    public Set<String> getPermissionsList() {
        return storeFacade.getPermissionsList();
    }

    public Response<String> isProductExist(Integer storeID, Integer productID) {
        return storeFacade.isProductExist(storeID, productID);
    }


    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByCategory(String category, String UserName ,String token) {
        SystemLogger.info("[START] User: " + " is trying to get product: " + category + " from all stores");
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.viewProductFromAllStoresByCategory(category);
        }
        SystemLogger.error("[ERROR] User: " + " tried to get product: " + category + " from all stores but the token was invalid");
        return Response.error("Invalid token", null);
    }

    //only for normal subscriber
    public Response<ArrayList<ProductDTO>> viewProductFromAllStoresByName(String productName, String UserName ,String token) {
        SystemLogger.info("[START] User: " + " is trying to get product: " + productName + " from all stores");
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.viewProductFromAllStoresByName(productName);
        }
        SystemLogger.error("[ERROR] User: " + " tried to get product: " + productName + " from all stores but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> isCategoryExist(Integer storeID, String category, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to check if category: " + category + " exists in store: " + storeID);
        if (userService.isValidToken(token, UserName)) {
            return storeFacade.isCategoryExist(storeID, category);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to check if category: " + category + " exists in store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

//    //may be deleted
//    public Response<ArrayList<ProductDTO>> getProductsFromAllStoresByCategory(String category, String UserName, String token) {
//        SystemLogger.info("[START] User: " + UserName + " is trying to search products by category: " + category);
//        if (userService.isValidToken(token, UserName)) {
//            return storeFacade.getProductsFromAllStoresByCategory(category);
//        }
//        SystemLogger.error("[ERROR] User: " + UserName + " tried to search products by category: " + category + " but the token was invalid");
//        return Response.error("Invalid token", null);
//    }
//
//    //may be deleted
//    public Response<ArrayList<ProductDTO>> getProductsFromAllStoresByName(String productName, String UserName, String token) {
//        SystemLogger.info("[START] User: " + UserName + " is trying to search products by name: " + productName);
//        if (userService.isValidToken(token, UserName)) {
//            return storeFacade.getProductsFromAllStoresByName(productName);
//        }
//        SystemLogger.error("[ERROR] User: " + UserName + " tried to search products by name: " + productName + " but the token was invalid");
//        return Response.error("Invalid token", null);
//    }

    /**
     * This method adds a simple discount to the store
     * @param username the username of the user
     * @param token the token of the user
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param category the category
     * @param percent the percent of the discount
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> CreateDiscountSimple(String username, String token, Integer productID, Integer storeID, String category, Double percent) {
        SystemLogger.info("[START] User: " + username + " is trying to create discount for product: " + productID + " in store: " + storeID);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
            return storeFacade.CreateDiscount(productID, storeID, username,category,percent);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to create discount");
        return Response.error("Invalid token", null);
    }




    public Response<List<ProductDTO>> LockProducts(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        return storeFacade.LockProducts(shoppingCart);
    }


    public Response<String> CalculateDiscounts(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        return storeFacade.CalculateDiscounts(shoppingCart);
    }

    public Response<String> unlockProductsBackToStore(Map<Integer, Map<Integer, Integer>> shoppingCart) {
        return storeFacade.unlockProductsBackToStore(shoppingCart);
    }

    /**
     * This method retrieves the discounts of a store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a list of discounts. <br> If not, returns an error message.
     */
    public Response<List<DiscountDTO>> getDiscountsFromStore(Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get discounts from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getDiscountsFromStore(storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get discounts from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method retrieves the policies of a store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a list of discounts. <br> If not, returns an error message.
     */
    public Response<List<ConditionDTO>> getPoliciesFromStore(Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get policies from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.getPoliciesFromStore(storeID);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get policies from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }


    /**
     * This method remove Discount from store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns the discount of the product. <br> If not, returns an error message.
     */
    public Response<String> removeDiscount(Integer storeID, String username, String token, Integer discountID) {
        SystemLogger.info("[START] User: " + username + " is trying to remove discount: " + discountID + " from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
            return storeFacade.removeDiscount(storeID, username, discountID);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to remove discount: " + discountID + " from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> RemoveOrderFromStoreAfterSuccessfulPurchase(Map<Integer, Map<Integer, Integer>> data) {
        return storeFacade.RemoveOrderFromStoreAfterSuccessfulPurchase(data);
    }

    public Response<Double> calculateShoppingCartPrice(Map<Integer, Map<Integer, Integer>> shoppingCartContents) {
        return storeFacade.calculateShoppingCartPrice(shoppingCartContents);
    }
    /**
     * This method adds a simple discount to the store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a list of discounts. <br> If not, returns an error message.
     */
    public Response<String> makeComplexDiscount(String username, String token, Integer storeID ,Integer discountId1, Integer discountId2, String discountType) {
        SystemLogger.info("[START] User: " + username + " is trying to create complex discount for store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.makeComplexDiscount( username,storeID ,discountId1,discountId2,discountType);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to create complex discount for store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }
    /**
     * This method retrieves the discounts of a store
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     * @return If successful, returns a list of discounts. <br> If not, returns an error message.
     */
    public Response<String> makeConditionDiscount(String username, String token, Integer storeID , Integer discountId, Integer conditionId) {
        SystemLogger.info("[START] User: " + username + " is trying to create complex discount for store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.makeConditionDiscount(username, storeID , discountId,conditionId);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to create complex discount for store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    /**
     * This method adds a simple purchase policy to the store
     * @param username the username of the user
     * @param token the token of the user
     * @param storeID the ID of the store
     * @param category the category
     * @param productID the ID of the product
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @param price the price
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> addSimplePolicyToStore(String username, String token, Integer storeID, String category, Integer productID, Double amount, Double minAmount, Double maxAmount, Boolean price) {
        SystemLogger.info("[START] User: " + username + " is trying to add simple purchase policy for product: " + productID + " in store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.addSimplePolicyToStore(username,category, storeID, productID, amount, minAmount, maxAmount,price);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to add simple purchase policy for product: " + productID + " in store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> makeComplexCondition(String username, String token, Integer storeID, Integer policyId1, Integer policyId2, String ConditionType) {
        SystemLogger.info("[START] User: " + username + " is trying to create complex policy for store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.makeComplexPolicy(username, storeID, policyId1, policyId2, ConditionType);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to create complex policy for store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> makeConditionPolicy(String username, String token, Integer storeID, Integer policyId, Integer conditionId) {
        SystemLogger.info("[START] User: " + username + " is trying to create complex policy for store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.makeConditionPolicy(username, storeID, policyId, conditionId);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to create complex policy for store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }


    public Response<String> removeProductFromCategory(int productId, String category, Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove product: " + productId + " from category: " + category + " in store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.removeProductFromCategory(productId, category, storeID, username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to remove product: " + productId + " from category: " + category + " in store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public Response<String> removePolicy(Integer storeID, String username, String token, Integer policyId) {
        SystemLogger.info("[START] User: " + username + " is trying to remove policy: " + policyId + " from store: " + storeID);
        if (userService.isValidToken(token, username)) {
            return storeFacade.removePolicy(storeID, username, policyId);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to remove policy: " + policyId + " from store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public boolean isNominatorOf(Integer storeID, String username, String manager) {
        return storeFacade.isNominatorOf(storeID, username, manager);
    }

    public Response<String> reopenStore(Integer storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to reopen store: " + storeID);
        if (userService.isValidToken(token, username)) {
            if (adminService.isSuspended(username)) {
                SystemLogger.error("[ERROR] User: " + username + " is suspended");
                return Response.error("You're suspended", null);
            }
            Response<List<String>> storeReopenResponse = storeFacade.reopenStore(storeID, username);
            if (!storeReopenResponse.isSuccess()) {
                return Response.error(storeReopenResponse.getMessage(), null);
            }
            return userService.sendReopenStoreNotification(storeReopenResponse.getData(), getStoreNameByID(storeID, username, token).getData());
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to reopen store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token", null);
    }

    public boolean isStoreActive(Integer storeID, String username, String token) {
        return storeFacade.isStoreActive(storeID);
    }

    public Response<ArrayList<String>> retrieveCategoriesFromAllStore(String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to retrieve all categories");
        if (userService.isValidToken(token, username)) {
            return storeFacade.retrieveAllCategoriesFromAllStore();
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to retrieve all categories  but the token was invalid");
        return Response.error("Invalid token", null);
    }
}

