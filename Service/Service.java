package Service;

import Domain.Market.Market;
import Utilities.Response;

import java.util.List;

public class Service {
    private UserService userService;
    private PaymentService paymentService;
    private StoreService storeService;
    private Market market;
    public Service(){
        market=new Market();
        userService=new UserService(market);
        paymentService=new PaymentService(market);
          storeService=new StoreService(market);
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public UserService getUserService() {
        return userService;
    }

    public StoreService getStoreService() {
        return storeService;
    }

    // Method to add a store owner subscription
    public Response<String> makeStoreOwner(String storeID, String storeOwnerID, String subscriberID) {
        if (!storeService.isStoreOwner(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (storeService.isStoreOwner(storeID, subscriberID)) { //The subscriber is already the store owner
            return Response.error("The user you're trying to nominate is already the store owner.",null);
        }
        return userService.makeStoreOwner(storeID, subscriberID);
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
}
