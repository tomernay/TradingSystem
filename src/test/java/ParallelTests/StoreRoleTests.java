package ParallelTests;

import Service.ServiceInitializer;
import Service.StoreService;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreRoleTests {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, newOwner, newManager, notOwner;
    Store store;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService=serviceInitializer.getStoreService();
        userService=serviceInitializer.getUserService();
        userService.register("yair12312","Password123!");
        userService.loginAsSubscriber("yair12312","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getUser("yair12312");

        userService.register("newOwner","Password123!");
        userService.loginAsSubscriber("newOwner","Password123!");
        newOwner=userService.getUserFacade().getUserRepository().getUser("newOwner");

        userService.register("newManager","Password123!");
        userService.loginAsSubscriber("newManager","Password123!");
        newManager=userService.getUserFacade().getUserRepository().getUser("newManager");

        userService.register("notOwner","Password123!");
        userService.loginAsSubscriber("notOwner","Password123!");
        notOwner=userService.getUserFacade().getUserRepository().getUser("notOwner");

        storeService.addStore("yairStore","yair12312",subscriber.getToken());
        store=storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

    @Test
    public void makeStoreOwnerTestParallel() throws InterruptedException {
        // Create a list of owners
        List<Subscriber> owners = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String username = "owner" + i;
            userService.register(username, "Password123!");
            userService.loginAsSubscriber(username, "Password123!");
            owners.add(userService.getUserFacade().getUserRepository().getUser(username));

            // Original owner nominates new owner
            userService.SendStoreOwnerNomination(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
        }

        // Create a list of threads
        List<Thread> threads = new ArrayList<>();

        // Create a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);

        // Create an AtomicBoolean
        AtomicBoolean nominationSucceeded = new AtomicBoolean(false);

        // Create and start threads
        for (Subscriber owner : owners) {
            Runnable task = () -> {
                try {
                    latch.await();
                    Response<String> response = userService.SendStoreOwnerNomination(store.getId(), owner.getUsername(), newOwner.getUsername(), owner.getToken());
                    if (nominationSucceeded.get()) {
                        // If another thread has already succeeded, this thread should fail
                        Assert.assertFalse(response.isSuccess());
                    } else {
                        // If no other thread has succeeded yet, this thread should succeed
                        Assert.assertTrue(response.isSuccess());
                        nominationSucceeded.set(true);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (AssertionError e) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " failed to perform the action: " + e.getMessage());
                }
            };
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);
        }

        // Count down the latch, releasing all waiting threads at once
        latch.countDown();

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void makeStoreManagerTestParallel() throws InterruptedException {
        // Create a list of owners
        List<Subscriber> owners = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String username = "owner" + i;
            userService.register(username, "Password123!");
            userService.loginAsSubscriber(username, "Password123!");
            owners.add(userService.getUserFacade().getUserRepository().getUser(username));

            // Original owner nominates new owner
            userService.SendStoreOwnerNomination(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
        }

        // Create a list of threads
        List<Thread> threads = new ArrayList<>();

        // Create a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);

        // Create an AtomicBoolean
        AtomicBoolean nominationSucceeded = new AtomicBoolean(false);

        // Create and start threads
        for (Subscriber owner : owners) {
            Runnable task = () -> {
                try {
                    latch.await();
                    Response<String> response = userService.SendStoreManagerNomination(store.getId(), owner.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), owner.getToken());
                    if (nominationSucceeded.compareAndSet(false, true)) {
                        Assert.assertTrue(response.isSuccess());
                    } else {
                        Assert.assertFalse(response.isSuccess());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);
        }

        latch.countDown();

        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void addPermissionSuccessTestParallel() throws InterruptedException {
        // Similar to the previous test, create a list of owners and nominate them
        List<Subscriber> owners = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String username = "owner" + i;
            userService.register(username, "Password123!");
            userService.loginAsSubscriber(username, "Password123!");
            owners.add(userService.getUserFacade().getUserRepository().getUser(username));

            // Original owner nominates new owner
            userService.SendStoreOwnerNomination(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
        }

        userService.SendStoreManagerNomination(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(newManager.getUsername(), true, newManager.getToken());

        // Create a list of threads
        List<Thread> threads = new ArrayList<>();

        // Create a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);

        // Create an AtomicBoolean
        AtomicBoolean permissionAdded = new AtomicBoolean(false);

        // Create and start threads
        for (Subscriber owner : owners) {
            Runnable task = () -> {
                try {
                    latch.await();
                    Response<String> response = storeService.addManagerPermissions(store.getId(), owner.getUsername(), newManager.getUsername(), "ADD_PRODUCT", owner.getToken());
                    if (permissionAdded.compareAndSet(false, true)) {
                        Assert.assertTrue(response.isSuccess());
                    } else {
                        Assert.assertFalse(response.isSuccess());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);
        }

        latch.countDown();

        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void removePermissionSuccessTestParallel() throws InterruptedException {
        // Similar to the previous test, create a list of owners and nominate them
        List<Subscriber> owners = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String username = "owner" + i;
            userService.register(username, "Password123!");
            userService.loginAsSubscriber(username, "Password123!");
            owners.add(userService.getUserFacade().getUserRepository().getUser(username));

            // Original owner nominates new owner
            userService.SendStoreOwnerNomination(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
        }

        userService.SendStoreManagerNomination(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(newManager.getUsername(), true, newManager.getToken());

        // Create a list of threads
        List<Thread> threads = new ArrayList<>();

        // Create a CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);

        // Create an AtomicBoolean
        AtomicBoolean permissionRemoved = new AtomicBoolean(false);

        // Create and start threads
        for (Subscriber owner : owners) {
            Runnable task = () -> {
                try {
                    latch.await();
                    Response<String> response = storeService.removeManagerPermissions(store.getId(), owner.getUsername(), newManager.getUsername(), "REMOVE_PRODUCT", owner.getToken());
                    if (permissionRemoved.compareAndSet(false, true)) {
                        Assert.assertTrue(response.isSuccess());
                    } else {
                        Assert.assertFalse(response.isSuccess());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);
        }

        latch.countDown();

        for (Thread thread : threads) {
            thread.join();
        }
    }

    // Add similar parallel tests for other methods...
}