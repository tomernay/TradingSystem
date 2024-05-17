package Domain.Repo;

import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.Subscriber.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;
import Utilities.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private Map<String, Subscriber> users = new HashMap<>();
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
        return Response.success("Notification sent successfully", null);
    }
}
