package AcceptanceTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Service.UserService;
import Utilities.Response;

public class UserRegisterLoginLogout {
    ServiceInitializer serviceInitializer;
    UserService userService;
    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
    }

    @Test
    public void registerUserTest(){
        // Attempt to register a new user with a password that meets the requirements
        Response<String> response = userService.register("tomer","Password123!");

        // Assert that the registration was successful
        Assert.assertTrue(response.isSuccess());
    }
    /*
    @Test
    public void registerInvalidUsernameTest(){
        // Try to register a user with a username that does not meet the requirements
        Response<String> response = userService.register("","Password123!");
        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }*/
    @Test
    public void registerDuplicateUsernameTest(){
        // Register a user with username
        userService.register("nivn96","Password123!");

        // Try to register another user with the same username
        Response<String> response = userService.register("nivn96","Password123!");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }


    @Test
    public void loginAsSubscriberTest(){
        // Register a new user
        userService.register("nivn96","Password123!");

        // Attempt to log in as a subscriber
        Response<String> response = userService.loginAsSubscriber("nivn96","Password123!");

        // Assert that the login was successful
        Assert.assertTrue(response.isSuccess());
    }
    @Test
    public void loginAsSubscriberFailureTest(){
        // Attempt to log in as a subscriber who is not registered
        Response<String> response = userService.loginAsSubscriber("nonExistentUser","Password123!");

        // The login should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void loginAsSubscriberWithInvalidUsernameTest(){
        // Register a new user with valid username
        userService.register("validUser","Password123!");

        // Attempt to log in as a subscriber with invalid username
        Response<String> response = userService.loginAsSubscriber("invalidUser","Password123!");

        // The login should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void loginAsSubscriberWithInvalidPasswordTest(){
        // Register a new user with valid password
        userService.register("validUser","ValidPassword123!");

        // Attempt to log in as a subscriber with invalid password
        Response<String> response = userService.loginAsSubscriber("validUser","InvalidPassword123!");

        // The login should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void logoutAsSubscriberTest(){
        // Register and login a new user
        userService.register("nivn96","Password123!");
        userService.loginAsSubscriber("nivn96","Password123!");

        // Attempt to log out as a subscriber
        Response<String> response = userService.logoutAsSubscriber("nivn96");

        // Assert that the logout was successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void logoutAsSubscriberNotLoggedInTest(){
        // Register a new user but do not log them in
        userService.register("nivn96","Password123!");

        // Attempt to log out as a subscriber
        Response<String> response = userService.logoutAsSubscriber("nivn96");

        // Assert that the logout was unsuccessful
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void logoutAsNonExistentSubscriberTest(){
        // Attempt to log out a user who does not exist
        Response<String> response = userService.logoutAsSubscriber("nonExistentUser");

        // Assert that the logout was unsuccessful
        Assert.assertFalse(response.isSuccess());
    }

}

