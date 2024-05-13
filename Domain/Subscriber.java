package Domain;

import java.util.HashMap;
import java.util.Map;

public class Subscriber extends User{
    private Map<Store, SubscriberState> storeStates = new HashMap<>();
    private Map<Store, Permission> storePermissions = new HashMap<>();

    public void addStore(Store store, SubscriberState state, Permission permissions) {
        storeStates.put(store, state);
        storePermissions.put(store, permissions);
    }

    public void changePermissions(Store store, Permission permissions) {
        if (storePermissions.containsKey(store)) {
            storePermissions.put(store, permissions);
            System.out.println("Permissions changed successfully for store: " + store.getName());
        } else {
            System.out.println("Subscriber is not associated with store: " + store.getName());
        }
    }

    public void changeState(Store store, SubscriberState newState) {
        if (storeStates.containsKey(store)) {
            SubscriberState currentState = storeStates.get(store);
            currentState.changeState(this, store, newState);
        } else {
            System.out.println("Subscriber is not associated with store: " + store.getName());
        }
    }

    public void setState(Store store, SubscriberState state) {
        storeStates.put(store, state);
    }
}
