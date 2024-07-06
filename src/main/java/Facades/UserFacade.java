package Facades;

import Domain.Externals.Security.PasswordEncoderUtil;
import Domain.Externals.Security.TokenHandler;
import Domain.Repo.UserRepository;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Presentation.application.View.UtilitiesView.Broadcaster;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import Utilities.SystemLogger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class UserFacade {
    private final UserRepository userRepository;

    public UserFacade() {
        userRepository = new UserRepository();
    }


    public Response<List<String>> loginAsGuest(){
        String username = "Guest" + userRepository.getIdCounter();
        User user = new User(username);
        userRepository.increaseIdCounter();
        String token = user.generateToken();
        Boolean answer = userRepository.addGuest(user);
        if (answer) {
            SystemLogger.info("[SUCCESS] User " + username + " logged in successfully");
            return Response.success("You signed in as a GUEST", List.of(username, token));
        }
        SystemLogger.error("[ERROR] User " + username + " is already logged in");
        return Response.error("User is already logged in", null);
    }

    public Response<String> logoutAsGuest(String username) {
        User guest = userRepository.getGuest(username);
        if(guest == null) {
            SystemLogger.error("[ERROR] User " + username + " is already logged out");
            return Response.error("User is already logged out", null);
        }
        userRepository.removeGuest(username);
        SystemLogger.info("[SUCCESS] User " + username + " logged out successfully");
        return Response.success("You signed out as a GUEST", null);
    }

    public Response<String> loginAsSubscriber(String username, String password){
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            if (PasswordEncoderUtil.matches(password,subscriber.getPassword())) {
                String token = subscriber.generateToken();
                Boolean answer = userRepository.addLoggedIn(username);
                if (!answer) {
                    SystemLogger.error("[ERROR] User " + username + " is already logged in");
                    return Response.error("User is already logged in", null);
                }
                SystemLogger.info("[SUCCESS] User " + username + " logged in successfully");
                return Response.success("Logged in successfully", token);
            }
            SystemLogger.error("[ERROR] Incorrect password for user " + username);
            return Response.error("Incorrect password", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> logoutAsSubscriber(String username){
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber == null) {
            SystemLogger.error("[ERROR] User " + username + " does not exist");
            return Response.error("User does not exist", null);
        }
        Boolean answer = userRepository.removeLoggedIn(username);
        if (answer) {
            subscriber.resetToken();
            SystemLogger.info("[SUCCESS] User " + username + " logged out successfully");
            return Response.success("You signed out as a SUBSCRIBER", null);
        }
        SystemLogger.error("[ERROR] User " + username + " is already logged out");
        return Response.error("User is already logged out", null);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeName) {
        for (String subscriberName : subscriberNames) {
            Broadcaster.broadcast("Store " + storeName + " has been closed",subscriberName);
            Subscriber subscriber = userRepository.getSubscriber(subscriberName);
            subscriber.addMessage(new NormalMessage("Store " + storeName + " has been closed"));
        }
        SystemLogger.info("[SUCCESS] Store " + storeName + " has been closed. Notifications sent to all related subscribers.");
        return Response.success("Notification sent successfully", null);
    }
    public Response<String> sendReopenStoreNotification(List<String> subscriberNames, String storeName) {
        for (String subscriberName : subscriberNames) {
            Broadcaster.broadcast("Store " + storeName + " has been reopen",subscriberName);
            Subscriber subscriber = userRepository.getSubscriber(subscriberName);
            subscriber.addMessage(new NormalMessage("Store " + storeName + " has been re-opened"));
        }
        SystemLogger.info("[SUCCESS] Store " + storeName + " has been reopened. Notifications sent to all related subscribers.");
        return Response.success("Notification sent successfully", null);
    }

    public Response<String> register(String username, String password) {
        if(!isUsernameValid(username)) {
            SystemLogger.error("[ERROR] Username does not meet the requirements");
            return Response.error("Username does not meet the requirements", null);
        }
        else if(!isValidPassword(password)) {
            SystemLogger.error("[ERROR] Password does not meet the requirements");
            return Response.error("Password does not meet the requirements", null);
        }
        else {
            if (userRepository.isUserExist(username)) {
                SystemLogger.error("[ERROR] User " + username + " is already registered");
                return Response.error("User is already registered", null);
            }
            Subscriber subscriber = new Subscriber(username, PasswordEncoderUtil.encode(password));
            Boolean answer = userRepository.addSubscriber(subscriber);
            if (!answer) {
                SystemLogger.error("[ERROR] User " + username + " is already registered");
                return Response.error("User is already registered", null);
            }
            SystemLogger.info("[SUCCESS] User " + username + " registered successfully");
            return Response.success("User registered successfully", username);
        }
    }

    public Response<String> addProductToShoppingCart(Integer storeID,Integer productID, Integer quantity,String username){
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.addProductToShoppingCart(storeID, productID, quantity);
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.addProductToShoppingCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> removeProductFromShoppingCart(String username,Integer storeID, Integer productID) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.removeProductFromShoppingCart(storeID, productID);
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.removeProductFromShoppingCart(storeID, productID);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> updateProductInShoppingCart(Integer storeID, Integer productID, String username, Integer quantity) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.updateProductInShoppingCart(storeID, productID, quantity);
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.updateProductInShoppingCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Message> ownerNominationResponse(Integer messageID, String currentUsername, Boolean answer) {
        Subscriber subscriber = userRepository.getSubscriber(currentUsername);
        if (subscriber == null) {
            SystemLogger.error("[ERROR] User " + currentUsername + " does not exist");
            return Response.error("User does not exist", null);
        }
        Response<Message> response = subscriber.ownerNominationResponse(messageID, answer);
        if (response.isSuccess()) {
            sendMessageToUser(((nominateOwnerMessage) response.getData()).getNominator(), new NormalMessage("Your request to nominate " + currentUsername + " as a store owner has been " + (answer ? "accepted" : "declined")));
            SystemLogger.info("[SUCCESS] message responded successfully");
            return response;
        }
        return Response.error(response.getMessage(), null);
    }

    public Response<Message> managerNominationResponse(Integer messageID, String currentUsername, Boolean answer) {
        Subscriber subscriber = userRepository.getSubscriber(currentUsername);
        if (subscriber == null) {
            SystemLogger.error("[ERROR] User " + currentUsername + " does not exist");
            return Response.error("User does not exist", null);
        }
        Response<Message> response = subscriber.managerNominationResponse(messageID, answer);
        if (response.isSuccess()) {
            sendMessageToUser(((nominateManagerMessage) response.getData()).getNominatorUsername(), new NormalMessage("Your request to nominate " + currentUsername + " as a store manager has been " + (answer ? "accepted" : "declined")));
            SystemLogger.info("[SUCCESS] message responded successfully");
            return response;
        }
        return Response.error(response.getMessage(), null);
    }

    public boolean userExist(String subscriberUsername) {
        return userRepository.isUserExist(subscriberUsername);
    }

    public Response<Map<Integer, Map<Integer, Integer>>> getShoppingCartContents(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.getShoppingCartContents();
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.getShoppingCartContents();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Integer> sendMessageToUser(String username, Message message) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        return subscriber.addMessage(message);
    }

    public boolean isValidToken(String token, String currentUsername) {
        return TokenHandler.isValidJWT(token,currentUsername);
    }

    public void addCreatorRole(String creatorUsername, Integer storeID) {
        Subscriber subscriber = userRepository.getSubscriber(creatorUsername);
        if (subscriber == null) {
            SystemLogger.error("[ERROR] User " + creatorUsername + " does not exist");
            return;
        }
        subscriber.addCreatorRole(storeID);
    }

    public Response<Map<Integer, String>> getStoresRole(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber == null) {
            SystemLogger.error("[ERROR] User " + username + " does not exist");
            return Response.error("User does not exist", null);
        }
        return subscriber.getStoresRole();
    }

    public void removeStoreRole(String subscriberUsername, Integer storeID) {
        Subscriber subscriber = userRepository.getSubscriber(subscriberUsername);
        if (subscriber == null) {
            SystemLogger.error("[ERROR] User " + subscriberUsername + " does not exist");
            return;
        }
        subscriber.removeStoreRole(storeID);
    }

    public Response<String> isOwner(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if(subscriber == null){
            return Response.error("User does not exist",null);
        }
        return subscriber.isOwner();
    }

    public Response<String> isManager(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if(subscriber == null){
            return Response.error("User does not exist",null);
        }
        return subscriber.isManager();
    }

    public Response<String> isCreator(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if(subscriber == null){
            return Response.error("User does not exist",null);
        }
        return subscriber.isCreator();
    }

    public Response<String> changePassword(String username, String oldPassword, String newPassword){
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            if (PasswordEncoderUtil.matches(oldPassword, subscriber.getPassword())) {
                if (isValidPassword(newPassword)) {
                    subscriber.setPassword(newPassword);
                    SystemLogger.info("[SUCCESS] Password for user " + username + " changed successfully");
                    return Response.success("Password changed successfully", null);
                }
                SystemLogger.error("[ERROR] New password does not meet the requirements");
                return Response.error("New password does not meet the requirements", null);
            }
            SystemLogger.error("[ERROR] Old password does not match the current password");
            return Response.error("Old password does not match the current password", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> changeUsername(String username, String newUsername){
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            if (isUsernameValid(newUsername)) {
                if (!userRepository.isUserExist(username)) {
                    userRepository.removeSubscriber(username);
                    subscriber.setUsername(newUsername);
                    String token = subscriber.generateToken();
                    userRepository.addSubscriber(subscriber);
                    SystemLogger.info("[SUCCESS] Username for user " + username + " changed successfully to " + newUsername);
                    return Response.success("Username changed successfully", token);
                }
                SystemLogger.error("[ERROR] New username is already taken");
                return Response.error("New username is already taken", null);
            }
            SystemLogger.error("[ERROR] New username does not meet the requirements");
            return Response.error("New username does not meet the requirements", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public boolean isUsernameValid(String username) {
        // Check if the username is null or empty
        if (username == null || username.isEmpty()) {
            return false;
        }
        // Check if the username contains only alphanumeric characters
        return username.matches("[A-Za-z0-9]*");
    }

    public static boolean isValidPassword(String password) {
        // Define the password criteria
        int minLength = 8;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        // Check the length of the password
        if (password.length() < minLength) {
            return false;
        }

        // Check each character of the password
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (isSpecialCharacter(c)) {
                hasSpecialChar = true;
            }
        }

        // Return true if all criteria are met
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    // Helper method to check if a character is a special character
    private static boolean isSpecialCharacter(char c) {
        String specialCharacters = "!@#$%^&*()-+";
        return specialCharacters.indexOf(c) >= 0;
    }

    public Response<String> ResetCartAfterPurchase(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.resetShoppingCart();
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.resetShoppingCart();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> lockFlagShoppingCart(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.lockFlagShoppingCart();
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.lockFlagShoppingCart();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> unlockFlagShoppingCart(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.unlockFlagShoppingCart();
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.unlockFlagShoppingCart();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Boolean> isFlagLock(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " is locked");
            return Response.success("Shopping cart is locked", subscriber.isFlagLock());
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " is locked");
            return Response.success("Shopping cart is locked", guest.isFlagLock());
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> clearCart(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.clearCart();
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.clearCart();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> updateProductQuantityInCart(Integer storeId, Integer productId, Integer quantity, String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.updateProductQuantityInCart(storeId, productId, quantity);
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.updateProductQuantityInCart(storeId, productId, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public CompletableFuture<String> startPurchaseTimer(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.startPurchaseTimer();
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.startPurchaseTimer();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return null;
    }

    public void interruptPurchaseTimer(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            subscriber.interruptPurchaseTimer();
        }
        else {
            User guest = userRepository.getGuest(username);
            if (guest != null) {
                guest.interruptPurchaseTimer();
            }
            else {
                SystemLogger.error("[ERROR] User " + username + " does not exist");
            }
        }
    }


    public boolean isInPurchaseProcess(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.isInPurchaseProcess();
        }
        User guest = userRepository.getGuest(username);
        if (guest != null) {
            return guest.isInPurchaseProcess();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return false;
    }

    public Response<String> removeMessage(String username, Integer messageID) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.removeMessage(messageID);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Integer> getUnreadMessagesCount(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.getUnreadMessagesCount();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Set<String>> getAllSubscribersUsernames() {
        return userRepository.getAllSubscribersUsernames();
    }

    public Response<List<Message>> getMessages(String username) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return Response.success("Messages retrieved successfully", subscriber.getMessages());
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> sendMessage(String username, String message) {
        Subscriber subscriber = userRepository.getSubscriber(username);
        if (subscriber != null) {
            return subscriber.sendMessage(message);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }
}