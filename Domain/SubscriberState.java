package Domain;

public interface SubscriberState {
    void changeState(Subscriber subscriber, String storeID, SubscriberState newState);
}
