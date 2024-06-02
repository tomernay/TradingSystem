package Domain.Users.Subscriber;

import Domain.Externals.Security.Security;
import Utilities.Messages.Message;

import Domain.Users.User;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Subscriber extends User {
    private List<String> stores;
    private Queue<Message> messages;
    private String password;
    private String credit;


    public Subscriber(String username,String password) {
        super(username);
        this.stores = new ArrayList<>();
        this.messages = new LinkedBlockingQueue<>();
        this.password = password;
    }

    public void addStore(String storeID) {
        stores.add(storeID);
    }


    public Response<Message> messageResponse(boolean answer) {
        Message message = messages.poll();
        if (message == null) {
            SystemLogger.error("[ERROR] No messages to respond to.");
            return Response.error("No messages to respond to.", null);
        }
        return message.response(answer);
    }

    public String getToken() {
        return Token;
    }

    public String getUsername() {
        return super.getUsername();
    }

    //yair added
    public synchronized Response<String> addMessage(Message m){
        if (m instanceof nominateOwnerMessage) {
            if (messages.stream().anyMatch(a -> a instanceof nominateOwnerMessage && ((nominateOwnerMessage) a).getStoreID().equals(((nominateOwnerMessage) m).getStoreID()))) {
                SystemLogger.error("[ERROR] User already has a pending owner nomination message.");
                return Response.error("User already has a pending owner nomination message.", null);
            }
            else {
                messages.add(m);
                SystemLogger.info("[SUCCESS] Owner nomination message successfully sent to: " + username);
                return Response.success("Owner nomination message successfully sent to: " + username, null);
            }
        }
        else if (m instanceof nominateManagerMessage) {
            if (messages.stream().anyMatch(a -> a instanceof nominateManagerMessage && ((nominateManagerMessage) a).getStoreID().equals(((nominateManagerMessage) m).getStoreID()))) {
                SystemLogger.error("[ERROR] User already has a pending manager nomination message.");
                return Response.error("User already has a pending manager nomination message.", null);
            }
            else {
                messages.add(m);
                SystemLogger.info("[SUCCESS] Manager nomination message successfully sent to: " + username);
                return Response.success("Manager nomination message successfully sent to: " + username, null);
            }
        }
        else {
            messages.add(m);
            SystemLogger.info("[SUCCESS] Message successfully sent to: " + username);
            return Response.success("Message successfully sent to: " + username, null);
        }
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public Queue<Message> getMessages() {
        return messages;
    }

    public String getPassword() {
        return password;
    }

    public Response<Message> ownerNominationResponse(boolean answer) {
        Message message = messages.poll();
        if (message == null) {
            SystemLogger.error("[ERROR] No messages to respond to.");
            return Response.error("No messages to respond to.", null);
        }
        return message.response(answer);
    }

    public Response<Message> managerNominationResponse(boolean answer) {
        Message message = messages.poll();
        if (message == null) {
            SystemLogger.error("[ERROR] No messages to respond to.");
            return Response.error("No messages to respond to.", null);
        }
        return message.response(answer);
    }

    public void resetToken() {
        Token = null;
    }
}
