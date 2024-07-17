package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import jakarta.persistence.*;


import java.util.List;

@Entity
@Table(name = "subscriber_state")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class SubscriberState {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false) // Change this if needed to match your DB schema
    private String subscriberUsername;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id", referencedColumnName = "storeid", insertable = false, updatable = false)
    private Store store;

    public SubscriberState(String subscriberUsername, Store store) {
        this.subscriberUsername = subscriberUsername;
        this.store = store;
    }

    public SubscriberState(){

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
