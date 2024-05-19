package src.main.java.Domain.Users.Subscriber.Messages;

import src.main.java.Domain.Store.StoreData.Permissions;
import src.main.java.Domain.Store.Store;
import src.main.java.Utilities.SystemLogger;

import java.util.List;

public class nominateManagerMessage extends Message {
    public Store store;
    List<Permissions> permissions;
    public String subscriberUsername;

    public nominateManagerMessage(Store store, List<Permissions> permissions, String subscriberUsername) {
        super("Nominate manager message");
        this.store = store;
        this.permissions = permissions;
        this.subscriberUsername = subscriberUsername;
    }

    public List<Permissions> getPermissions() {
        return permissions;
    }

    @Override
    public void response(boolean answer) {
        if (answer) {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has accepted the Manager nomination for the store: " + store.getName() + ".");
            store.nominateManager(subscriberUsername, permissions);
            SystemLogger.info("[INFO] User " + subscriberUsername + " has been nominated as a manager of the store: " + store.getName() + ".");
        }
        else {
            SystemLogger.info("[INFO] User " + subscriberUsername + " has declined the Manager nomination for the store: " + store.getName() + ".");
        }
    }
}
