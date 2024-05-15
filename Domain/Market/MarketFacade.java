package Domain.Market;

import Domain.Repo.StoreRepository;
import Domain.Repo.UserRepository;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;


import java.util.List;

public class MarketFacade {
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    public MarketFacade() {
        this.userRepository = new UserRepository();
        this.storeRepository = new StoreRepository();
    }


    public boolean isStoreOwner(String storeID, String storeCreatorID) {
        return storeRepository.isStoreOwner(storeID, storeCreatorID);
    }

    public Response<String> makeStoreOwner(String storeID, String subscriberID) {
        Message message = storeRepository.makeNominateOwnerMessage(storeID, subscriberID);
        return userRepository.makeStoreOwner(subscriberID, message);
    }

    public boolean isStoreManager(String storeID, String storeOwnerID) {
        return storeRepository.isStoreManager(storeID, storeOwnerID);
    }

    public boolean makeStoreManager(String storeID, String subscriberID, List<String> permissions) {
        Message message = storeRepository.makeNominateManagerMessage(storeID, subscriberID, permissions);
        return userRepository.makeStoreManager(subscriberID, message);
    }

    public boolean addManagerPermissions(String storeID, String storeManagerID, String permission) {
        return storeRepository.addManagerPermissions(storeID, storeManagerID, permission);
    }

    public boolean removeManagerPermissions(String storeID, String storeManagerID, String permission) {
        return storeRepository.removeManagerPermissions(storeID, storeManagerID, permission);
    }

    public boolean isStoreCreator(String storeID, String storeCreatorID) {
        return storeRepository.isStoreCreator(storeID, storeCreatorID);
    }

    public boolean messageResponse(String subscriberID, boolean answer) {
        return userRepository.messageResponse(subscriberID, answer);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }
}
