package UnitTests;

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
        Response<String> response = userService.register("testUser","Password123");

        // Assert that the registration was successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void registerUserFailureTest(){
        // Register a user with username
        userService.register("existingUser","Password123");

        // Try to register another user with the same username
        Response<String> response = userService.register("existingUser","Password123");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void registerUserInvalidUsernameTest(){
        // Try to register a user with a username that does not meet the requirements
        Response<String> response = userService.register("","Password123");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }
}
