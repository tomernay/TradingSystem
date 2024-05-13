package Domain;

public class StoreOwner implements SubscriberState{

    @Override
    public void changeState(Subscriber subscriber, Store store, SubscriberState newState) {
        if (newState instanceof StoreManager || newState instanceof StoreCreator || newState instanceof NormalSubscriber) {
            subscriber.setState(store, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }

}
