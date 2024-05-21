package src.main.java.Domain.Market;




import src.main.java.Domain.Repo.OrderRepository;
import src.main.java.Domain.Repo.StoreRepository;
import src.main.java.Domain.Repo.UserRepository;
import src.main.java.Domain.Store.StoreData.Permissions;
import src.main.java.Domain.Users.StateOfSubscriber.SubscriberState;
import src.main.java.Domain.Users.Subscriber.Messages.Message;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import src.main.java.Domain.Users.User;
import src.main.java.Utilities.Response;

import java.util.List;
import java.util.Map;

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

    public Response<String> loginAsSubscriber(Subscriber subscriber){
        return userRepository.loginAsSubscriber(subscriber);
    }
    public Response<String> logoutAsSubscriber(Subscriber subscriber){
        return userRepository.logoutAsSubscriber(subscriber);
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

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return userRepository.sendCloseStoreNotification(subscriberNames, storeID);
    }

    public Response<String> register(String username, String password) {
        return userRepository.register(username, password);
    }

    public Response<Map<String, SubscriberState>> requestEmployeesStatus(String storeID){
        return storeRepository.requestEmployeesStatus(storeID);
    }

    public Response<Map<String, List<Permissions>>> requestManagersPermissions(String storeID){
        return storeRepository.requestManagersPermissions(storeID);
    }

    public Response<String> addProductToShoppingCart(String storeID,String productID,String userName,int quantity){
            return userRepository.addProductToShoppingCart(storeID, productID, userName, quantity);
    }

    public Response<String> removeProductFromShoppingCart(String userName,String storeID, String productID) {
        return userRepository.removeProductFromShoppingCart(userName,storeID, productID);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, String userName, int quantity) {
        return userRepository.updateProductInShoppingCart(storeID, productID, userName, quantity);
    }



    public Response<String> getShoppingCartContents(String userName) {
        Map<String, Map<String, Integer>> shoppingCartContents = userRepository.getShoppingCartContents(userName).getData();
        if (shoppingCartContents == null) {
            return Response.error("Error - can't get shopping cart contents", null);
        } else {
            StringBuilder cartContents = new StringBuilder();
            for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCartContents.entrySet()) {
                String storeName = storeEntry.getKey();
                Map<String, Integer> products = storeEntry.getValue();

                cartContents.append("Store: ").append(storeName).append("\n");

                for (Map.Entry<String, Integer> productEntry : products.entrySet()) {
                    String productName = productEntry.getKey();
                    int quantity = productEntry.getValue();

                    cartContents.append("Product: ").append(productName)
                            .append(", Quantity: ").append(quantity)
                            .append("\n");
                }
                cartContents.append("\n");
            }
            return Response.success("get ShoppingCart Contents successfully",cartContents.toString());
        }
    }

}
