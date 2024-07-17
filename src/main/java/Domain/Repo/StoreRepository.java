package Domain.Repo;

import Domain.Store.Store;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Domain.Users.Subscriber.Subscriber;
import Presentation.application.View.UtilitiesView.Broadcaster;
import Utilities.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class StoreRepository {
    @Autowired
    private IStoreRepository iStoreRepository;
    private final Map<Integer, Store> stores;
    private final Map<Integer, Store> deactivatedStores; // <StoreID, Store>
    private final AtomicInteger storeID = new AtomicInteger(0);
    @Autowired
    private UserRepository userRepository;


    public StoreRepository() {
        this.stores = new HashMap<>();
        this.deactivatedStores = new HashMap<>();
    }

    public Integer getStoreID() {
        return storeID.getAndIncrement();
    }


    public void addActiveStore(Store store) {
        iStoreRepository.save(store);
    }

    public void addDeactivatedStore(Store store) {
        deactivatedStores.put(store.getId(), store);
        for(String name : store.getSubscribers().keySet())
            Broadcaster.broadcast(store.getName()+" has been deactivated",name);
    }

    public void removeActiveStore(Integer storeId) {
        iStoreRepository.deleteById(storeId);
    }

    public void removeDeactivatedStore(Integer storeId) {
        iStoreRepository.deleteById(storeId);
    }

    public Store getActiveStore(Integer storeID) {
        Store s= iStoreRepository.findById(storeID).get();
        System.out.println(s);
        return s;
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

    public synchronized Boolean isStoreExist(Integer storeID) {
        return iStoreRepository.existsById(storeID);
    }

    @Transactional
    public List<Store> getActiveStores() {
        return iStoreRepository.findAll();
    }

    public Map<Integer, Store> getDeactivatedStores() {
        return deactivatedStores;
    }

    public Response< Set<String>>
    getStoreCreator(Integer storeName) {
        Map<String, SubscriberState> subscriberStateMap=stores.get(storeName).getSubscribers();

            return new Response<Set<String>>(true,"",subscriberStateMap.keySet() );

    }
}
