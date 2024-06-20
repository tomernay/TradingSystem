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
    Subscriber subscriber, subscriber2;
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

        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        storeService.addStore("yairStore","yair12312",subscriber.getToken());
        store=storeService.getStoreFacade().getStoreRepository().getStore(0);
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
            Response<Integer> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(response.getData(), username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
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
                    Response<Integer> response = userService.SendOwnerNominationRequest(store.getId(), owner.getUsername(), subscriber2.getUsername(), owner.getToken());
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
            Response<Integer> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(response.getData(), username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
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
                    Response<Integer> response = userService.SendManagerNominationRequest(store.getId(), owner.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), owner.getToken());
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
            Response<Integer> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(response.getData(), username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
        }

        Response<Integer> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());

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
                    Response<String> response2 = storeService.addManagerPermissions(store.getId(), owner.getUsername(), subscriber2.getUsername(), "ADD_PRODUCT", owner.getToken());
                    if (permissionAdded.compareAndSet(false, true)) {
                        Assert.assertTrue(response2.isSuccess());
                    } else {
                        Assert.assertFalse(response2.isSuccess());
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
            Response<Integer> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), username, subscriber.getToken());
            userService.ownerNominationResponse(response.getData(), username, true, userService.getUserFacade().getUserRepository().getUser(username).getToken());
        }

        Response<Integer> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS"), subscriber.getToken());
        userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());

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
                    Response<String> response2 = storeService.removeManagerPermissions(store.getId(), owner.getUsername(), subscriber2.getUsername(), "REMOVE_PRODUCT", owner.getToken());
                    if (permissionRemoved.compareAndSet(false, true)) {
                        Assert.assertTrue(response2.isSuccess());
                    } else {
                        Assert.assertFalse(response2.isSuccess());
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