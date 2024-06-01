package Utilities.Messages;

import Utilities.Response;
import Utilities.SystemLogger;

import java.util.List;

public class nominateManagerMessage extends Message {
    public String storeID;
    List<String> permissions;
    public String subscriberUsername;
    boolean isSubscribed;
    public String nominatorUsername;

    public nominateManagerMessage(String storeID, List<String> permissions, String subscriberUsername, boolean isSubscribed, String nominatorUsername) {
        super("Nominate manager message");
        this.storeID = storeID;
        this.permissions = permissions;
        this.subscriberUsername = subscriberUsername;
        this.isSubscribed = isSubscribed;
        this.nominatorUsername = nominatorUsername;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    @Override
    public Response<Message> response(boolean answer) {
        if (answer) {
            SystemLogger.info("[SUCCESS] User " + subscriberUsername + " has accepted the Manager nomination for the store: " + storeID + ".");
            return Response.success("",this);
        }
        else {
            SystemLogger.info("[SUCCESS] User " + subscriberUsername + " has declined the Manager nomination for the store: " + storeID + ".");
            return Response.success("",this);
        }
    }

    public String getStoreID() {
        return storeID;
    }

    public String getNominatorUsername() {
        return nominatorUsername;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }
}
