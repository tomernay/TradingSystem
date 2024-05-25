package Domain.Users.Subscriber.Messages;

import Domain.Store.StoreData.Permissions;
import Domain.Store.Store;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.List;

public class nominateManagerMessage extends Message {
    public Store store;
    List<Permissions> permissions;
    public String subscriberUsername;
    boolean isSubscribed;
    public String nominatorUsername;

    public nominateManagerMessage(Store store, List<Permissions> permissions, String subscriberUsername, boolean isSubscribed, String nominatorUsername) {
        super("Nominate manager message");
        this.store = store;
        this.permissions = permissions;
        this.subscriberUsername = subscriberUsername;
        this.isSubscribed = isSubscribed;
        this.nominatorUsername = nominatorUsername;
    }

    public List<Permissions> getPermissions() {
        return permissions;
    }

    @Override
    public Response<Message> response(boolean answer) {
        if (answer) {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has accepted the Manager nomination for the store: " + store.getName() + ".");
            store.nominateManager(subscriberUsername, permissions, nominatorUsername);
            SystemLogger.info("[INFO] User " + subscriberUsername + " has been nominated as a manager of the store: " + store.getName() + ".");
            return Response.success("User " + subscriberUsername + " has been nominated as a manager of the store: " + store.getName(), this);
        }
        else {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has declined the Manager nomination for the store: " + store.getName() + ".");
            if (!isSubscribed) {
                store.removeSubscriber(subscriberUsername);
            }
            return Response.success("User " + subscriberUsername + " has declined the Manager nomination for the store: " + store.getName() + ".", this);
        }
    }

    public String getNominator() {
        return nominatorUsername;
    }
}
