package Domain.Users.Subscriber;

import Utilities.Messages.Message;

import Domain.Users.User;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Subscriber extends User {
    private List<String> subscribedStores;
    private Queue<Message> messages;
    private String password;
    private String credit;
    private Map<String, String> storesRole;





    public Subscriber(String username,String password) {
        super(username);
        this.subscribedStores = new ArrayList<>();
        this.messages = new LinkedBlockingQueue<>();
        this.password = password;
        this.storesRole = new HashMap<>();
    }

    public void addStore(String storeID) {
        subscribedStores.add(storeID);
    }


    public Response<Message> messageResponse(boolean answer) {
        Message message = messages.poll();
        if (message == null) {
            SystemLogger.error("[ERROR] No messages to respond to.");
            return Response.error("No messages to respond to.", null);
        }
        return message.response(answer);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        super.setUsername(username);
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
        storesRole.put(((nominateOwnerMessage) message).getStoreID(), "Owner");
        return message.response(answer);
    }

    public Response<Message> managerNominationResponse(boolean answer) {
        Message message = messages.poll();
        if (message == null) {
            SystemLogger.error("[ERROR] No messages to respond to.");
            return Response.error("No messages to respond to.", null);
        }
        storesRole.put(((nominateManagerMessage) message).getStoreID(), "Manager");
        return message.response(answer);
    }

    public void resetToken() {
        Token = null;
    }

    public void addCreatorRole(String storeID) {
        storesRole.put(storeID, "Creator");
    }

    public Response<Map<String, String>> getStoresRole() {
        return Response.success("[SUCCESS] Successfully retrieved the user's stores roles.", storesRole);
    }

    public void removeStoreRole(String storeID) {
        storesRole.remove(storeID);
    }

    public Response<String> isOwner() {
        if(storesRole.containsValue("Owner")){
            return Response.success("User is an owner",null);
        }
        return Response.error("User is not an owner",null);
    }

    public Response<String> isManager() {
        if(storesRole.containsValue("Manager")){
            return Response.success("User is a manager","true");
        }
        return Response.error("User is not a manager",null);
    }

    public Response<String> isCreator() {
        if(storesRole.containsValue("Creator")){
            return Response.success("User is a creator","true");
        }
        return Response.error("User is not a creator",null);
    }
}
