package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;


import java.util.List;

public abstract class SubscriberState {
    private final String subscriberUsername;
    private final Store store;

    public SubscriberState(String subscriberUsername, Store store) {
        this.subscriberUsername = subscriberUsername;
        this.store = store;
    }


    public abstract Response<String> changeState(Store store, String subscriberUsername, SubscriberState newState);

    public Response<Message> makeNominateOwnerMessage(String subscriberUsername, boolean isSubscribed, String nominatorUsername) {
        return Response.success("Created message successfully", new nominateOwnerMessage(this.store.getId(), this.store.getName(), subscriberUsername, isSubscribed, nominatorUsername));
    }

    public Response<Message> makeNominateManagerMessage(String subscriberUsername, List<String> permissions, boolean isSubscribed, String nominatorUsername) {
        return Response.success("Created message successfully", new nominateManagerMessage(this.store.getId(), this.store.getName(), permissions, subscriberUsername, isSubscribed, nominatorUsername));
    }

    public String getSubscriberUsername() {
        return subscriberUsername;
    }

    //Override toString()
    @Override
    public abstract String toString();
}
