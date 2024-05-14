package Domain.Users.Subscriber;

import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Subscriber extends User {
    private List<String> stores;
    private Queue<Message> messages;

    public Subscriber() {
        this.stores = new ArrayList<>();
        this.messages = new PriorityQueue<>();
    }

    public void addStore(String storeID) {
        stores.add(storeID);
    }

    public boolean makeStoreOwner(Message message) {
        messages.add(message);
        return true;
    }

    public boolean messageResponse(boolean answer) {
        Message message = messages.poll();
        assert message != null;
        message.response(answer);
        return answer;
    }

    public boolean makeStoreManager(Message message) {
        messages.add(message);
        return true;
    }

}
