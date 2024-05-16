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
        if (!storeService.isStoreOwner(storeID, storeOwnerID) && !storeService.isStoreCreator(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (storeService.isStoreOwner(storeID, subscriberID)) { //The subscriber is already the store owner
            return Response.error("The user you're trying to nominate is already the store owner.",null);
        }
        return userService.makeStoreOwner(storeID, subscriberID);
    }

    // Method to add a store manager subscription
    public Response<String> makeStoreManager(String storeID, String storeOwnerID, String subscriberID, List<String> permissions) {
        if (!storeService.isStoreOwner(storeID, storeOwnerID) && !storeService.isStoreCreator(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (storeService.isStoreOwner(storeID, subscriberID)) { //The subscriber is already the store owner
            return Response.error("The user you're trying to nominate is already the store owner.",null);
        }
        if (storeService.isStoreManager(storeID, subscriberID)) { //The subscriber is already the store manager
            return Response.error("The user you're trying to nominate is already the store manager.",null);
        }
        return userService.makeStoreManager(storeID, subscriberID, permissions);
    }

    // Method to change permissions of a store manager
    public Response<String> addManagerPermissions(String storeID, String storeOwnerID, String storeManagerID, String permission) {
        if (!storeService.isStoreOwner(storeID, storeOwnerID) && !storeService.isStoreCreator(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!storeService.isStoreManager(storeID, storeManagerID)) { //The subscriber is not the store manager
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return userService.addManagerPermissions(storeID, storeManagerID, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String storeOwnerID, String storeManagerID, String permission) {
        if (!storeService.isStoreOwner(storeID, storeOwnerID) && !storeService.isStoreCreator(storeID, storeOwnerID)) { //The storeCreatorID is not the store owner
            return Response.error("The user trying to do this action is not the store owner.",null);
        }
        if (!storeService.isStoreManager(storeID, storeManagerID)) { //The subscriber is not the store manager
            return Response.error("The user you're trying to change permissions for is not the store manager.",null);
        }
        return userService.removeManagerPermissions(storeID, storeManagerID, permission);
    }

    // Method to close a store
    public Response<String> closeStore(String storeID, String storeCreatorID) {
        if (!storeService.isStoreCreator(storeID, storeCreatorID)) { //The storeCreatorID is not the store owner
            return Response.error("The user trying to do this action is not the store creator.",null);
        }
        //notify all owners and managers
        //MORE TO IMPLEMENT
        return null;
    }

    public Response<String> messageResponse(String subscriberID, boolean answer) {
        return userService.messageResponse(subscriberID, answer);
    }
}
