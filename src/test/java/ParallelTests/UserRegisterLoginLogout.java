package ParallelTests;

import Domain.Externals.Security.Security;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserRegisterLoginLogout {
    ServiceInitializer serviceInitializer;
    UserService userService;

    @Before
    public void init(){
        serviceInitializer = new ServiceInitializer();
        userService = serviceInitializer.getUserService();
    }

    @Test
    public void registerUserSuccessTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to register as subscriber
        Future<Response<String>> future1 = executorService.submit(() -> userService.register("testUser1","Password123!"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.register("testUser2","Password123!"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both registrations were successful
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void registerUserFailureTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Register a user with username
        userService.register("existingUser","Password123!");

        // Submit two tasks to register with the same username
        Future<Response<String>> future1 = executorService.submit(() -> userService.register("existingUser","Password123!"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.register("existingUser","Password123!"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both registrations were unsuccessful
        Assert.assertFalse(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void registerUserInvalidUsernameTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to register with invalid usernames
        Future<Response<String>> future1 = executorService.submit(() -> userService.register("","Password123!"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.register("","Password123!"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both registrations were unsuccessful
        Assert.assertFalse(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void registerUserInvalidPasswordTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to register with invalid passwords
        Future<Response<String>> future1 = executorService.submit(() -> userService.register("testUser3",""));
        Future<Response<String>> future2 = executorService.submit(() -> userService.register("testUser4",""));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both registrations were unsuccessful
        Assert.assertFalse(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void loginAsSubscriberSuccessTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Submit two tasks to log in as a subscriber with valid credentials
        Future<Response<String>> future1 = executorService.submit(() -> userService.loginAsSubscriber("validUser","ValidPassword123!"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.loginAsSubscriber("validUser","ValidPassword123!"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both logins were successful
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void loginAsSubscriberInvalidUsernameTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Submit two tasks to log in as a subscriber with invalid username
        Future<Response<String>> future1 = executorService.submit(() -> userService.loginAsSubscriber("invalidUser","ValidPassword123!"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.loginAsSubscriber("invalidUser","ValidPassword123!"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both logins were unsuccessful
        Assert.assertFalse(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void loginAsSubscriberInvalidPasswordTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Submit two tasks to log in as a subscriber with invalid password
        Future<Response<String>> future1 = executorService.submit(() -> userService.loginAsSubscriber("validUser","InvalidPassword123!"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.loginAsSubscriber("validUser","InvalidPassword123!"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both logins were unsuccessful
        Assert.assertFalse(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void loginAsSubscriberInvalidTokenTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Submit a task to log in as a subscriber with valid credentials
        Future<Response<String>> future1 = executorService.submit(() -> userService.loginAsSubscriber("validUser","ValidPassword123!"));

        // Get the response. This will block until the task is complete.
        Response<String> response1 = future1.get();

        // Assert that the login was successful
        Assert.assertTrue(response1.isSuccess());

        // Generate a valid token for a different user
        String invalidToken = Security.generateJWT("anotherUser");

        // Submit a task to check if the token is valid for the original user
        Future<Boolean> future2 = executorService.submit(() -> userService.isValidToken(invalidToken, "validUser"));

        // Get the response. This will block until the task is complete.
        boolean isValid = future2.get();

        // The token should be invalid for the original user, so isValid should be false
        Assert.assertFalse(isValid);

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void logoutAsSubscriberSuccessTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Register and login a new user
        userService.register("nivn96","Password123!");
        userService.loginAsSubscriber("nivn96","Password123!");

        // Submit two tasks to log out as a subscriber
        Future<Response<String>> future1 = executorService.submit(() -> userService.logoutAsSubscriber("nivn96"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.logoutAsSubscriber("nivn96"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that the first one was success while the other should fail
        Assert.assertTrue(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void logoutAsSubscriberNotLoggedInTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Register a new user but do not log them in
        userService.register("nivn96","Password123!");

        // Submit two tasks to log out as a subscriber
        Future<Response<String>> future1 = executorService.submit(() -> userService.logoutAsSubscriber("nivn96"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.logoutAsSubscriber("nivn96"));

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both logouts were unsuccessful
        Assert.assertFalse(response1.isSuccess());
        Assert.assertFalse(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void logoutAsNonExistentSubscriberTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to log out a user who does not exist
        Future<Response<String>> future1 = executorService.submit(() -> userService.logoutAsSubscriber("nonExistentUser"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.logoutAsSubscriber("nonExistentUser"));

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