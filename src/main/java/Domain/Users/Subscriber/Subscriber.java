package Domain.Users.Subscriber;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Presentation.application.View.UtilitiesView.Broadcaster;
import Utilities.Messages.Message;

import Domain.Users.User;
import Utilities.Messages.NormalMessage;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Subscriber extends User {
    private final List<Integer> subscribedStores;
    private final List<Message> messages;
    private String password;
    private String credit;
    private final Map<Integer, String> storesRole;

    public Subscriber(String username,String password) {
        super(username);
        this.subscribedStores = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.password = password;
        this.storesRole = new HashMap<>();
    }

    public void addStore(Integer storeID) {
        subscribedStores.add(storeID);
    }


//    public Response<String> messageResponse(boolean answer) {
//        Message message = messages.poll();
//        if (message == null) {
//            SystemLogger.error("[ERROR] No messages to respond to.");
//            return Response.error("No messages to respond to.", null);
//        }
//        Response<Message> response = message.response(answer);
//        return Response.success(response.getMessage(), null);
//    }

    public String getToken() {
        return Token;
    }

    public String getUsername() {
        return super.getUsername();
    }

    //yair added
    public synchronized Response<Integer> addMessage(Message m){
        Broadcaster.broadcast(m.getMessage(),username);
        if (m instanceof nominateOwnerMessage) {
            if (messages.stream().anyMatch(a -> a instanceof nominateOwnerMessage && ((nominateOwnerMessage) a).getStoreID().equals(((nominateOwnerMessage) m).getStoreID()))) {
                SystemLogger.error("[ERROR] User already has a pending owner nomination message.");
                return Response.error("User already has a pending owner nomination message.", null);
            }
            else {
                messages.add(m);
                SystemLogger.info("[SUCCESS] Owner nomination message successfully sent to: " + username);
                return Response.success("Owner nomination message successfully sent to: " + username, m.getId());
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
                return Response.success("Manager nomination message successfully sent to: " + username, m.getId());
            }
        }
        else {
            messages.add(m);
            SystemLogger.info("[SUCCESS] Message successfully sent to: " + username);
            return Response.success("Message successfully sent to: " + username, null);
        }

    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getPassword() {
        return password;
    }

    public Response<Message> ownerNominationResponse(Integer messageID, boolean answer) {
        if (messages.isEmpty())
            return Response.error("[ERROR] Message not found.", null);
        for (Message message : messages) {
            if (message.getId().equals(messageID)) {
                messages.remove(message);
                messages.add(new NormalMessage("You have been nominated to store: " + ((nominateOwnerMessage)message).getStoreName() + " as an owner, by: " + ((nominateOwnerMessage)message).getNominatorUsername() + "."));
                storesRole.put(((nominateOwnerMessage) message).getStoreID(), "Owner");
                return message.response(answer);
            }
        }
        SystemLogger.error("[ERROR] Message not found.");
        return Response.error("[ERROR] Message not found.", null);
    }

    public Response<Message> managerNominationResponse(Integer messageID, boolean answer) {
        if (messages.isEmpty())
            return Response.error("[ERROR] Message not found.", null);
        for (Message message : messages) {
            if (message.getId().equals(messageID)) {
                messages.remove(message);
                messages.add(new NormalMessage("You have been nominated to store: " + ((nominateManagerMessage)message).getStoreName() + " as a manager by: " + ((nominateManagerMessage)message).getNominatorUsername() + "."));
                storesRole.put(((nominateManagerMessage) message).getStoreID(), "Manager");
                return message.response(answer);
            }
        }
        SystemLogger.error("[ERROR] Message not found.");
        return Response.error("[ERROR] Message not found.", null);
    }

    public void resetToken() {
        Token = null;
    }

    public void addCreatorRole(Integer storeID) {
        storesRole.put(storeID, "Creator");
    }

    public Response<Map<Integer, String>> getStoresRole() {
        return Response.success("[SUCCESS] Successfully retrieved the user's stores roles.", storesRole);
    }

    public void removeStoreRole(Integer storeID) {
        storesRole.remove(storeID);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        super.setUsername(username);
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


    public Response<String> removeMessage(Integer messageID) {
        for (Message message : messages) {
            if (message.getId().equals(messageID)) {
                messages.remove(message);
                return Response.success("[SUCCESS] Message removed successfully.", null);
            }
        }
        return Response.error("[ERROR] Message not found.", null);


    }

    public Response<Integer> getUnreadMessagesCount() {
        int unread = 0;
        for (Message message : messages) {
            if (!message.isRead())
                unread++;
        }
        return Response.success("[SUCCESS] Successfully retrieved the number of unread messages.", unread);
    }

    public Response<String> sendMessage(String message) {
        messages.add(new NormalMessage(message));
        return Response.success("[SUCCESS] Message sent successfully.", null);
    }
}