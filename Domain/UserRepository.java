package Domain;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private Map<String, Subscriber> users = new HashMap<>();
    public boolean makeStoreOwner(String subscriberID, Message message) {
        return users.get(Integer.parseInt(subscriberID)).makeStoreOwner(message);
    }

    public boolean makeStoreManager(String subscriberID, Message message) {
        return users.get(Integer.parseInt(subscriberID)).makeStoreManager(message);
    }

    public boolean messageResponse(String subscriberID, boolean answer) {
        return users.get(Integer.parseInt(subscriberID)).messageResponse(answer);
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
}
