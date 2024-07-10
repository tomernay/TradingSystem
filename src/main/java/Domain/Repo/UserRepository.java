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
public class UserRepository implements JpaRepository<User, Long> {

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

    public Response<Set<String>> getAllSubscribersUsernames() {
        return Response.success("All subscribers",subscribers.keySet());
    }
}