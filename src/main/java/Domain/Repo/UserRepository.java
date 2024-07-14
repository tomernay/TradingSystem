package Domain.Repo;

import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Utilities.Response;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IUserRepository iuserRepository;
    private final List<String> subscribersLoggedIn;
    private final Map<String, User> guests;
    private Integer userIDS; //Counter for guest ID

    public UserRepository() {
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
        return iuserRepository.findById(username).get();
    }

    public List<Subscriber> getAllSubscribers() {
        return iuserRepository.findAll();
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
        return iuserRepository.existsById(username);
    }

    public void addSubscriber(Subscriber subscriber) {
        iuserRepository.save(subscriber);
    }

    public void removeSubscriber(String username) {
        Subscriber subscriber = iuserRepository.findById(username).get();
        iuserRepository.delete(subscriber);
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
        return Response.success("All subscribers", new HashSet<>(iuserRepository.findAll().stream().map(Subscriber::getUsername).toList()));
    }
}