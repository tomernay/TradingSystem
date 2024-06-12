package Facades;

import Domain.Repo.UserRepository;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.List;
import java.util.Map;

public class UserFacade {
    private UserRepository userRepository;

    public UserFacade() {
        userRepository = new UserRepository();
    }


    public Response<List<String>> loginAsGuest(){
        return userRepository.loginAsGuest();
    }

    public Response<String> logoutAsGuest(String username) {
        return userRepository.logoutAsGuest(username);
    }

    public Response<String> loginAsSubscriber(String username, String password){
        return userRepository.loginAsSubscriber(username, password);
    }

    public Response<String> logoutAsSubscriber(String username){
        return userRepository.logoutAsSubscriber(username);
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



    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
        Response<Message> message = userRepository.messageResponse(subscriberUsername, answer);
        if (message.isSuccess()) {
            SystemLogger.info("[SUCCESS] message responded successfully");
            return Response.success(message.getMessage(), null);
        }
        return Response.error(message.getMessage(), null);
    }

    public Response<Message> ownerNominationResponse(String currentUsername, boolean answer) {
        Response<Message> message =  userRepository.ownerNominationResponse(currentUsername, answer);
        userRepository.sendMessageToUser(((nominateOwnerMessage) message.getData()).getNominator(), new NormalMessage("Your request to nominate " + currentUsername + " as a store owner has been " + (answer ? "accepted" : "declined")));
        if (message.isSuccess()) {
            SystemLogger.info("[SUCCESS] message responded successfully");
            return message;
        }
        return Response.error(message.getMessage(), null);
    }

    public Response<Message> managerNominationResponse(String currentUsername, boolean answer) {
        Response<Message> message = userRepository.managerNominationResponse(currentUsername, answer);
        userRepository.sendMessageToUser(((nominateManagerMessage) message.getData()).getNominatorUsername(), new NormalMessage("Your request to nominate " + currentUsername + " as a store manager has been " + (answer ? "accepted" : "declined")));
        if (message.isSuccess()) {
            SystemLogger.info("[SUCCESS] message responded successfully");
            return message;
        }
        return Response.error(message.getMessage(), null);
    }

    public boolean userExist(String subscriberUsername) {
        return userRepository.isUserExist(subscriberUsername);
    }

    public Response<Map<String, Map<String, Integer>>> getShoppingCartContents(String userName) {
        return userRepository.getShoppingCartContents(userName);
    }



    public Response<String> sendMessageToUser(String username, Message Message) {
        return userRepository.sendMessageToUser(username, Message);
    }

    public boolean isValidToken(String token, String currentUsername) {
        return userRepository.isValidToken(token, currentUsername);
    }

    public void addCreatorRole(String creatorUsername, String storeID) {
        userRepository.addCreatorRole(creatorUsername, storeID);
    }

    public Response<Map<String, String>> getStoresRole(String username) {
        return userRepository.getStoresRole(username);
    }

    public void removeStoreRole(String subscriberUsername, String storeID) {
        userRepository.removeStoreRole(subscriberUsername, storeID);
    }

    public Response<String> isOwner(String username) {
        return userRepository.isOwner(username);
    }

    public Response<String> isManager(String username) {
        return userRepository.isManager(username);
    }

    public Response<String> isCreator(String username) {
        return userRepository.isCreator(username);
    }

    public Response<String> changePassword( String username ,String password, String confirmPassword){
        return userRepository.changePassword(username ,password, confirmPassword);
    }

    public Response<String> changeUsername(String username, String newUsername){
        return userRepository.changeUsername(username, newUsername);
    }




    public Response<String> ReleaseShoppSingCartForUser(String username) {
        return userRepository.ReleaseShoppSingCartForUser(username);
    }

    public Response<String> lockFlagShoppingCart(String username) {
        return userRepository.lockFlagShoppingCart(username);
    }

    public Response<String> unlockFlagShoppingCart(String username) {
        return userRepository.unlockFlagShoppingCart(username);
    }

    public Response<Boolean> isFlagLock(String username) {
        return userRepository.isFlagLock(username);
    }
}