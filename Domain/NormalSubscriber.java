package Domain;

public class NormalSubscriber implements SubscriberState{
    public void handle() {
        System.out.println("Normal subscriber behavior");
    }

    @Override
    public void changeState(Subscriber subscriber, Store store, SubscriberState newState) {
        if (newState instanceof StoreManager || newState instanceof StoreCreator || newState instanceof StoreCreator) {
            subscriber.setState(store, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }
}