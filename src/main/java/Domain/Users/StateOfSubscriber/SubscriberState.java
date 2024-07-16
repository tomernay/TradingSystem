package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Entity
public class SubscriberState {
    @Id
    private String subscriberUsername;

    @ManyToOne
    @JoinColumn(name = "store_id", foreignKey = @ForeignKey(name = "FK_store_subscribers"))
    private Store store;

    public SubscriberState() {
    }

    public SubscriberState(String subscriberUsername, Store store) {
        this.subscriberUsername = subscriberUsername;
        this.store = store;
    }


    public Response<String> changeState(Store store, String subscriberUsername, SubscriberState newState) {
        return store.changeState(subscriberUsername, newState);
    }

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
    public String toString() {
        return "SubscriberState{" +
                "subscriberUsername='" + subscriberUsername + '\'' +
                ", store=" + store +
                '}';
    }
}
