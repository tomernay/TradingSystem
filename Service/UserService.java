package Service;

import Domain.Market;

import java.util.List;

public class UserService {
    private Market market;

    public UserService(Market market) {
        this.market = market;
    }

    // Method to add a store owner subscription
    public void addStoreOwnerSubscription(String storeCreatorID, String subscriberID) {
//        // Check if the storeOwner is already associated with a store
//        if (storeOwner instanceof StoreOwner && ((StoreOwner) storeOwner).getStore() == null) {
//            // Check if the subscriber is not already associated with another store owner
//            if (subscriber instanceof Subscriber && !(((Subscriber) subscriber).getStore() instanceof StoreOwner)) {
//                // Prompt the subscriber to accept the subscription
//                // Assuming there is a method for prompting the subscriber to accept the subscription
//                boolean subscriptionAccepted = promptSubscription(subscriber.getUsername(), storeOwner.getUsername());
//
//                if (subscriptionAccepted) {
//                    // Create a store owner subscription
//                    ((Subscriber) subscriber).setStoreOwnerSubscription((StoreOwner) storeOwner);
//                    System.out.println(subscriber.getUsername() + " subscribed to " + storeOwner.getUsername() + " as a store owner.");
//                } else {
//                    System.out.println(subscriber.getUsername() + " declined the subscription request.");
//                }
//            } else {
//                System.out.println("Subscriber is already associated with another store owner.");
//            }
//        } else {
//            System.out.println("Store owner is already associated with a store.");
//        }
    }

    // Method to add a store manager subscription
    public void addStoreManagerSubscription(String storeCreatorID, String subscriberID, List<String> permissions) {
//        // Check if the storeManager is already associated with a store
//        if (storeManager instanceof StoreManager && ((StoreManager) storeManager).getStore() == null) {
//            // Check if the subscriber is not already associated with another store owner
//            if (subscriber instanceof Subscriber && !(((Subscriber) subscriber).getStore() instanceof StoreManager)) {
//                // Prompt the subscriber to accept the subscription
//                // Assuming there is a method for prompting the subscriber to accept the subscription
//                boolean subscriptionAccepted = promptSubscription(subscriber.getUsername(), storeManager.getUsername());
//
//                if (subscriptionAccepted) {
//                    // Create a store manager subscription
//                    ((Subscriber) subscriber).setStoreManagerSubscription((StoreManager) storeManager, permissions);
//                    System.out.println(subscriber.getUsername() + " subscribed to " + storeManager.getUsername() + " as a store manager.");
//                } else {
//                    System.out.println(subscriber.getUsername() + " declined the subscription request.");
//                }
//            } else {
//                System.out.println("Subscriber is already associated with another store manager.");
//            }
//        } else {
//            System.out.println("Store manager is already associated with a store.");
//        }
    }

    // Method to change permissions of a store manager
    public void changeManagerPermissions(String storeCreatorID, String storeManagerID, String permission, String value) {
//        storeManager.setPermissions(permissions);
//        System.out.println("Permissions changed successfully for store manager: " + storeManager.getUsername());
    }

    // Method to close a store
    public void closeStore(String storeCreatorID) {
//        if (storeOwner instanceof StoreOwner) {
//            // Close the store
//            storeOwner.closeStore();
//            System.out.println("Store closed successfully.");
//        } else {
//            System.out.println("Invalid operation. User is not a store owner.");
//        }
    }

    // Method to prompt the subscriber to accept the subscription
    private boolean promptSubscription(String subscriberUsername, String targetUsername) {
        // Implement the prompt logic here
        // For example, display a prompt to the subscriber
        // and wait for user input to accept or decline the subscription
        return true; // Assume subscription is accepted
    }
}
