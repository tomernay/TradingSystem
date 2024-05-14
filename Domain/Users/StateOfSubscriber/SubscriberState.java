package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Domain.Store.StoreData.Permissions;
import Domain.Users.Subscriber.Messages.Message;
import Domain.Users.Subscriber.Messages.nominateManagerMessage;
import Domain.Users.Subscriber.Messages.nominateOwnerMessage;


import java.util.List;

public abstract class SubscriberState {
    private String subscriberID;
    private Store store;

    public SubscriberState(String subscriberID, Store store) {
        this.subscriberID = subscriberID;
        this.store = store;
    }


    public abstract void changeState(Store store, String SubscriberID, SubscriberState newState);

    public Message makeNominateOwnerMessage(String subscriberID) {
        return new nominateOwnerMessage(this.store, subscriberID);
    }

    public Message makeNominateManagerMessage(String subscriberID, List<String> permissions) {
        return new nominateManagerMessage(store, Permissions.convertStringList(permissions), subscriberID);
    }
}
