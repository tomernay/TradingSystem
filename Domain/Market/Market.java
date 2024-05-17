package Domain.Market;

import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;

import java.util.List;

public class Market {
    private  MarketFacade marketFacade = new MarketFacade();

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return marketFacade.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> makeStoreOwner(String storeID, String currentUsername, String subscriberUsername) {
        return marketFacade.makeStoreOwner(storeID, currentUsername, subscriberUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return marketFacade.isStoreManager(storeID, currentUsername);
    }

    public Response<String> makeStoreManager(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return marketFacade.makeStoreManager(storeID, currentUsername, subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return marketFacade.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return marketFacade.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return marketFacade.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return marketFacade.messageResponse(subscriberUsername, answer);
    }

    public MarketFacade getMarketFacade() {
        return marketFacade;
    }

    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        return marketFacade.closeStore(storeID, currentUsername);
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return marketFacade.sendCloseStoreNotification(subscriberNames, storeID);
    }
}
