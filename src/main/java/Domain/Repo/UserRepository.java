package Domain.Repo;

import Domain.Externals.Security.PasswordEncoderUtil;
import Domain.Externals.Security.TokenHandler;
import Domain.Store.Inventory.ProductDTO;
import Domain.Users.Subscriber.Cart.ShoppingCart;
import Utilities.Messages.Message;

import Utilities.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import Utilities.Response;
import Utilities.SystemLogger;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class UserRepository implements JpaRepository<User, Long> {

    private final Map<String, Subscriber> subscribers;
    private final Map<String, Subscriber> subscribersLoggedIn;
    private final Map<String, User> guests;
    private Integer userIDS;

    public UserRepository() {
        subscribers = new HashMap<>();
        subscribersLoggedIn = new HashMap<>();
        guests = new HashMap<>();
        userIDS = 0;
    }

    public Map<String, Subscriber> getSubscribers() {
        return subscribers;
    }

    public Map<String, Subscriber> getSubscribersLoggedIn() {
        return subscribersLoggedIn;
    }

    public Response<List<String>> loginAsGuest() {
        String username = "Guest" + userIDS;
        User user = new User(username);
        userIDS++;
        addGuest(user);
        String token = guests.get(username).generateToken();
        return Response.success("You signed in as a GUEST", List.of(username, token));
    }

    public Response<String> logoutAsGuest(String username) {
        if(guests.get(username) == null) {
            return Response.error("User is already logged out", null);
        }
        if(guests.get(username).logoutAsGuest()){
            guests.remove(username);
            return Response.success("You signed out as a GUEST", null);
        }
        return Response.error("Error - can't signed out as a GUEST", null);
    }

    public Response<String> loginAsSubscriber(String username, String password) {
        if (isUserExist(username)) {
            Subscriber subscriber = getUser(username);
            if (PasswordEncoderUtil.matches(password,subscriber.getPassword())) {
                String token = getUser(username).generateToken();
                subscribersLoggedIn.put(username, subscriber);
                SystemLogger.info("[SUCCESS] User " + username + " logged in successfully");
                return Response.success("Logged in successfully", token);
            } else {
                SystemLogger.error("[ERROR] Incorrect password for user " + username);
                return Response.error("Incorrect password", null);
            }
        } else {
            SystemLogger.error("[ERROR] User " + username + " does not exist");
            return Response.error("User does not exist", null);
        }
    }

    public Response<String> logoutAsSubscriber(String username) {
        if(!subscribersLoggedIn.containsKey(username)) {
            SystemLogger.error("[ERROR] User " + username + " is already logged out");
            return Response.error("User is already logged out", null);
        }
        getUser(username).resetToken();
        subscribersLoggedIn.remove(username);
        SystemLogger.info("[SUCCESS] User " + username + " logged out successfully");
        return Response.success("You signed out as a SUBSCRIBER", null);
    }

    public void addGuest(User user) {
        guests.put(user.getUsername(), user);
    }

//    public Response<String> messageResponse(String subscriberUsername, boolean answer) {
//        return subscribers.get(subscriberUsername).messageResponse(answer);
//    }

    public Response<Integer> sendMessageToUser(String user,Message message){
        return subscribers.get(user).addMessage(message);
    }

    public boolean isUserExist(String username) {
        return subscribers.containsKey(username);
    }

    public void addUser(Subscriber user) {
        subscribers.put(user.getUsername(), user);
    }

    public Subscriber getUser(String username) {
        return subscribers.get(username);
    }

    public Response<String> sendCloseStoreNotification(List<String> subscriberNames, String storeName) {
        for (String subscriberName : subscriberNames) {
            subscribers.get(subscriberName).addMessage(new NormalMessage("Store " + storeName + " has been closed"));
        }
        SystemLogger.info("[SUCCESS] Store " + storeName + " has been closed. Notifications sent to all related subscribers.");
        return Response.success("Notification sent successfully", null);
    }

    public Response<String> sendReopenStoreNotification(List<String> subscriberNames, String storeName) {
        for (String subscriberName : subscriberNames) {
            subscribers.get(subscriberName).addMessage(new NormalMessage("Store " + storeName + " has been re-opened"));
        }
        SystemLogger.info("[SUCCESS] Store " + storeName + " has been reopened. Notifications sent to all related subscribers.");
        return Response.success("Notification sent successfully", null);
    }

    public Response<String> register(String username, String password) {
        /*   if(!isUsernameValid(username) ) {
            SystemLogger.error("[ERROR] Username does not meet the requirements");
            return Response.error("Username does not meet the requirements", null);
        }*/
         if(!isValidPassword(password)) {
            SystemLogger.error("[ERROR] Password does not meet the requirements");
            return Response.error("Password does not meet the requirements", null);
        }
        else if(isUserExist(username)) {
            SystemLogger.error("[ERROR] User " + username + " already exists");
            return  Response.error("User already exists", null);
        }
        else {
            Subscriber subscriber = new Subscriber(username, PasswordEncoderUtil.encode(password));
            addUser(subscriber);
            SystemLogger.info("[SUCCESS] User " + username + " registered successfully");
            return Response.success("User registered successfully", username);
        }
    }


    public boolean isUsernameValid(String username) {
        // Check if the username is null or empty
        if (username == null || username.isEmpty()) {
            return false;
        }
        // Check if the username is less than 3 characters long
        if (username.length() < 3) {
            return false;
        }
        // Check if the username contains only alphanumeric characters
        return username.matches("[A-Za-z0-9]*");
    }

    public static boolean isValidPassword(String password) {
        // Define the password criteria
        int minLength = 8;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        // Check the length of the password
        if (password.length() < minLength) {
            return false;
        }

        // Check each character of the password
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (isSpecialCharacter(c)) {
                hasSpecialChar = true;
            }
        }

        // Return true if all criteria are met
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    // Helper method to check if a character is a special character
    private static boolean isSpecialCharacter(char c) {
        String specialCharacters = "!@#$%^&*()-+";
        return specialCharacters.indexOf(c) >= 0;
    }



    public Response<String> addProductToShoppingCart(Integer storeID, Integer productID, Integer quantity, String userName) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).addProductToShoppingCart(storeID, productID, quantity);
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).addProductToShoppingCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + userName + " does not exist");
        return Response.error("User does not exist", null);
    }


    public Response<String> removeProductFromShoppingCart(String userName, Integer storeID, Integer productID) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).removeProductFromShoppingCart(storeID, productID);
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).removeProductFromShoppingCart(storeID, productID);
        }
        SystemLogger.error("[ERROR] User " + userName + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> updateProductInShoppingCart(Integer storeID, Integer productID, String userName, Integer quantity) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).updateProductInShoppingCart(storeID, productID, quantity);
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).updateProductInShoppingCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + userName + " does not exist");
        return Response.error("User does not exist", null);
    }



    public Response<Map<Integer, Map<Integer, Integer>>> getShoppingCartContents(String userName) {
        if (subscribers.containsKey(userName)) {
            return subscribers.get(userName).getShoppingCartContents();
        }
        else if (guests.containsKey(userName)) {
            return guests.get(userName).getShoppingCartContents();
        }
        SystemLogger.error("[ERROR] User " + userName + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Message> ownerNominationResponse(Integer messageID, String currentUsername, Boolean answer) {
        Response<Message> response = subscribers.get(currentUsername).ownerNominationResponse(messageID, answer);
        if (response.isSuccess()) {
            sendMessageToUser(((nominateOwnerMessage) response.getData()).getNominator(), new NormalMessage("Your request to nominate " + currentUsername + " as a store owner has been " + (answer ? "accepted" : "declined")));
            SystemLogger.info("[SUCCESS] message responded successfully");
            return response;
        }
        return Response.error(response.getMessage(), null);
    }

    public Response<Message> managerNominationResponse(Integer messageID, String currentUsername, Boolean answer) {
        Response<Message> response = subscribers.get(currentUsername).managerNominationResponse(messageID, answer);
        if (response.isSuccess()) {
            sendMessageToUser(((nominateManagerMessage) response.getData()).getNominatorUsername(), new NormalMessage("Your request to nominate " + currentUsername + " as a store manager has been " + (answer ? "accepted" : "declined")));
            SystemLogger.info("[SUCCESS] message responded successfully");
            return response;
        }
        return Response.error(response.getMessage(), null);
    }

    public boolean isValidToken(String token, String currentUsername) {
        return TokenHandler.isValidJWT(token,currentUsername);
    }

    public void addCreatorRole(String creatorUsername, Integer storeID) {
        subscribers.get(creatorUsername).addCreatorRole(storeID);
    }

    public Response<Map<Integer, String>> getStoresRole(String username) {
        if (!subscribers.containsKey(username)) {
            SystemLogger.error("[ERROR] User " + username + " does not exist");
            return Response.error("User does not exist", null);
        }
        return subscribers.get(username).getStoresRole();
    }

    public void removeStoreRole(String subscriberUsername, Integer storeID) {
        if (!subscribers.containsKey(subscriberUsername)) {
            SystemLogger.error("[ERROR] User " + subscriberUsername + " does not exist");
            return;
        }
        subscribers.get(subscriberUsername).removeStoreRole(storeID);
    }

    public List<Message> getMessages(String user) {
        return subscribers.get(user).getMessages();
    }

    public String addNormalMessage(String user, String message) {
        return subscribers.get(user).addMessage(new NormalMessage(message)).getMessage();
    }

    public Response<String> isOwner(String username) {
        Subscriber s = subscribers.get(username);
        if(s == null){
            return Response.error("User does not exist","");
        }
        return s.isOwner();
    }

    public Response<String> isManager(String username) {
        Subscriber s = subscribers.get(username);
        if(s == null){
            return Response.error("User does not exist","");
        }
        return s.isManager();

    }

    public Response<String> isCreator(String username) {
        Subscriber s = subscribers.get(username);
        if(s == null){
            return Response.error("User does not exist","");
        }
        return s.isCreator();
    }

    public Response<String> changePassword(String username, String password, String newPassword) {
        if (isUserExist(username)) {
            Subscriber subscriber = getUser(username);
            if (subscriber.getPassword().equals(password)) {
                if (isValidPassword(newPassword)) {
                    subscriber.setPassword(newPassword);
                    SystemLogger.info("[SUCCESS] Password for user " + username + " changed successfully");
                    return Response.success("Password changed successfully", null);
                } else {
                    SystemLogger.error("[ERROR] New password does not meet the requirements");
                    return Response.error("New password does not meet the requirements", null);
                }
            } else {
                SystemLogger.error("[ERROR] Incorrect password for user " + username);
                return Response.error("Incorrect password", null);
            }
        } else {
            SystemLogger.error("[ERROR] User " + username + " does not exist");
            return Response.error("User does not exist", null);
        }
    }

    public Response<String> changeUsername(String username, String newUsername) {
        if (isUserExist(username)) {
            if (isUsernameValid(newUsername)) {
                Subscriber subscriber = getUser(username);
                subscriber.setUsername(newUsername);
                subscribers.put(newUsername, subscriber);
                SystemLogger.info("subscribers new user " + subscribers.get(newUsername));
                SystemLogger.info("subscribers og user" + subscribers.get(username));
                SystemLogger.info("subscriber curr " + subscriber.getUsername());
                SystemLogger.info("username" + username);
                SystemLogger.info("newUsername" + newUsername);
                subscribers.remove(username);
                SystemLogger.info("username" + username);
                SystemLogger.info("newUsername" + newUsername);
                //generate the new token
                String token = subscriber.generateToken();

                SystemLogger.info("[SUCCESS] Username for user " + username + " changed successfully to " + newUsername);
                return Response.success("Username changed successfully", token);
            } else {
                SystemLogger.error("[ERROR] New username does not meet the requirements");
                return Response.error("New username does not meet the requirements", null);
            }
        } else {
            SystemLogger.error("[ERROR] User " + username + " does not exist");
            return Response.error("User does not exist", null);
        }
    }



    public Response<String> ResetCartAfterPurchase(String username) {
        if (subscribers.containsKey(username)) {
            subscribers.get(username).setShoppingCart(new ShoppingCart());
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " released successfully");
            return Response.success("Shopping cart released successfully", null);
        }
        else if (guests.containsKey(username)) {
            guests.get(username).setShoppingCart(new ShoppingCart());
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " released successfully");
            return Response.success("Shopping cart released successfully", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> lockFlagShoppingCart(String username) {
        if (subscribers.containsKey(username)) {
            return subscribers.get(username).lockFlagShoppingCart();
        }
        else if (guests.containsKey(username)) {
            return guests.get(username).lockFlagShoppingCart();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> unlockFlagShoppingCart(String username) {
        if (subscribers.containsKey(username)) {
            subscribers.get(username).unlockFlagShoppingCart();
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " unlocked successfully");
            return Response.success("Shopping cart unlocked successfully", null);
        }
        else if (guests.containsKey(username)) {
            guests.get(username).unlockFlagShoppingCart();
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " unlocked successfully");
            return Response.success("Shopping cart unlocked successfully", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Boolean> isFlagLock(String username) {
        if (subscribers.containsKey(username)) {
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " is locked");
            return Response.success("Shopping cart is locked", subscribers.get(username).isFlagLock());
        }
        else if (guests.containsKey(username)) {
            SystemLogger.info("[SUCCESS] Shopping cart for user " + username + " is locked");
            return Response.success("Shopping cart is locked", guests.get(username).isFlagLock());
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> clearCart(String usernameFromCookies) {
        if (subscribers.containsKey(usernameFromCookies)) {
            return subscribers.get(usernameFromCookies).clearCart();
        }
        else if (guests.containsKey(usernameFromCookies)) {
            return guests.get(usernameFromCookies).clearCart();
        }
        SystemLogger.error("[ERROR] User " + usernameFromCookies + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<String> updateProductQuantityInCart(Integer storeId, Integer productId, Integer quantity, String username) {
        if (subscribers.containsKey(username)) {
            return subscribers.get(username).updateProductQuantityInCart(storeId, productId, quantity);
        }
        else if (guests.containsKey(username)) {
            return guests.get(username).updateProductQuantityInCart(storeId, productId, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Map<String, User> getGuests() {
        return guests;
    }

    public CompletableFuture<String> startPurchaseTimer(String username) {
        if (subscribers.containsKey(username)) {
            return subscribers.get(username).startPurchaseTimer();
        }
        else if (guests.containsKey(username)) {
            return guests.get(username).startPurchaseTimer();
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return null;
    }

    public void interruptPurchaseTimer(String username) {
        if (subscribers.containsKey(username)) {
            subscribers.get(username).interruptPurchaseTimer();
        }
        else if (guests.containsKey(username)) {
            guests.get(username).interruptPurchaseTimer();
        }
        else {
            SystemLogger.error("[ERROR] User " + username + " does not exist");
        }
    }

    public boolean isInPurchaseProcess(String user) {
        if (subscribers.containsKey(user)) {
            return subscribers.get(user).isInPurchaseProcess();
        }
        else if (guests.containsKey(user)) {
            return guests.get(user).isInPurchaseProcess();
        }
        SystemLogger.error("[ERROR] User " + user + " does not exist");
        return false;
    }

    public Response<String> removeMessage(String username, String token, Integer messageID) {
        if (subscribers.containsKey(username)) {
            if (TokenHandler.isValidJWT(token,username)) {
                return subscribers.get(username).removeMessage(messageID);
            }
            SystemLogger.error("[ERROR] Invalid token for user " + username);
            return Response.error("Invalid token", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    public Response<Integer> getUnreadMessagesCount(String username, String token) {
        if (subscribers.containsKey(username)) {
            if (TokenHandler.isValidJWT(token,username)) {
                return subscribers.get(username).getUnreadMessagesCount();
            }
            SystemLogger.error("[ERROR] Invalid token for user " + username);
            return Response.error("Invalid token", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not exist");
        return Response.error("User does not exist", null);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public User getOne(Long aLong) {
        return null;
    }

    @Override
    public User getById(Long aLong) {
        return null;
    }

    @Override
    public User getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends User> S save(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public List<User> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<User> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }

    public Response<Set<String>> getAllSubscribers(String username) {
        return Response.success("All subscribers",subscribers.keySet());
    }
}