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


    public synchronized Response<List<String>> loginAsGuest() {
        return userFacade.loginAsGuest();
    }

    //function as a Guest - exit from the website
    public synchronized Response<String> logoutAsGuest(String username) {
        return userFacade.logoutAsGuest(username);
    }

    public synchronized Response<String> loginAsSubscriber(String username, String password) {
        SystemLogger.info("[START] User: " + username + " is trying to login");
        return userFacade.loginAsSubscriber(username, password);
    }

    public synchronized Response<String> logoutAsSubscriber(String username) {
        SystemLogger.info("[START] User: " + username + " is trying to logout");
        return userFacade.logoutAsSubscriber(username);
    }

    public Response<String> SendStoreOwnerNomination(String storeID, String currentUsername, String subscriberUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to make " + subscriberUsername + " a store owner");
        if(isValidToken(token,currentUsername)) {
            if (currentUsername.equals(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " is trying to make himself a store manager");
                return Response.error("You can't nominate yourself.", null);
            }
            if (!userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            Message ownerNominationMessage = storeService.makeNominateOwnerMessage(storeID, currentUsername, subscriberUsername).getData();
            if (ownerNominationMessage != null) {
                return userFacade.sendMessageToUser(subscriberUsername, ownerNominationMessage);
            }
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to make " + subscriberUsername + " a store owner but the token was invalid");
        return Response.error("Invalid token",null);
    }

    // Method to add a store manager subscription
    public Response<String> SendStoreManagerNomination(String storeID, String currentUsername, String subscriberUsername, List<String> permissions, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to make " + subscriberUsername + " a store manager");
        if(isValidToken(token,currentUsername)) {
            if (currentUsername.equals(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " is trying to make himself a store manager");
                return Response.error("User: " + subscriberUsername + " is trying to make himself a store manager", null);
            }
            if (!userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            Message managerNominationMessage = storeService.makeNominateManagerMessage(storeID,currentUsername, subscriberUsername, permissions).getData();
            if (managerNominationMessage != null) {
                return userFacade.sendMessageToUser(subscriberUsername, managerNominationMessage);
            }
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to make " + subscriberUsername + " a store manager but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> waiveOwnership(String storeID, String currentUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to waive his ownership of the store");
        if(isValidToken(token,currentUsername)) {
            Set<String> usernames = storeService.waiveOwnership(storeID, currentUsername).getData();
            for (String username : usernames) {
                userFacade.sendMessageToUser(username, new NormalMessage("The owner of the store has self-waived and have been removed from the store"));
            }
            return Response.success("The owner of the store has self-waived and all of its' nominess have been removed as well.", null);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to waive his ownership of the store but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> ownerNominationResponse(String currentUsername, boolean answer, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to respond to a store owner nomination");
        if(isValidToken(token,currentUsername)) {
            nominateOwnerMessage nominationMessage = (nominateOwnerMessage)userFacade.ownerNominationResponse(currentUsername, answer).getData();
            if (nominationMessage != null && answer) {
                return storeService.nominateOwner(nominationMessage.getStoreID(), currentUsername, nominationMessage.getNominatorUsername());
            }
            else if (nominationMessage != null && !answer && !nominationMessage.isSubscribed()) {
                return storeService.systemRemoveStoreSubscription(nominationMessage.getStoreID(), currentUsername);
            }
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to respond to a store owner nomination but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> managerNominationResponse(String currentUsername, boolean answer, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to respond to a store manager nomination");
        if(isValidToken(token,currentUsername)) {
            nominateManagerMessage nominationMessage = (nominateManagerMessage)userFacade.managerNominationResponse(currentUsername, answer).getData();
            if (nominationMessage != null && answer) {
                return storeService.nominateManager(nominationMessage.getStoreID(), currentUsername, nominationMessage.getPermissions(), nominationMessage.getNominatorUsername());
            }
            else if (nominationMessage != null && !answer && !nominationMessage.isSubscribed()) {
                return storeService.systemRemoveStoreSubscription(nominationMessage.getStoreID(), currentUsername);
            }
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to respond to a store manager nomination but the token was invalid");
        return Response.error("Invalid token",null);
    }



    public Response<String> messageResponse(String currentUsername, boolean answer, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to respond to a message");
        if(isValidToken(token,currentUsername)) {
            return userFacade.messageResponse(currentUsername, answer);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to respond to a message but the token was invalid");
        return Response.error("Invalid token",null);
    }

    // Method to prompt the subscriber to accept the subscription
    private boolean promptSubscription(String subscriberUsername, String targetUsername) {
        // Implement the prompt logic here
        // For example, display a prompt to the subscriber
        // and wait for user input to accept or decline the subscription
        return true; // Assume subscription is accepted
    }

    //register a new user
    public synchronized Response<String> register(String username, String password) {
        SystemLogger.info("[START] User: " + username + " is trying to register");
        return userFacade.register(username, password);
    }


    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return userFacade.sendCloseStoreNotification(subscriberNames, storeID);
    }

    public boolean userExists(String subscriberUsername) {
        return userFacade.userExist(subscriberUsername);
    }

    public Response<Map<String, String>> requestEmployeesStatus(String storeID, String userName, String token) {
        SystemLogger.info("[START] User: " + userName + " is trying to request the employees status of the store");
        if (isValidToken(token, userName)) {
            if (storeService.isStoreOwner(storeID, userName) || storeService.isStoreCreator(storeID, userName)) {
                return storeService.requestEmployeesStatus(storeID);
            }
            SystemLogger.error("[ERROR] User: " + userName + " tried to request the employees status of the store but is not the store owner");
            return Response.error("The user trying to do this action is not the store owner.", null);
        }
        SystemLogger.error("[ERROR] User: " + userName + " tried to request the employees status of the store but the token was invalid");
        return Response.error("invalid token", null);
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID, String userName, String token) {
        SystemLogger.info("[START] User: " + userName + " is trying to request the managers permissions of the store");
        if (isValidToken(token, userName)) {
            if (storeService.isStoreOwner(storeID, userName) || storeService.isStoreCreator(storeID, userName)) {
                return storeService.requestManagersPermissions(storeID);
            }
        }
        SystemLogger.error("[ERROR] User: " + userName + " tried to request the managers permissions of the store but the token was invalid");
        return Response.error("invalid token", null);
    }

    public Response<String> addProductToShoppingCart(String storeID, String productID, String userName, String token, int quantity) {
        SystemLogger.info("[START] User: " + userName + " is trying to add a product to the shopping cart");
        if (isValidToken(token, userName)) {
            if (storeService.isStoreOwner(storeID, userName) || storeService.isStoreCreator(storeID, userName)) {
                return userFacade.addProductToShoppingCart(storeID, productID, userName, quantity);
            }
        }
        SystemLogger.error("[ERROR] User: " + userName + " tried to add a product to the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }

    public Response<String> removeProductFromShoppingCart(String storeID, String productID, String userName, String token) {
        SystemLogger.info("[START] User: " + userName + " is trying to remove a product from the shopping cart");
        if (isValidToken(token, userName)) {
            return userFacade.removeProductFromShoppingCart(userName,storeID, productID);
        }
        SystemLogger.error("[ERROR] User: " + userName + " tried to remove a product from the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, String userName, String token, int quantity) {
        SystemLogger.info("[START] User: " + userName + " is trying to update a product in the shopping cart");
        if (isValidToken(token, userName)) {
            return userFacade.updateProductInShoppingCart(storeID, productID, userName, quantity);
        }
        SystemLogger.error("[ERROR] User: " + userName + " tried to update a product in the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }

    public Response<String> getShoppingCartContents(String userName, String token) {
        SystemLogger.info("[START] User: " + userName + " is trying to get the shopping cart contents");
        if (isValidToken(token, userName)) {
            return userFacade.getShoppingCartContents(userName);
        }
        SystemLogger.error("[ERROR] User: " + userName + " tried to get the shopping cart contents but the token was invalid");
        return Response.error("invalid token", null);
    }

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
