package Domain.Market;

import Domain.Repo.StoreRepository;
import Domain.Store.Store;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Utilities.Messages.Message;
import Utilities.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoreFacade {
    private StoreRepository storeRepository;

    public StoreFacade() {
        storeRepository = new StoreRepository();
    }


    public boolean isStoreManager(String storeID, String currentUsername) {
        return storeRepository.isStoreManager(storeID, currentUsername);
    }

    public Response<Message> makeNominateOwnerMessage(String storeID, String currentUsername, String subscriberUsername) {
        return storeRepository.makeNominateOwnerMessage(storeID, currentUsername, subscriberUsername);
    }

    public Response<Message> makeNominateManagerMessage(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        return storeRepository.makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
    }

    public Response<String> addManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission ) {
        return storeRepository.addManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String currentUsername, String subscriberUsername, String permission) {
        return storeRepository.removeManagerPermissions(storeID, currentUsername, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return storeRepository.isStoreCreator(storeID, currentUsername);
    }

    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        return storeRepository.closeStore(storeID, currentUsername);
    }

    public Response<Map<String, String>> requestEmployeesStatus(String storeID){
        return storeRepository.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<String>>> requestManagersPermissions(String storeID){
        return storeRepository.requestManagersPermissions(storeID);
    }

    public Store getStore(String storeID) {
        return storeRepository.getStore(storeID);
    }

    public Response<String> openStore(String storeID, String creator) {
        return storeRepository.addStore(storeID, creator);
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return storeRepository.isStoreOwner(storeID, currentUsername);
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }


    public Response<String> nominateOwner(String storeID, String currentUsername, String nominatorUsername) {
        return storeRepository.nominateOwner(storeID, currentUsername, nominatorUsername);
    }

    public Response<String> removeStoreSubscription(String storeID, String currentUsername) {
        return storeRepository.removeStoreSubscription(storeID, currentUsername);
    }

    public Response<String> nominateManager(String storeID, String currentUsername, List<String> permissions, String nominatorUsername) {
        return storeRepository.nominateManager(storeID, currentUsername, permissions, nominatorUsername);
    }

    public Response<Set<String>> waiveOwnership(String storeID, String currentUsername) {
        return storeRepository.waiveOwnership(storeID, currentUsername);
    }

    public boolean storeExists(String storeID) {
        return storeRepository.storeExists(storeID);
    }
}
