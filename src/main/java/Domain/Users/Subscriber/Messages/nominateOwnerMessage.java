package Domain.Users.Subscriber.Messages;

import Domain.Store.Store;
import Utilities.Response;
import Utilities.SystemLogger;

public class nominateOwnerMessage extends Message {
    public Store store;
    public String subscriberUsername;
    public boolean isSubscribed;
    public String nominatorUsername;


    public nominateOwnerMessage(Store store, String subscriberID, boolean isSubscribed, String nominatorUsername) {
        super("Nominate owner message");
        this.store = store;
        this.subscriberUsername = subscriberID;
        this.isSubscribed = isSubscribed;
        this.nominatorUsername = nominatorUsername;
    }

    @Override
    public Response<Message> response(boolean answer) {
        if (answer) {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has accepted the Owner nomination request for the store: " + store.getName() + ".");
            store.nominateOwner(subscriberUsername, nominatorUsername);
            SystemLogger.info("[INFO] User " + subscriberUsername + " has been nominated as an owner of the store: " + store.getName());
            return Response.success("User " + subscriberUsername + " has been nominated as an owner of the store: " + store.getName(), this);
        }
        else {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has declined the Owner nomination request for the store: " + store.getName() + ".");
            if (!isSubscribed) {
                store.removeSubscriber(subscriberUsername);
            }
            return Response.error("User " + subscriberUsername + " has declined the Owner nomination request for the store: " + store.getName() + ".", this);
        }
    }

    public String getNominator() {
        return nominatorUsername;
    }
}
