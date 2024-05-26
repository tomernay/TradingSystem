package Domain.Repo;

import Domain.Externals.Security.Security;
import Domain.Users.Subscriber.Cart.ShoppingCart;
import Utilities.Messages.Message;

import Utilities.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {

    private Map<String, Subscriber> subscribers = new HashMap<>();
    private Map<String, User> guests = new HashMap<>();
    private int userIDS = 0;

    public Response<String> loginAsGuest() {
        String username = "Guest" + userIDS;
        User user = new User(username);
        userIDS++;
        guests.put(username, user);
        return Response.success("You signed in as a GUEST", username);
    }

    public Response<String> logoutAsGuest(String username) {
        if(guests.get(username) == null) {
            return Response.error("User is already logged out", null);
        }
        if(!guests.get(username).logoutAsGuest()){
            guests.remove(username);
            return Response.success("You signed out as a GUEST", null);
        }
        return Response.error("Error - can't signed out as a GUEST", null);
    }

    public Response<String> loginAsSubscriber(String username, String password) {
        if (isUserExist(username)) {
            Subscriber subscriber = getUser(username);
            if (subscriber.getPassword().equals(password)) {
                getUser(username).generateToken();
                return Response.success("Logged in successfully", null);
            } else {
                return Response.error("Incorrect password", null);
            }
        } else {
            return Response.error("User does not exist", null);
        }
    }

    public Response<String> logoutAsSubscriber(Subscriber subscriber) {
        if(!subscribers.containsKey(subscriber.getUsername())) {
            return Response.error("User is already logged out", null);
        }
        subscribers.remove(subscriber.getUsername());
        return Response.success("You signed out as a SUBSCRIBER", null);
    }

    public void addGuest(User user) {
        guests.put(user.getUsername(), user);
    }

    public User getGuest(ShoppingCart shoppingCart) {
        return guests.get(shoppingCart);
    }

    public Response<String> makeStoreOwner(String subscriberUsername, Message message) {
        return subscribers.get(subscriberUsername).makeStoreOwner(message);
    }

    public Response<String> makeStoreManager(String subscriberUsername, Message message) {
        return subscribers.get(subscriberUsername).makeStoreManager(message);
    }

    public Response<Message> messageResponse(String subscriberUsername, boolean answer) {
        return subscribers.get(subscriberUsername).messageResponse(answer);
    }

    public void sendMessageToUser(String user,Message message){
        subscribers.get(user).addMessage(message);
    }

    public boolean isUserExist(String username) {
        return subscribers.containsKey(username);
    }

    public void addUser(Subscriber user) {
        subscribers.put(user.getUsername(), user);
    }

    public Subscriber getUser(String username) {
        return subscribers.get(username);
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        for (String subscriberName : subscriberNames) {
            subscribers.get(subscriberName).addMessage(new NormalMessage("Store " + storeID + " has been closed"));
        }
        SystemLogger.info("Store " + storeID + " has been closed. Notifications sent to all related subscribers.");
        return Response.success("Notification sent successfully", null);
    }

    public Response<String> register(String username, String password) {
        if(!isUsernameValid(username) ) {
            return Response.error("Username does not meet the requirements", null);
        }
        else if(!isValidPassword(password)) {
            return Response.error("Password does not meet the requirements", null);
        }
        else if(isUserExist(username)) {
            return  Response.error("User already exists", null);
        }
        else {
            Subscriber subscriber = new Subscriber(username,password);
            addUser(subscriber);
            return Response.success("User registered successfully", username);
        }
    }


    public boolean isUsernameValid(String username) {
        // Check if the username is null or empty
        if (username == null || username.isEmpty()) {
            return false;
        }
        // Check if the username is less than 3 characters long
        if (username.length() < 3) {
            return false;
        }
        // Check if the username contains only alphanumeric characters
        if (!username.matches("[A-Za-z0-9]*")) {
            return false;
        }
        return true;
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



    public Response<String> addProductToShoppingCart(String storeID,String productID,String userName,int quantity) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).addProductToShoppingCart(storeID, productID, quantity);
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).addProductToShoppingCart(storeID, productID, quantity);
        }
        return Response.error("User does not exist", null);
    }


    public Response<String> removeProductFromShoppingCart(String userName, String storeID, String productID) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).removeProductFromShoppingCart(storeID, productID);
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).removeProductFromShoppingCart(storeID, productID);
        }
        return Response.error("User does not exist", null);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, String userName, int quantity) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).updateProductInShoppingCart(storeID, productID, quantity);
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).updateProductInShoppingCart(storeID, productID, quantity);
        }
        return Response.error("User does not exist", null);
    }



    public Response<Map<String, Map<String, Integer>>> getShoppingCartContents(String userName) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).getShoppingCartContents();
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).getShoppingCartContents();
        }
        return Response.error("User does not exist", null);
    }

    public Response<Message> ownerNominationResponse(String currentUsername, boolean answer) {
        return subscribers.get(currentUsername).ownerNominationResponse(answer);
    }

    public Response<Message> managerNominationResponse(String currentUsername, boolean answer) {
        return subscribers.get(currentUsername).managerNominationResponse(answer);
    }

    public boolean isValidToken(String token, String currentUsername) {
        return Security.isValidJWT(token,currentUsername);
    }
}
