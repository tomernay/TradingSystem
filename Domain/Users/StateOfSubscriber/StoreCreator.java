package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;


public class StoreCreator extends SubscriberState {

    public StoreCreator(Store store, String subscriberID) {
        super(subscriberID, store);
    }

    @Override
    public void changeState(Store store, String SubscriberID, SubscriberState newState) {
        if (newState instanceof StoreManager || newState instanceof NormalSubscriber || newState instanceof StoreOwner) {
            store.setState(SubscriberID, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }
}
