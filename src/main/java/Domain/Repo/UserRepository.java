package Domain.Repo;

import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;

@Repository
public class UserRepository {

    private final Map<String, Subscriber> subscribers;
    private final List<String> subscribersLoggedIn;
    private final Map<String, User> guests;
    private Integer userIDS; //Counter for guest ID

    public UserRepository() {
        subscribers = new HashMap<>();
        subscribersLoggedIn = new ArrayList<>();
        guests = new HashMap<>();
        userIDS = 0;
    }

    public User getGuest(String username) {
        if (guests.containsKey(username)) {
            return guests.get(username);
        }
        return null;
    }

    public Subscriber getSubscriber(String username) {
        if (subscribers.containsKey(username)) {
            return subscribers.get(username);
        }
        return null;
    }

    public Map<String, Subscriber> getSubscribers() {
        return subscribers;
    }

    public List<String> getSubscribersLoggedIn() {
        return subscribersLoggedIn;
    }

    public Integer getIdCounter() {
        return userIDS;
    }

    public void increaseIdCounter() {
        userIDS++;
    }

    public Boolean isUserExist(String username) {
        return subscribers.containsKey(username);
    }

    public Boolean addSubscriber(Subscriber subscriber) {
        if (subscribers.containsKey(subscriber.getUsername())) {
            return false;
        }
        subscribers.put(subscriber.getUsername(), subscriber);
        return true;
    }

    public Boolean removeSubscriber(String username) {
        if (subscribers.containsKey(username)) {
            subscribers.remove(username);
            return true;
        }
        return false;
    }

    public Boolean addGuest(User guest) {
        if (guests.containsKey(guest.getUsername())) {
            return false;
        }
        guests.put(guest.getUsername(), guest);
        return true;
    }

    public void removeGuest(String username) {
        guests.remove(username);
    }

    public Boolean addLoggedIn(String username) {
        if (subscribersLoggedIn.contains(username)) {
            return false;
        }
        subscribersLoggedIn.add(username);
        return true;
    }

    public Boolean isUserLoggedIn(String username) {
        return subscribersLoggedIn.contains(username);
    }

    public Boolean removeLoggedIn(String username) {
        if (subscribersLoggedIn.contains(username)) {
            subscribersLoggedIn.remove(username);
            return true;
        }
        return false;
    }

    public Map<String, User> getGuests() {
        return guests;
    }


    public Response<Set<String>> getAllSubscribersUsernames() {
        return Response.success("All subscribers",subscribers.keySet());
    }
}