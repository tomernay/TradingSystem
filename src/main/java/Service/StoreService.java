package src.main.java.Service;

import src.main.java.Domain.Externals.Security.Security;

import src.main.java.Domain.Market.Market;
import src.main.java.Domain.Store.Store;
import src.main.java.Utilities.Response;
import src.main.java.Utilities.SystemLogger;

import java.util.List;

public class StoreService {
  Market market;
    public StoreService(Market market){
        this.market=market;
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

    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        return market.closeStore(storeID, currentUsername);
    }

    public Market getMarket() {
        return market;
    }
}
