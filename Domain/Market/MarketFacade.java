package Domain.Market;

import Domain.Repo.OrderRepository;
import Domain.Repo.StoreRepository;
import Domain.Repo.UserRepository;
import Domain.Users.Subscriber.Messages.Message;
import Domain.Users.User;
import Utilities.Response;


import java.util.List;

public class MarketFacade {
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private OrderRepository orderRepository;

    public MarketFacade() {
        this.userRepository = new UserRepository();
        this.storeRepository = new StoreRepository();
        this.orderRepository = new OrderRepository();
    }

    public Response<String> loginAsGuest() {
        return User.loginAsGuest();
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return storeRepository.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> makeStoreOwner(String storeID, String subscriberUsername) {
        Message message = storeRepository.makeNominateOwnerMessage(storeID, subscriberUsername);
        return userRepository.makeStoreOwner(subscriberUsername, message);
    }

    public boolean isStoreManager(String storeID, String currentUsername) {
        return storeRepository.isStoreManager(storeID, currentUsername);
    }

    public Response<String> makeStoreManager(String storeID, String subscriberUsername, List<String> permissions) {
        Message message = storeRepository.makeNominateManagerMessage(storeID, subscriberUsername, permissions);
        return userRepository.makeStoreManager(subscriberUsername, message);
    }

    public Response<String> addManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return storeRepository.addManagerPermissions(storeID, subscriberUsername, permission);
    }

    public Response<String> removeManagerPermissions(String storeID, String subscriberUsername, String permission) {
        return storeRepository.removeManagerPermissions(storeID, subscriberUsername, permission);
    }

    public boolean isStoreCreator(String storeID, String currentUsername) {
        return storeRepository.isStoreCreator(storeID, currentUsername);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return userRepository.messageResponse(subscriberUsername, answer);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }
}
