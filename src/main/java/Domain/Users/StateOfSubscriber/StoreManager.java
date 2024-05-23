package Domain.Users.StateOfSubscriber;

import Domain.Store.Store;


public class StoreManager extends SubscriberState {
    String currentUsername;

    public StoreManager(Store store, String subscriberID, String currentUsername) {
        super(subscriberID, store);
        this.currentUsername = currentUsername;
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
