package ParallelTests;

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
        Future<Response<String>> future1 = executorService.submit(() -> userService.register("testUser1","Password123"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.register("testUser2","Password123"));

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
        userService.register("existingUser","Password123");

        // Submit two tasks to register with the same username
        Future<Response<String>> future1 = executorService.submit(() -> userService.register("existingUser","Password123"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.register("existingUser","Password123"));

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
        Future<Response<String>> future1 = executorService.submit(() -> userService.register("","Password123"));
        Future<Response<String>> future2 = executorService.submit(() -> userService.register("","Password123"));

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
}