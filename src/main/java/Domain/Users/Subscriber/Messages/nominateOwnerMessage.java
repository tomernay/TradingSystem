package Domain.Users.Subscriber.Messages;

import Domain.Store.Store;
import Utilities.SystemLogger;

public class nominateOwnerMessage extends Message {
    public Store store;
    public String subscriberUsername;
    public boolean isSubscribed;


    public nominateOwnerMessage(Store store, String subscriberID, boolean isSubscribed) {
        super("Nominate owner message");
        this.store = store;
        this.subscriberUsername = subscriberID;
        this.isSubscribed = isSubscribed;
    }

    @Override
    public void response(boolean answer) {
        if (answer) {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has accepted the Owner nomination request for the store: " + store.getName() + ".");
            store.nominateOwner(subscriberUsername);
            SystemLogger.info("[INFO] User " + subscriberUsername + " has been nominated as an owner of the store: " + store.getName());
        }
        else {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has declined the Owner nomination request for the store: " + store.getName() + ".");
            if (!isSubscribed) {
                store.removeSubscriber(subscriberUsername);
            }
        }
    }
}
