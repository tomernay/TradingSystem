package Service;

import Domain.Externals.Security.Security;
import Domain.Market.Market;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;

public class StoreService {
    Market market;
    public StoreService(Market market){
        this.market=market;
    }

    /**
     * add store to market by creator
     * @param name
     * @param creator
     * @param token
     */
    public void addStore(String name, String creator,String token){
        if(Security.isValidJWT(creator,token)) {
          if(  market.getMarketFacade().getUserRepository().isUserExist(creator) ){
                market.getMarketFacade().getStoreRepository().addStore(name, creator);
            }
        }
    }

    public Store getStore(String name){
        return market.getMarketFacade().getStoreRepository().getStore(name);
    }


    public boolean isStoreOwner(String storeID, String storeOwnerID) {
        return market.isStoreOwner(storeID, storeOwnerID);
    }

    public boolean isStoreManager(String storeID, String storeOwnerID) {
        return market.isStoreManager(storeID, storeOwnerID);
    }

    public boolean isStoreCreator(String storeID, String storeCreatorID) {
        return market.isStoreCreator(storeID, storeCreatorID);
    }
}
