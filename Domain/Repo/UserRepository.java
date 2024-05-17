package Domain.Repo;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;

import java.util.HashMap;
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

    public void addUser(Subscriber user) {
        subscribers.put(user.getUsername(), user);
    }

    public Subscriber getUser(String username) {
        return subscribers.get(username);
    }
}
