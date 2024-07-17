package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Utilities.Response;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "store_creators")
public class StoreCreator extends SubscriberState {

    public StoreCreator() {
        super();
    }

    public StoreCreator(Store store, String subscriberID) {
        super(subscriberID, store);
    }

    @Override
    public Response<String> changeState(Store store, String SubscriberID, SubscriberState newState) {
        if (newState instanceof StoreManager || newState instanceof NormalSubscriber || newState instanceof StoreOwner) {
            store.setState(SubscriberID, newState);
            return Response.success("State changed successfully to " + newState.getClass().getSimpleName(), null);
        } else {
            return Response.error("Invalid state transition", null);
        }
    }

    @Override
    public String toString() {
        return "CREATOR";
    }
}
