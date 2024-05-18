package Domain.Users.Subscriber;

import Domain.Externals.Security.Security;
import Domain.Users.Subscriber.Messages.Message;

import Domain.Users.User;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Subscriber extends User {
    private List<String> stores;
    private Queue<Message> messages;
    String username;
    String password;
    String Token;
    String credit;


    public Subscriber(String username,String password) {
        this.stores = new ArrayList<>();
        this.messages = new LinkedBlockingQueue<>();
        this.password=password;
        this.username=username;
        Token=Security.generateJWT(username);
        System.out.println(Token);

    }

    public void addStore(String storeID) {
        stores.add(storeID);
    }

    public Response<String> makeStoreOwner(Message message) {
        messages.add(message);
        SystemLogger.info("Owner nomination message successfully sent to: " + username);
        return Response.success("Owner nomination request sent to user " + username, null);
    }

    public Response<String> messageResponse(boolean answer) {
        Message message = messages.poll();
        if (message == null) {
            SystemLogger.error("[ERROR] No messages to respond to.");
            return Response.error("No messages to respond to.", null);
        }
        message.response(answer);
        return Response.success("Message responded.", null);
    }

    public Response<String> makeStoreManager(Message message) {
        messages.add(message);
        SystemLogger.info("Manager nomination message successfully sent to: " + username);
        return Response.success("Manager nomination request sent to user " + username, null);
    }

    public String getToken() {
        return Token;
    }

    public String getUsername() {
        return username;
    }

    //yair added
    public void addMessage(Message m){
        messages.add(m);
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}
