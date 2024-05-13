package Domain;

public class StoreManager implements SubscriberState{

    @Override
    public void changeState(Subscriber subscriber, Store store, SubscriberState newState) {
        if (newState instanceof NormalSubscriber || newState instanceof StoreCreator || newState instanceof StoreOwner) {
            subscriber.setState(store, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }
}
