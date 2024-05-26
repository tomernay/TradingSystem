package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Domain.Store.StoreData.Permissions;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;


import java.util.List;

public abstract class SubscriberState {
    private String subscriberUsername;
    private Store store;

    public SubscriberState(String subscriberUsername, Store store) {
        this.subscriberUsername = subscriberUsername;
        this.store = store;
    }


    public abstract void changeState(Store store, String subscriberUsername, SubscriberState newState);

    public Response<Message> makeNominateOwnerMessage(String subscriberUsername, boolean isSubscribed, String nominatorUsername) {
        return Response.success("Created message successfully", new nominateOwnerMessage(this.store.getId(), subscriberUsername, isSubscribed, nominatorUsername));
    }

    public Response<Message> makeNominateManagerMessage(String subscriberUsername, List<String> permissions, boolean isSubscribed, String nominatorUsername) {
        return Response.success("Created message successfully", new nominateManagerMessage(this.store.getId(), permissions, subscriberUsername, isSubscribed, nominatorUsername));
    }

    public String getSubscriberUsername() {
        return subscriberUsername;
    }

    //Override toString()
    @Override
    public abstract String toString();
}
