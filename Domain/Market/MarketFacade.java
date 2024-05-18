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

    public Response<String> loginAsGuest(User user){
        return userRepository.loginAsGuest(user);
    }

    public Response<String> logoutAsGuest(User user) {
        return userRepository.logoutAsGuest(user);
    }

    public boolean isStoreOwner(String storeID, String currentUsername) {
        return storeRepository.isStoreOwner(storeID, currentUsername);
    }

    public Response<String> makeStoreOwner(String storeID, String currentUsername, String subscriberUsername) {
        Response<Message> message = makeNominateOwnerMessage(storeID, currentUsername, subscriberUsername);
        if (!message.isSuccess()) {
            return Response.error(message.getMessage(), null);
        }
        return userRepository.makeStoreOwner(subscriberUsername, message.getData());
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

    public Response<String> makeStoreManager(String storeID, String currentUsername, String subscriberUsername, List<String> permissions) {
        Response<Message> message = makeNominateManagerMessage(storeID, currentUsername, subscriberUsername, permissions);
        if (!message.isSuccess()) {
            return Response.error(message.getMessage(), null);
        }
        return userRepository.makeStoreManager(subscriberUsername, message.getData());
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

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        return userRepository.messageResponse(subscriberUsername, answer);
    }

    public Response<String> openStore(String storeID, String creator) {
        return storeRepository.addStore(storeID, creator);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }

    public Response<List<String>> closeStore(String storeID, String currentUsername) {
        return storeRepository.closeStore(storeID, currentUsername);
    }

//    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
//        return userRepository.sendCloseStoreNotification(subscriberNames, storeID);
//    }
}
