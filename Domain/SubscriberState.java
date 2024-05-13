package Domain;

public interface SubscriberState {
    void changeState(Subscriber subscriber, Store store, SubscriberState newState);
}
