package Domain.Repo;

import Domain.Store.Store;
import Utilities.Response;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreRepository {
    private final Map<Integer, Store> stores;
    private final Map<Integer, Store> deactivatedStores; // <StoreID, Store>
    private final AtomicInteger storeID = new AtomicInteger(0);


    public StoreRepository() {
        this.stores = new HashMap<>();
        this.deactivatedStores = new HashMap<>();
    }

    public Integer getStoreID() {
        return storeID.getAndIncrement();
    }

    public void addActiveStore(Store store) {
        stores.put(store.getId(), store);
    }

    public void addDeactivatedStore(Store store) {
        deactivatedStores.put(store.getId(), store);
    }

    public Boolean removeActiveStore(Integer storeId) {
        if (!stores.containsKey(storeId)) {
            return false;
        }
        stores.remove(storeId);
        return true;
    }

    public Boolean removeDeactivatedStore(Integer storeId) {
        if (!deactivatedStores.containsKey(storeId)) {
            return false;
        }
        deactivatedStores.remove(storeId);
        return true;
    }

    public Store getActiveStore(Integer storeID) {
        return stores.get(storeID);
    }

    public Store getDeactivatedStore(Integer storeID) {
        return deactivatedStores.get(storeID);
    }

    public Boolean isClosedStore(Integer storeID) {
        return deactivatedStores.containsKey(storeID);
    }

    public Boolean isOpenedStore(Integer storeID) {
        return stores.containsKey(storeID);
    }

    public synchronized Response<String> isStoreExist(Integer storeID) {
        if (!stores.containsKey(storeID)) {
            if (deactivatedStores.containsKey(storeID)) {
                return Response.success("Store with ID: " + storeID + " is deactivated", null);
            }
            return Response.error("Store with ID: " + storeID + " doesn't exist", null);
        }
        return Response.success("Store with ID: " + storeID + " is active", null);
    }

    public Map<Integer, Store> getActiveStores() {
        return stores;
    }

    public Map<Integer, Store> getDeactivatedStores() {
        return deactivatedStores;
    }
}
