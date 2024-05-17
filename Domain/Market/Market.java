package Domain.Market;

import Utilities.Response;

import java.util.List;

public class Market {
    private  MarketFacade marketFacade = new MarketFacade();

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return marketFacade.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> loginAsGuest() {
        return marketFacade.loginAsGuest();
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

    public MarketFacade getMarketFacade() {
        return marketFacade;
    }
}
