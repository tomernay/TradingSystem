package Domain.Users.Subscriber;

import Domain.Externals.Security.Security;
import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Subscriber extends User {
    private List<String> stores;
    private Queue<Message> messages;
    String username;
    String password;
    String Token;

    public Subscriber(String username,String password) {
        this.stores = new ArrayList<>();
        this.messages = new PriorityQueue<>();
        this.password=password;
        this.username=username;
        Token=Security.generateJWT(username);
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

    public String getToken() {
        return Token;
    }

    public String getUsername() {
        return username;
    }
}
