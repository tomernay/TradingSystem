package src.main.java.Domain.Users.StateOfSubscriber;

import src.main.java.Domain.Store.Store;


public class StoreManager extends SubscriberState {

    public StoreManager(Store store, String subscriberID) {
        super(subscriberID, store);
    }

    @Override
    public void changeState(Store store, String SubscriberID, SubscriberState newState) {
        if (newState instanceof StoreCreator || newState instanceof NormalSubscriber || newState instanceof StoreOwner) {
            store.setState(SubscriberID, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }
}
