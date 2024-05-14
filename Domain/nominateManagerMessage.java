package Domain;

import java.util.List;

public class nominateManagerMessage extends Message{
    public Store store;
    List<Permissions> permissions;
    public String subscriberID;

    public nominateManagerMessage(Store store, List<Permissions> permissions, String subscriberID) {
        super("Nominate manager message");
        this.store = store;
        this.permissions = permissions;
        this.subscriberID = subscriberID;
    }

    public List<Permissions> getPermissions() {
        return permissions;
    }

    @Override
    public void response(boolean answer) {
        if (answer) {
            store.nominateManager(subscriberID, permissions);
        }
    }
}
