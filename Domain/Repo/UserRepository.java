package Domain.Repo;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.Subscriber.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private Map<String, Subscriber> subscribers = new HashMap<>();
    private Map<ShoppingCart, User> users = new HashMap<>();


    public Response<String> makeStoreOwner(String subscriberUsername, Message message) {
        return subscribers.get(subscriberUsername).makeStoreOwner(message);
    }

    public Response<String> makeStoreManager(String subscriberUsername, Message message) {
        return subscribers.get(subscriberUsername).makeStoreManager(message);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return subscribers.get(subscriberUsername).messageResponse(answer);
    }

    public void sendMessageToUser(String user,Message message){
        subscribers.get(user).addMessage(message);
    }

    public boolean isUserExist(String username) {
        return subscribers.containsKey(username);
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.put(subscriber.getUsername(), subscriber);
    }

    public Subscriber getSubscriber(String username) {
        return subscribers.get(username);
    }

    public void addUser(User user) {
        users.put(user.getShoppingCart(), user);
    }

    public User getUser(ShoppingCart shoppingCart) {
        return users.get(shoppingCart);
    }

//    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
//        for (String subscriberName : subscriberNames) {
//            users.get(subscriberName).addMessage(new NormalMessage("Store " + storeID + " has been closed"));
//        }
//        return Response.success("Notification sent successfully", null);
//    }

    public Response<String> loginAsGuest(User user) {
        users.put(user.getShoppingCart(), user);
        return Response.success("You signed in as a GUEST", null);
    }

    public Response<String> logoutAsGuest(User user) {
        if(user.logoutAsGuest()){
            return Response.error("Error - can't signed out as a GUEST", null);
        }
        return Response.success("You signed out as a GUEST", null);
    }
}
