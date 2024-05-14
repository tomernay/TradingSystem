package Domain;

import java.util.List;

public class MarketFacade {
    private UserRepository userRepository;


    public boolean isStoreOwner(String storeID, String storeCreatorID) {
        return userRepository.isStoreOwner(storeID, storeCreatorID);
    }

    public boolean makeStoreOwner(String storeID, String subscriberID) {
        return userRepository.makeStoreOwner(storeID, subscriberID);
    }

    public boolean isStoreManager(String storeID, String storeOwnerID) {
        return userRepository.isStoreManager(storeID, storeOwnerID);
    }

    public boolean makeStoreManager(String storeID, String subscriberID, List<String> permissions) {
        return userRepository.makeStoreManager(storeID, subscriberID, permissions);
    }

    public boolean addManagerPermissions(String storeID, String storeManagerID, String permission) {
        return userRepository.addManagerPermissions(storeID, storeManagerID, permission);
    }

    public boolean removeManagerPermissions(String storeID, String storeManagerID, String permission) {
        return userRepository.removeManagerPermissions(storeID, storeManagerID, permission);
    }

    public boolean isStoreCreator(String storeID, String storeCreatorID) {
        return userRepository.isStoreCreator(storeID, storeCreatorID);
    }
}
