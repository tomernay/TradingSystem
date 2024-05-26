package UnitTests;

import Domain.Externals.Security.Security;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserRegisterLoginLogoutTests {
    ServiceInitializer serviceInitializer;
    UserService userService;

    @Before
    public void init(){
        serviceInitializer = new ServiceInitializer();
        userService = serviceInitializer.getUserService();
    }

    @Test
    public void registerUserSuccessTest(){
        // Attempt to register a new user with a password that meets the requirements
        Response<String> response = userService.register("testUser","Password123!");

        // Assert that the registration was successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void registerUserFailureTest(){
        // Register a user with username
        userService.register("existingUser","Password123!");

        // Try to register another user with the same username
        Response<String> response = userService.register("existingUser","Password123!");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void registerUserInvalidUsernameTest(){
        // Try to register a user with a username that does not meet the requirements
        Response<String> response = userService.register("","Password123!");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void registerUserInvalidPasswordTest(){
        // Try to register a user with a password that does not meet the requirements
        Response<String> response = userService.register("tomer","Password1");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void loginAsSubscriberSuccessTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Attempt to log in as a subscriber with valid credentials
        Response<String> response = userService.loginAsSubscriber("validUser","ValidPassword123!");

        // Assert that the login was successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void loginAsSubscriberInvalidUsernameTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Attempt to log in as a subscriber with invalid username
        Response<String> response = userService.loginAsSubscriber("invalidUser","ValidPassword123!");

        // The login should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void loginAsSubscriberInvalidPasswordTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Attempt to log in as a subscriber with invalid password
        Response<String> response = userService.loginAsSubscriber("validUser","InvalidPassword123!");

        // The login should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void loginAsSubscriberInvalidTokenTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");

        // Attempt to log in as a subscriber with valid credentials
        Response<String> response = userService.loginAsSubscriber("validUser","ValidPassword123!");

        // Assert that the login was successful
        Assert.assertTrue(response.isSuccess());

        // Generate a valid token for a different user
        String invalidToken = Security.generateJWT("anotherUser");

        // Check if the token is valid for the original user
        boolean isValid = userService.isValidToken(invalidToken, "validUser");

        // The token should be invalid for the original user, so isValid should be false
        Assert.assertFalse(isValid);
    }
}
