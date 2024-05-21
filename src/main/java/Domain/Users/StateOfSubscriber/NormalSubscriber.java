package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;

public class NormalSubscriber extends SubscriberState {

    public NormalSubscriber(Store store, String subscriberID) {
        super(subscriberID, store);
    }

    @Override
    public void changeState(Store store, String SubscriberID, SubscriberState newState) {
        if (newState instanceof StoreCreator || newState instanceof StoreManager || newState instanceof StoreOwner) {
            store.setState(SubscriberID, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }
}
