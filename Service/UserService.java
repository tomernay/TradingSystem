package Service;

import Domain.Market.Market;
import Domain.Repo.UserRepository;
import Domain.Users.Subscriber.Cart.ShoppingCart;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;


import java.util.List;

public class UserService {
    private Market market;


    public UserService(Market market) {
        this.market = market;
    }

    // Method to add a store owner subscription
    public Response<String> makeStoreOwner(String storeID, String currentUsername, String subscriberUsername) {
        return market.makeStoreOwner( currentUsername, subscriberUsername);
    }

    // Method to add a store manager subscription
    public Response<String> makeStoreManager(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return market.makeStoreManager(currentUsername, subscriberUsername, permissions);
    }

    // Method to change permissions of a store manager
    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return market.addManagerPermissions(currentUsername, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return market.removeManagerPermissions(currentUsername, subscriberUsername, permission);
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

    public Response<String> loginAsGuest(){
        return market.loginAsGuest();
    }

    //function as a Guest - exit from the website
    public Response<String> logoutAsGuest(){
        return market.logoutAsGuest();
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

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return market.sendCloseStoreNotification(subscriberNames, storeID);
    }

    public boolean userExists(String subscriberUsername) {
        return market.getMarketFacade().getUserRepository().isUserExist(subscriberUsername);
    }
}
