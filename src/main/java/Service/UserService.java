package Service;

import Facades.UserFacade;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import Utilities.SystemLogger;


import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserService {
    private UserFacade userFacade;
    private StoreService storeService;


    public UserService() {
        userFacade = new UserFacade();
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * This method connects a guest to the system.
     * @return If successful, returns a success message & the token. <br> If not, returns an error message.
     */
    public synchronized Response<List<String>> loginAsGuest() {
        return userFacade.loginAsGuest();
    }

    /**
     * This method disconnects a guest from the system.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public synchronized Response<String> logoutAsGuest(String username) {
        return userFacade.logoutAsGuest(username);
    }

    /**
     * This method connects a subscriber to the system.
     * @param username The username of the subscriber.
     * @param password The password of the subscriber.
     * @return If successful, returns a success message & the token. <br> If not, returns an error message.
     */
    public synchronized Response<String> loginAsSubscriber(String username, String password) {
        SystemLogger.info("[START] User: " + username + " is trying to login");
        return userFacade.loginAsSubscriber(username, password);
    }

    /**
     * This method disconnects a subscriber from the system.
     * @param username The username of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public synchronized Response<String> logoutAsSubscriber(String username) {
        SystemLogger.info("[START] User: " + username + " is trying to logout");
        return userFacade.logoutAsSubscriber(username);
    }

    /**
     * This method sends an owner nomination request to a subscriber.
     * @param storeID The store ID of the store.
     * @param nominatorUsername The username of the nominator.
     * @param nomineeUsername The username of the nominee.
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> SendStoreOwnerNomination(String storeID, String nominatorUsername, String nomineeUsername, String token) {
        SystemLogger.info("[START] User: " + nominatorUsername + " is trying to make " + nomineeUsername + " a store owner");
        if(isValidToken(token,nominatorUsername)) {
            if (nominatorUsername.equals(nomineeUsername)) {
                SystemLogger.error("[ERROR] User: " + nomineeUsername + " is trying to make himself a store manager");
                return Response.error("You can't nominate yourself.", null);
            }
            if (!userExists(nomineeUsername)) {
                SystemLogger.error("[ERROR] User: " + nomineeUsername + " does not exist");
                return Response.error("User: " + nomineeUsername + " does not exist", null);
            }
            Message ownerNominationMessage = storeService.makeNominateOwnerMessage(storeID, nominatorUsername, nomineeUsername).getData();
            if (ownerNominationMessage != null) {
                return userFacade.sendMessageToUser(nomineeUsername, ownerNominationMessage);
            }
        }
        SystemLogger.error("[ERROR] User: " + nominatorUsername + " tried to make " + nomineeUsername + " a store owner but the token was invalid");
        return Response.error("Invalid token",null);
    }

    /**
     * This method sends an owner nomination request to a subscriber.
     * @param storeID The store ID of the store.
     * @param nominatorUsername The username of the nominator.
     * @param nomineeUsername The username of the nominee.
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> SendStoreManagerNomination(String storeID, String nominatorUsername, String nomineeUsername, List<String> permissions, String token) {
        SystemLogger.info("[START] User: " + nominatorUsername + " is trying to make " + nomineeUsername + " a store manager");
        if(isValidToken(token,nominatorUsername)) {
            if (nominatorUsername.equals(nomineeUsername)) {
                SystemLogger.error("[ERROR] User: " + nomineeUsername + " is trying to make himself a store manager");
                return Response.error("User: " + nomineeUsername + " is trying to make himself a store manager", null);
            }
            if (!userExists(nomineeUsername)) {
                SystemLogger.error("[ERROR] User: " + nomineeUsername + " does not exist");
                return Response.error("User: " + nomineeUsername + " does not exist", null);
            }
            Message managerNominationMessage = storeService.makeNominateManagerMessage(storeID,nominatorUsername, nomineeUsername, permissions).getData();
            if (managerNominationMessage != null) {
                return userFacade.sendMessageToUser(nomineeUsername, managerNominationMessage);
            }
        }
        SystemLogger.error("[ERROR] User: " + nominatorUsername + " tried to make " + nomineeUsername + " a store manager but the token was invalid");
        return Response.error("Invalid token",null);
    }

    /**
     * This method waives a subscriber's ownership of a store.
     * @param storeID The store ID of the store.
     * @param username The username of the subscriber.
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> waiveOwnership(String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to waive his ownership of the store");
        if(isValidToken(token,username)) {
            Set<String> usernames = storeService.waiveOwnership(storeID, username).getData();
            for (String subscriberUsername : usernames) {
                userFacade.sendMessageToUser(subscriberUsername, new NormalMessage("The owner of the store has self-waived and have been removed from the store"));
            }
            return Response.success("The owner of the store has self-waived and all of its' nominess have been removed as well.", null);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to waive his ownership of the store but the token was invalid");
        return Response.error("Invalid token",null);
    }

    /**
     * This method sends a response for an owner nomination request.
     * @param username The username of the subscriber.
     * @param answer The answer for the nomination request (true for accept, false for decline).
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> ownerNominationResponse(String username, boolean answer, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to respond to a store owner nomination");
        if(isValidToken(token,username)) {
            nominateOwnerMessage nominationMessage = (nominateOwnerMessage)userFacade.ownerNominationResponse(username, answer).getData();
            if (nominationMessage != null && answer) {
                return storeService.nominateOwner(nominationMessage.getStoreID(), username, nominationMessage.getNominatorUsername());
            }
            else if (nominationMessage != null && !answer && !nominationMessage.isSubscribed()) {
                return storeService.systemRemoveStoreSubscription(nominationMessage.getStoreID(), username);
            }
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to respond to a store owner nomination but the token was invalid");
        return Response.error("Invalid token",null);
    }

    /**
     * This method sends a response for a manager nomination request.
     * @param username The username of the subscriber.
     * @param answer The answer for the nomination request (true for accept, false for decline).
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> managerNominationResponse(String username, boolean answer, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to respond to a store manager nomination");
        if(isValidToken(token,username)) {
            nominateManagerMessage nominationMessage = (nominateManagerMessage)userFacade.managerNominationResponse(username, answer).getData();
            if (nominationMessage != null && answer) {
                return storeService.nominateManager(nominationMessage.getStoreID(), username, nominationMessage.getPermissions(), nominationMessage.getNominatorUsername());
            }
            else if (nominationMessage != null && !answer && !nominationMessage.isSubscribed()) {
                return storeService.systemRemoveStoreSubscription(nominationMessage.getStoreID(), username);
            }
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to respond to a store manager nomination but the token was invalid");
        return Response.error("Invalid token",null);
    }


    /**
     * This method sends a response for a message.
     * @param username The username of the subscriber.
     * @param answer The answer for the nomination request (true for accept, false for decline).
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> messageResponse(String username, boolean answer, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to respond to a message");
        if(isValidToken(token,username)) {
            return userFacade.messageResponse(username, answer);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to respond to a message but the token was invalid");
        return Response.error("Invalid token",null);
    }

    // Method to prompt the subscriber to accept the subscription
    private boolean promptSubscription(String subscriberUsername, String targetUsername) {
        // Implement the prompt logic here
        // For example, display a prompt to the subscriber
        // and wait for user input to accept or decline the subscription
        return true; // Assume subscription is accepted
    }

    /**
     * This method registers a new subscriber to the system.
     * @param username The username of the subscriber.
     * @param password The password of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public synchronized Response<String> register(String username, String password) {
        SystemLogger.info("[START] User: " + username + " is trying to register");
        return userFacade.register(username, password);
    }

    /**
     * This method sends a close store notification to the store subscribers & personnel.
     * @param subscriberNames The usernames of the subscribers.
     * @param storeID The store ID of the store.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return userFacade.sendCloseStoreNotification(subscriberNames, storeID);
    }

    public boolean userExists(String subscriberUsername) {
        return userFacade.userExist(subscriberUsername);
    }

    /**
     * This method requests the employees status of a store.
     * @param storeID The store ID of the store.
     * @param username The username of the requesting subscriber.
     * @param token The token of the requesting subscriber.
     * @return If successful, returns a success message & map of {username, role}. <br> If not, returns an error message.
     */
    public Response<Map<String, String>> requestEmployeesStatus(String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to request the employees status of the store");
        if (isValidToken(token, username)) {
            if (storeService.isStoreOwner(storeID, username) || storeService.isStoreCreator(storeID, username)) {
                return storeService.requestEmployeesStatus(storeID);
            }
            SystemLogger.error("[ERROR] User: " + username + " tried to request the employees status of the store but is not the store owner");
            return Response.error("The user trying to do this action is not the store owner.", null);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to request the employees status of the store but the token was invalid");
        return Response.error("invalid token", null);
    }

    /**
     * This method requests the managers permissions of a store.
     * @param storeID The store ID of the store.
     * @param username The username of the requesting subscriber.
     * @param token The token of the requesting subscriber.
     * @return If successful, returns a success message & map of {username, permissions}. <br> If not, returns an error message.
     */
    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to request the managers permissions of the store");
        if (isValidToken(token, username)) {
            if (storeService.isStoreOwner(storeID, username) || storeService.isStoreCreator(storeID, username)) {
                return storeService.requestManagersPermissions(storeID);
            }
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to request the managers permissions of the store but the token was invalid");
        return Response.error("invalid token", null);
    }

    /**
     * This method adds a product to the shopping cart.
     * @param storeID The store ID of the store.
     * @param productID The product ID of the product.
     * @param username The username of the subscriber.
     * @param token The token of the subscriber.
     * @param quantity The quantity of the product.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> addProductToShoppingCart(String storeID, String productID, String username, String token, int quantity) {
        SystemLogger.info("[START] User: " + username + " is trying to add a product to the shopping cart");
        if (isValidToken(token, username)) {
            if (storeService.isStoreOwner(storeID, username) || storeService.isStoreCreator(storeID, username)) {
                return userFacade.addProductToShoppingCart(storeID, productID, username, quantity);
            }
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to add a product to the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }

    /**
     * This method removes a product from the shopping cart.
     * @param storeID The store ID of the store.
     * @param productID The product ID of the product.
     * @param username The username of the subscriber.
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> removeProductFromShoppingCart(String storeID, String productID, String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to remove a product from the shopping cart");
        if (isValidToken(token, username)) {
            return userFacade.removeProductFromShoppingCart(username,storeID, productID);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to remove a product from the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }

    /**
     * This method updates a product in the shopping cart.
     * @param storeID The store ID of the store.
     * @param productID The product ID of the product.
     * @param username The username of the subscriber.
     * @param token The token of the subscriber.
     * @param quantity The quantity of the product.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> updateProductInShoppingCart(String storeID, String productID, String username, String token, int quantity) {
        SystemLogger.info("[START] User: " + username + " is trying to update a product in the shopping cart");
        if (isValidToken(token, username)) {
            return userFacade.updateProductInShoppingCart(storeID, productID, username, quantity);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to update a product in the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }

    /**
     * This method gets the shopping cart contents.
     * @param username The username of the subscriber.
     * @param token The token of the subscriber.
     * @return If successful, returns a success message & map of {storeID, {productID, quantity}}. <br> If not, returns an error message.
     */
    public Response<String> getShoppingCartContents(String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to get the shopping cart contents");
        if (isValidToken(token, username)) {
            return userFacade.getShoppingCartContents(username);
        }
        SystemLogger.error("[ERROR] User: " + username + " tried to get the shopping cart contents but the token was invalid");
        return Response.error("invalid token", null);
    }

    /**
     * This method purchases the shopping cart.
     * @param userName The username of the subscriber.
     * @param token The token of the subscriber.
     * @return If successful, returns a success message. <br> If not, returns an error message.
     */
    public Response<String> purchaseShoppingCart(String userName, String token) {
        SystemLogger.info("[START] User: " + userName + " is trying to purchase the shopping cart");
        if (isValidToken(token, userName)) {
            return userFacade.purchaseShoppingCart(userName);
        }
        SystemLogger.error("[ERROR] User: " + userName + " tried to purchase the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }

    public boolean isValidToken(String token, String currentUsername) {
        return userFacade.isValidToken(token, currentUsername);
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }


}
