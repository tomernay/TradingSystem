package Service;

import Domain.Externals.Security.Security;
import Domain.Market.Market;
import Domain.Repo.UserRepository;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Domain.Users.Subscriber.Subscriber;
import Utilities.Response;


import java.util.List;
import java.util.Map;

public class UserService {
    private Market market;


    public UserService(Market market) {
        this.market = market;
    }

    // Method to add a store owner subscription
    public Response<String> makeStoreOwner(String storeID, String subscriberUsername) {
        return market.makeStoreOwner(storeID, subscriberUsername);
    }

    // Method to add a store manager subscription
    public Response<String> makeStoreManager(String storeID, String subscriberUsername, List<String> permissions) {
        return market.makeStoreManager(storeID, subscriberUsername, permissions);
    }

    // Method to change permissions of a store manager
    public Response<String> addManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return market.addManagerPermissions(storeID, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return market.removeManagerPermissions(storeID, subscriberUsername, permission);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return market.messageResponse(subscriberUsername, answer);
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
            return false;
        }
    }

    public Response<Map<String, SubscriberState>> requestEmployeesStatus(String storeID, String userName, String token){
        if(Security.isValidJWT(token,userName)) {
            return market.requestEmployeesStatus(storeID);
        }
        return Response.error("invalid token", null);
    }

    public Response<Map<String, List<Permissions>>> requestManagersPermissions(String storeID, String userName, String token){
        if(Security.isValidJWT(token,userName)) {
            return market.requestManagersPermissions(storeID);
        }
        return Response.error("invalid token", null);
    }

    public Subscriber getUser(String username){
        return market.getMarketFacade().getUserRepository().getUser(username);
    }
}
