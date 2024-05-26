package ParallelTests;

import Domain.Users.User;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class guestLoginLogoutTests {
    UserService userService;

    @Before
    public void init(){
        userService = new UserService();
    }

    @Test
    public void loginAsGuestTest() throws Exception {
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to log in as guest
        Future<Response<String>> future1 = executorService.submit(() -> userService.loginAsGuest());
        Future<Response<String>> future2 = executorService.submit(() -> userService.loginAsGuest());

        // Get the responses. This will block until the tasks are complete.
        Response<String> response1 = future1.get();
        Response<String> response2 = future2.get();

        // Assert that both logins were successful
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(response2.isSuccess());

        // Shut down the executor service
        executorService.shutdown();
    }
}