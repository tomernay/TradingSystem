package Domain;

public class nominateOwnerMessage extends Message{
    public Store store;
    public String subscriberID;


    public nominateOwnerMessage(Store store, String subscriberID) {
        super("Nominate owner message");
        this.store = store;
        this.subscriberID = subscriberID;
    }

    @Override
    public void response(boolean answer) {
        if (answer) {
            store.nominateOwner(subscriberID);
        }
    }
}
