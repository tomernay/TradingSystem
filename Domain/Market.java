package Domain;

import java.util.List;

public class Market {
    private MarketFacade marketFacade = new MarketFacade();

    public boolean isStoreOwner(String storeID, String storeOwnerID) {
        return marketFacade.isStoreOwner(storeID, storeOwnerID);
    }

    public boolean makeStoreOwner(String storeID, String subscriberID) {
        return marketFacade.makeStoreOwner(storeID, subscriberID);
    }

    public boolean isStoreManager(String storeID, String storeOwnerID) {
        return marketFacade.isStoreManager(storeID, storeOwnerID);
    }

    public boolean makeStoreManager(String storeID, String subscriberID, List<String> permissions) {
        return marketFacade.makeStoreManager(storeID, subscriberID, permissions);
    }

    public boolean addManagerPermissions(String storeID, String storeManagerID, String permission) {
        return marketFacade.addManagerPermissions(storeID, storeManagerID, permission);
    }

    public boolean removeManagerPermissions(String storeID, String storeManagerID, String permission) {
        return marketFacade.removeManagerPermissions(storeID, storeManagerID, permission);
    }

    public boolean isStoreCreator(String storeID, String storeCreatorID) {
        return marketFacade.isStoreCreator(storeID, storeCreatorID);
    }

    public boolean messageResponse(String subscriberID, boolean answer) {
        return marketFacade.messageResponse(subscriberID, answer);
    }
}
