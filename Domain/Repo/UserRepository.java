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
    private Map<ShoppingCart, User> guests = new HashMap<>();

    public Response<String> loginAsGuest(User user) {
        guests.put(user.getShoppingCart(), user);
        return Response.success("You signed in as a GUEST", null);
    }

    public Response<String> logoutAsGuest(User user) {
        if(user.logoutAsGuest()){
            return Response.success("You signed out as a GUEST", null);
        }
        return Response.error("Error - can't signed out as a GUEST", null);
    }

    public void addGuest(User user) {
        guests.put(user.getShoppingCart(), user);
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

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
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
}
