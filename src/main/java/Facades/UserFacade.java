package Facades;

import Domain.Repo.UserRepository;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Messages.Message;
import Utilities.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UserFacade {
    private final UserRepository userRepository;

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
    public Response<String> sendReopenStoreNotification(List<String> subscriberNames, String storeID) {
        return userRepository.sendReopenStoreNotification(subscriberNames, storeID);
    }

    public Response<String> register(String username, String password) {
        return userRepository.register(username, password);
    }

    public Response<String> addProductToShoppingCart(String storeID,ProductDTO product,String userName){
        return userRepository.addProductToShoppingCart(storeID, product, userName);
    }

    public Response<String> removeProductFromShoppingCart(String userName,String storeID, String productID) {
        return userRepository.removeProductFromShoppingCart(userName,storeID, productID);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, String userName, int quantity) {
        return userRepository.updateProductInShoppingCart(storeID, productID, userName, quantity);
    }

//    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
//        return userRepository.messageResponse(subscriberUsername, answer);
//    }

    public Response<Message> ownerNominationResponse(String messageID, String currentUsername, boolean answer) {
        return userRepository.ownerNominationResponse(messageID, currentUsername, answer);
    }

    public Response<Message> managerNominationResponse(String messageID, String currentUsername, boolean answer) {
        return userRepository.managerNominationResponse(messageID, currentUsername, answer);
    }

    public boolean userExist(String subscriberUsername) {
        return userRepository.isUserExist(subscriberUsername);
    }

    public Response<Map<String, List<ProductDTO>>> getShoppingCartContents(String userName) {
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

    public Response<String> ResetCartAfterPurchase(String username) {
        return userRepository.ResetCartAfterPurchase(username);
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

    public Response<String> clearCart(String usernameFromCookies) {
        return userRepository.clearCart(usernameFromCookies);
    }

    public Response<String> checkout(String usernameFromCookies) {
        return userRepository.checkout(usernameFromCookies);
    }

    public Response<String> updateProductQuantityInCart(String storeId, String productId, Integer quantity, String username) {
        return userRepository.updateProductQuantityInCart(storeId, productId, quantity, username);
    }

    public CompletableFuture<String> startPurchaseTimer(String username) {
        return userRepository.startPurchaseTimer(username);
    }

    public void interruptPurchaseTimer(String username) {
        userRepository.interruptPurchaseTimer(username);
    }

    public Response<Map<String, List<ProductDTO>>> lockAndGetShoppingCartContents(String username) {
        return userRepository.lockAndGetShoppingCartContents(username);
    }

    public boolean isInPurchaseProcess(String user) {
        return userRepository.isInPurchaseProcess(user);
    }

    public Response<String> removeMessage(String username, String token, String messageID) {
        return userRepository.removeMessage(username, token, messageID);
    }

    public Response<Integer> getUnreadMessagesCount(String username, String token) {
        return userRepository.getUnreadMessagesCount(username, token);
    }
}