package Domain.Repo;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.Subscriber.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {

    private Map<String, Subscriber> users = new HashMap<>();
    private Map<String, User> guests = new HashMap<>();
    private int userIDS = 0;
    private Map<String, User> usersMap = new HashMap<>(); // <username, User>

    public Response<String> loginAsGuest() {
        User user = new User("Guest" + userIDS++);
        guests.put(user.getUsername(), user);
        return Response.success("You signed in as a GUEST", null);
    }

    public Response<String> logoutAsGuest(String username) {
        if(guests.get(username).logoutAsGuest()){
            guests.remove(username);
            return Response.success("You signed out as a GUEST", null);
        }
        return Response.error("Error - can't signed out as a GUEST", null);
    }

    public Response<String> loginAsSubscriber(Subscriber subscriberToLogin) {
        if (isUserExist(subscriberToLogin.getUsername())) {
            Subscriber subscriber = getUser(subscriberToLogin.getUsername());
            if (subscriber.getPassword().equals(subscriberToLogin.getPassword())) {
                // Log in the user and set the Subscriber object
                users.put(subscriber.getUsername(), subscriber);
                return Response.success("Logged in successfully", null);
            } else {
                return Response.error("Incorrect password", null);
            }
        } else {
            return Response.error("User does not exist", null);
        }
    }

    public Response<String> logoutAsSubscriber(Subscriber subscriber) {
        if(!users.containsKey(subscriber.getUsername())) {
            return Response.error("User is already logged out", null);
        }
        users.remove(subscriber.getUsername());
        return Response.success("You signed out as a SUBSCRIBER", null);
    }

    public void addGuest(User user) {
        guests.put(user.getUsername(), user);
    }

    public User getGuest(ShoppingCart shoppingCart) {
        return guests.get(shoppingCart);
    }

    public Response<String> makeStoreOwner(String subscriberUsername, Message message) {
        return users.get(subscriberUsername).makeStoreOwner(message);
    }

    public Response<String> makeStoreManager(String subscriberUsername, Message message) {
        return users.get(subscriberUsername).makeStoreManager(message);
    }

    public Response<Message> messageResponse(String subscriberUsername, boolean answer) {
        return users.get(subscriberUsername).messageResponse(answer);
    }

    public void sendMessageToUser(String user,Message message){
        users.get(user).addMessage(message);
    }

    public boolean isUserExist(String username) {
        return users.containsKey(username);
    }

    public void addUser(Subscriber user) {
        users.put(user.getUsername(), user);
    }

    public Subscriber getUser(String username) {
        return users.get(username);
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        for (String subscriberName : subscriberNames) {
            users.get(subscriberName).addMessage(new NormalMessage("Store " + storeID + " has been closed"));
        }
        SystemLogger.info("Store " + storeID + " has been closed. Notifications sent to all related subscribers.");
        return Response.success("Notification sent successfully", null);
    }

    public Response<String> register(String username, String password) {
        if(!isUsernameValid(username)) {
            return Response.error("Username does not meet the requirements", null);
        }
        else if(isUserExist(username)) {
            return  Response.error("User already exists", null);
        }
//        else if (!isPasswordValid(password)) {
//            return Response.error("Password does not meet the requirements", null);
//        }
        else {
            Subscriber subscriber = new Subscriber(username,password);
            addUser(subscriber);
            return Response.success("User registered successfully", null);
        }
    }

//    public boolean isPasswordValid(String password) {
//        if (password.length() < 3) {
//            return false;
//        }
//        if (!password.matches(".*[A-Z].*")) {
//            return false;
//        }
//        if (!password.matches(".*[a-z].*")) {
//            return false;
//        }
//        if (!password.matches(".*\\d.*")) {
//            return false;
//        }
//        return true;
//    }

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



    public Response<String> addProductToShoppingCart(String storeID,String productID,String userName,int quantity) {
        return usersMap.get(userName).addProductToShoppingCart(storeID, productID,quantity);
    }


    public Response<String> removeProductFromShoppingCart(String userName, String storeID, String productID) {
        return usersMap.get(userName).removeProductFromShoppingCart(storeID,productID);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, String userName, int quantity) {
        return usersMap.get(userName).updateProductInShoppingCart(storeID, productID, quantity);
    }



    public Response<Map<String, Map<String, Integer>>> getShoppingCartContents(String userName) {
        return usersMap.get(userName).getShoppingCartContents();
    }
}
