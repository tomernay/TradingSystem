package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Utilities.Response;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;


public class NormalSubscriber extends SubscriberState {



    public NormalSubscriber(Store store, String subscriberID) {
        super(subscriberID, store);
    }

    @Override
    public Response<String> changeState(Store store, String SubscriberID, SubscriberState newState) {
        if (newState instanceof StoreCreator || newState instanceof StoreManager || newState instanceof StoreOwner) {
            store.setState(SubscriberID, newState);
            return Response.success("State changed successfully to " + newState.getClass().getSimpleName(), null);
        }
        else {
            return Response.error("Invalid state transition", null);
        }
    }

    @Override
    public String toString() {
        return "SUBSCRIBER";
    }
}
