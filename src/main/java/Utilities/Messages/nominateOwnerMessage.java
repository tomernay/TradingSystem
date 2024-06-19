package Utilities.Messages;

import Utilities.Response;
import Utilities.SystemLogger;

public class nominateOwnerMessage extends Message {
    public String storeID;
    public String storeName;
    public String subscriberUsername;
    public boolean isSubscribed;
    public String nominatorUsername;


    public nominateOwnerMessage(String storeID, String StoreName, String subscriberID, boolean isSubscribed, String nominatorUsername) {
        super("Owner nomination request for store: " + StoreName + " from user: " + nominatorUsername + ". Accept or decline?");
        this.storeName = StoreName;
        this.storeID = storeID;
        this.subscriberUsername = subscriberID;
        this.isSubscribed = isSubscribed;
        this.nominatorUsername = nominatorUsername;
    }

    @Override
    public Response<Message> response(boolean answer) {
        if (answer) {
            SystemLogger.info("[SUCCESS] User " + subscriberUsername + " has accepted the Owner nomination for the store: " + storeID + ".");
            return Response.success("",this);
        }
        else {
            SystemLogger.info("[SUCCESS] User " + subscriberUsername + " has declined the Owner nomination for the store: " + storeID + ".");
            return Response.success("",this);
        }
    }

    public String getNominator() {
        return nominatorUsername;
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

    public String getStoreName() {
        return storeName;
    }
}
