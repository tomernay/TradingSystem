package Domain;

public class StoreCreator implements SubscriberState{

    @Override
    public void changeState(Subscriber subscriber, String storeID, SubscriberState newState) {
        if (newState instanceof StoreManager || newState instanceof NormalSubscriber || newState instanceof StoreOwner) {
            subscriber.setState(storeID, newState);
            System.out.println("State changed successfully to " + newState.getClass().getSimpleName());
        } else {
            System.out.println("Invalid state transition");
        }
    }
}
