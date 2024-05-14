package Domain.Repo;

import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.Subscriber.Subscriber;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private Map<Integer, Subscriber> users = new HashMap<>();
    public boolean makeStoreOwner(String subscriberID, Message message) {
        return users.get(Integer.parseInt(subscriberID)).makeStoreOwner(message);
    }

    public boolean makeStoreManager(String subscriberID, Message message) {
        return users.get(Integer.parseInt(subscriberID)).makeStoreManager(message);
    }

    public boolean messageResponse(String subscriberID, boolean answer) {
        return users.get(Integer.parseInt(subscriberID)).messageResponse(answer);
    }
}
