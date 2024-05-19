package Domain.Market;

import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public class Market {
    private  MarketFacade marketFacade = new MarketFacade();

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return marketFacade.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> makeStoreOwner(String storeID, String subscriberUsername) {
        return marketFacade.makeStoreOwner(storeID, subscriberUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return marketFacade.isStoreManager(storeID, currentUsername);
    }

    public Response<String> makeStoreManager(String storeID, String subscriberUsername, List<String> permissions) {
        return marketFacade.makeStoreManager(storeID, subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return marketFacade.addManagerPermissions(storeID, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return marketFacade.removeManagerPermissions(storeID, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return marketFacade.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return marketFacade.messageResponse(subscriberUsername, answer);
    }

    public Response<Map<String, SubscriberState>> requestEmployeesStatus(String storeID) {
        return marketFacade.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<Permissions>>> requestManagersPermissions(String storeID) {
        return marketFacade.requestManagersPermissions(storeID);
    }

    public MarketFacade getMarketFacade() {
        return marketFacade;
    }
}
