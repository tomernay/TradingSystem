package Domain.Market;

import Domain.Externals.Security.Security;
import Domain.Repo.UserRepository;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public class UserFacade {
    private UserRepository userRepository;

    public UserFacade() {
        userRepository = new UserRepository();
    }


    public Response<String> loginAsGuest(){
        return userRepository.loginAsGuest();
    }

    public Response<String> logoutAsGuest(String username) {
        return userRepository.logoutAsGuest(username);
    }

    public Response<String> loginAsSubscriber(Subscriber subscriber){
        return userRepository.loginAsSubscriber(subscriber);
    }

    public Response<String> logoutAsSubscriber(Subscriber subscriber){
        return userRepository.logoutAsSubscriber(subscriber);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeID) {
        return userRepository.sendCloseStoreNotification(subscriberNames, storeID);
    }

    public Response<String> register(String username, String password) {
        return userRepository.register(username, password);
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

    public boolean userExists(String subscriberUsername) {
        return userRepository.isUserExist(subscriberUsername);
    }

    public Subscriber getUser(String subscriberUsername) {
        return userRepository.getUser(subscriberUsername);
    }

    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        Response<Message> message = userRepository.messageResponse(subscriberUsername, answer);
        if (message.getData() instanceof nominateOwnerMessage) {
            userRepository.sendMessageToUser(((nominateOwnerMessage) message.getData()).getNominator(), new NormalMessage("Your request to nominate " + subscriberUsername + " as a store owner has been " + (answer ? "accepted" : "declined")));
        }
        if (message.getData() instanceof nominateManagerMessage) {
            userRepository.sendMessageToUser(((nominateManagerMessage) message.getData()).getNominatorUsername(), new NormalMessage("Your request to nominate " + subscriberUsername + " as a store manager has been " + (answer ? "accepted" : "declined")));
        }
        if (message.isSuccess()) {
            return Response.success(message.getMessage(), null);
        }
        return Response.error(message.getMessage(), null);
    }

    public Response<Message> ownerNominationResponse(String currentUsername, boolean answer) {
        return userRepository.ownerNominationResponse(currentUsername, answer);
    }

    public Response<Message> managerNominationResponse(String currentUsername, boolean answer) {
        return userRepository.managerNominationResponse(currentUsername, answer);
    }

    public boolean userExist(String subscriberUsername) {
        return userRepository.isUserExist(subscriberUsername);
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

    public Response<String> purchaseShoppingCart(String userName) {
//        Map<String, Map<String, Integer>> shoppingCartContents = userRepository.getShoppingCartContents(userName).getData();
//        Response lockResponse = storeRepository.tryLockShoppingCart(shoppingCartContents);
//        if(lockResponse.isSuccess()){
//            Response payResponse = paymentRepository.userPayment(userName,lockResponse.getData());
//
//            if(payResponse.isSuccess()) {
//                for (Map.Entry<String, Map<String, Integer>> storeEntry : shoppingCartContents.entrySet()) {
//                    String storeName = storeEntry.getKey();
//                    orderRepository.addOrder(userName, storeName, lockResponse.getData());
//                }
//            }
//            else{
//                return Response.error("Error - can't purchase shopping cart", null);
//            }
//        return lockResponse;
//        }
        return Response.error("NOT IMPLEMENETED", null);

    }

    public void sendMessageToUser(String username, Message Message) {
        userRepository.sendMessageToUser(username, Message);
    }

    public boolean isValidToken(String token, String currentUsername) {
        return userRepository.isValidToken(token, currentUsername);
    }
}
