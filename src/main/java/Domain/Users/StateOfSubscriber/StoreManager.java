package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Utilities.Response;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANAGER")
public class StoreManager extends SubscriberState {
    private  String nominatorUsername;

    public StoreManager() {
    }

    public StoreManager(Store store, String subscriberUsername, String nominatorUsername) {
        super(subscriberUsername, store);
        this.nominatorUsername = nominatorUsername;
    }

    public String getNominatorUsername() {
        return nominatorUsername;
    }

    @Override
    public Response<String> changeState(Store store, String SubscriberID, SubscriberState newState) {
        if (newState instanceof StoreCreator || newState instanceof NormalSubscriber || newState instanceof StoreOwner) {
            store.setState(SubscriberID, newState);
            return Response.success("State changed successfully to " + newState.getClass().getSimpleName(), null);
        } else {
            return Response.error("Invalid state transition", null);
        }
    }

    @Override
    public String toString() {
        return "MANAGER";
    }
}
