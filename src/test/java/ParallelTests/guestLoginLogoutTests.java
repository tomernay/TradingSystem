package ParallelTests;

import Domain.Users.User;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class guestLoginLogoutTests {
    ServiceInitializer serviceInitializer;
    UserService userService;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
    }

    @Test
    public void loginAsGuestTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to log in as guest
        Future<Response<List<String>>> future1 = executorService.submit(() -> userService.loginAsGuest());
        Future<Response<List<String>>> future2 = executorService.submit(() -> userService.loginAsGuest());

        // Get the responses. This will block until the tasks are complete.
        Response<List<String>> response1 = future1.get();
        Response<List<String>> response2 = future2.get();

        // Assert that both logins were successful
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void logoutAsGuestSuccessTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to log in as guest
        Future<Response<List<String>>> loginFuture1 = executorService.submit(() -> userService.loginAsGuest());
        Future<Response<List<String>>> loginFuture2 = executorService.submit(() -> userService.loginAsGuest());

        // Get the responses. This will block until the tasks are complete.
        Response<List<String>> loginResponse1 = loginFuture1.get();
        Response<List<String>> loginResponse2 = loginFuture2.get();

        // Submit two tasks to log out as guest
        Future<Response<String>> logoutFuture1 = executorService.submit(() -> userService.logoutAsGuest(loginResponse1.getData().get(0)));
        Future<Response<String>> logoutFuture2 = executorService.submit(() -> userService.logoutAsGuest(loginResponse2.getData().get(0)));

        // Get the responses. This will block until the tasks are complete.
        Response<String> logoutResponse1 = logoutFuture1.get();
        Response<String> logoutResponse2 = logoutFuture2.get();

        // Assert that both logouts were successful
        Assert.assertTrue(logoutResponse1.isSuccess());
        Assert.assertTrue(logoutResponse2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void logoutAsGuestFailureTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to log out as guest with non-existent guest usernames
        Future<Response<String>> future1 = executorService.submit(() -> userService.logoutAsGuest("NonExistentGuest1"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.logoutAsGuest("NonExistentGuest2"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both logouts were unsuccessful
        Assert.assertFalse(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }
}