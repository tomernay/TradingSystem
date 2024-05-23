package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;


public class StoreOwner extends SubscriberState {
    private String nominatorUsername;

    public StoreOwner(Store store, String subscriberID, String nominatorUsername) {
        super(subscriberID, store);
        this.nominatorUsername = nominatorUsername;
    }

    @Override
    public void changeState(Store store, String SubscriberID, SubscriberState newState) {
        if (newState instanceof StoreManager || newState instanceof NormalSubscriber || newState instanceof StoreCreator) {
            store.setState(SubscriberID, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }

}
