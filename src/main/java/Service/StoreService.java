package Service;

import Domain.Externals.Security.Security;

import Domain.Market.Market;
import Domain.Store.Store;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.List;

public class StoreService  {
  Market market;
    public StoreService(){
        this.market = Market.getInstance();
    }

//    /**
//     * add store to market by creator
//     * @param name
//     * @param creator
//     * @param token
//     */

    public Response<String> addStore(String name, String creator, String token) {
        SystemLogger.info("[START] User: " + creator + " is trying to create a store with name: " + name);
        if(Security.isValidJWT(token,creator)) {
            return market.openStore(name,creator);
        }
        SystemLogger.error("[ERROR] User: " + creator + " tried to add a store but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Store getStore(String storeID){
        return market.getMarketFacade().getStoreRepository().getStore(storeID);
    }


    public boolean isStoreOwner(String storeID, String currentUsername) {
        return market.isStoreOwner(storeID, currentUsername);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return market.isStoreManager(storeID, currentUsername);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return market.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> closeStore(String storeID, String currentUsername, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to close store: " + storeID);
        if(Security.isValidJWT(token,currentUsername)) {
            Response<List<String>> storeCloseResponse = market.closeStore(storeID, currentUsername);
            if (!storeCloseResponse.isSuccess()) {
                return Response.error(storeCloseResponse.getMessage(), null);
            }
            return market.sendCloseStoreNotification(storeCloseResponse.getData(), storeID);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to close store: " + storeID + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to add permissions: " + permission + " to " + subscriberUsername);
        if(Security.isValidJWT(token,currentUsername)) {
            if (!market.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return market.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to add permissions: " + permission + " to " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission, String token) {
        SystemLogger.info("[START] User: " + currentUsername + " is trying to remove permissions: " + permission + " from " + subscriberUsername);
        if(Security.isValidJWT(token,currentUsername)) {
            if (!market.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] User: " + subscriberUsername + " does not exist");
                return Response.error("User: " + subscriberUsername + " does not exist", null);
            }
            return market.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
        }
        SystemLogger.error("[ERROR] User: " + currentUsername + " tried to remove permissions: " + permission + " from " + subscriberUsername + " but the token was invalid");
        return Response.error("Invalid token",null);
    }

    public Market getMarket() {
        return market;
    }

    public Response<String> setQuantity(int productID, int quantity, String storeID, String UserName, String token) {
        SystemLogger.info("[START] User: " + UserName + " is trying to set quantity of product: " + productID + " to: " + quantity);
        if(Security.isValidJWT(token,UserName)){
            return market.setQuantity(productID, quantity, storeID, UserName);
        }
        SystemLogger.error("[ERROR] User: " + UserName + " tried to set quantity of product: " + productID + " to: " + quantity + " but the token was invalid");
        return Response.error("Invalid token",null);
    }
}
