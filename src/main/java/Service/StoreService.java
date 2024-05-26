package Service;

import Domain.Market.StoreFacade;
import Domain.Market.UserFacade;
import Domain.Repo.StoreRepository;
import Utilities.Messages.Message;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoreService  {
    private StoreFacade storeFacade;
    private UserService userService;
    public StoreService(){
        storeFacade = new StoreFacade();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Response<String> addStore(String name, String creator, String token) {
        SystemLogger.info("[START] User: " + creator + " is trying to create a store with name: " + name);
        if(userService.isValidToken(token,creator)) {
            return storeFacade.openStore(name,creator);
        }
        SystemLogger.error("[ERROR] User: " + creator + " tried to add a store but the token was invalid");
        return Response.error("Invalid token",null);
    }

//    public Store getStore(String storeID){
//        return market.getMarketFacade().getStoreRepository().getStore(storeID);
//    }


    public boolean isStoreOwner(String storeID, String currentUsername) {
        return storeFacade.isStoreOwner(storeID, currentUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return storeFacade.isStoreManager(storeID, currentUsername);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return storeFacade.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> closeStore(String storeID, String currentUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to close store: " + storeID);
        if(userService.isValidToken(token,currentUsername)) {
            Response<List<String>> storeCloseResponse = storeFacade.closeStore(storeID, currentUsername);
            if (!storeCloseResponse.isSuccess()) {
                return Response.error(storeCloseResponse.getMessage(), null);
            }
            return userService.sendCloseStoreNotification(storeCloseResponse.getData(), storeID);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to close store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to add permissions: " + permission + " to " + subscriberUsername);
        if(userService.isValidToken(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return storeFacade.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to remove permissions: " + permission + " from " + subscriberUsername);
        if(userService.isValidToken(token,currentUsername)) {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return storeFacade.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> nominateOwner(String storeID, String currentUsername, String nominatorUsername) {
        return storeFacade.nominateOwner(storeID, currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(String storeID, String currentUsername) {
        return storeFacade.removeStoreSubscription(storeID, currentUsername);
    }

    public Response<String> nominateManager(String storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        return storeFacade.nominateManager(storeID, currentUsername, permissions, nominatorUsername);
    }

    public Response<Map<String, String>> requestEmployeesStatus(String storeID){
        return storeFacade.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID){
        return storeFacade.requestManagersPermissions(storeID);
    }

    public Response<Set<String>> waiveOwnership(String storeID, String currentUsername) {
        return storeFacade.waiveOwnership(storeID, currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(String storeID, String currentUsername, String subscriberUsername) {
        return storeFacade.makeNominateOwnerMessage(storeID, currentUsername, subscriberUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return storeFacade.makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
    }

    public boolean storeExists(String storeID) {
        return storeFacade.storeExists(storeID);
    }

    public StoreFacade getStoreFacade() {
        return storeFacade;
    }


}
