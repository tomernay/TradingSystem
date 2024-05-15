package Service;

import Domain.Market.Market;
<<<<<<< Updated upstream
import Domain.Repo.UserRepository;
import Domain.Users.Subscriber.Subscriber;
=======
>>>>>>> Stashed changes

import java.util.List;

public class UserService {
    private Market market;


    public UserService(Market market) {
        this.market = market;
    }

    // Method to add a store owner subscription
    public boolean addStoreOwnerSubscription(String storeID, String storeOwnerID, String subscriberID) {
        if (!market.isStoreOwner(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return false;
        }
        if (market.isStoreOwner(storeID, subscriberID)) { //The subscriber is already the store owner
            return false;
        }
        return market.makeStoreOwner(storeID, subscriberID);
    }

    // Method to add a store manager subscription
    public boolean addStoreManagerSubscription(String storeID, String storeOwnerID, String subscriberID, List<String> permissions) {
        if (!market.isStoreOwner(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return false;
        }
        if (market.isStoreOwner(storeID, subscriberID) || market.isStoreManager(storeID, storeOwnerID)) { //The subscriber is already the store owner / manager
            return false;
        }
        return market.makeStoreManager(storeID, subscriberID, permissions);
    }

    // Method to change permissions of a store manager
    public boolean addManagerPermissions(String storeID, String storeOwnerID, String storeManagerID, String permission) {
        if (!market.isStoreOwner(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return false;
        }
        return market.addManagerPermissions(storeID, storeManagerID, permission);
    }

    public boolean removeManagerPermissions(String storeID, String storeOwnerID, String storeManagerID, String permission) {
        if (!market.isStoreOwner(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return false;
        }
        return market.removeManagerPermissions(storeID, storeManagerID, permission);
    }

    // Method to close a store
    public boolean closeStore(String storeID, String storeCreatorID) {
        if (!market.isStoreCreator(storeID, storeCreatorID)) { //The storeCreatorID is not the store creator
            return false;
        }
        //notify all owners and managers
        //MORE TO IMPLEMENT
        return true;
    }

    public boolean messageResponse(String subscriberID, boolean answer) {
        return market.messageResponse(subscriberID, answer);
    }

    // Method to prompt the subscriber to accept the subscription
    private boolean promptSubscription(String subscriberUsername, String targetUsername) {
        // Implement the prompt logic here
        // For example, display a prompt to the subscriber
        // and wait for user input to accept or decline the subscription
        return true; // Assume subscription is accepted
    }


    //yair added
    //register a new user
    public boolean register(String username,String password){
        Subscriber subscriber=new Subscriber(username,password);
        if(!market.getMarketFacade().getUserRepository().isUserExist(username)) {
            market.getMarketFacade().getUserRepository().addUser(subscriber);
            return true;
        }
        else {
            return  false;
        }
    }

    public Subscriber getUser(String username){
        return market.getMarketFacade().getUserRepository().getUser(username);
    }


}
