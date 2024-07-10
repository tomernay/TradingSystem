package UnitTests;

import Domain.Externals.Security.PasswordEncoderUtil;
import Domain.Externals.Security.TokenHandler;
import Domain.Repo.UserRepository;
import Service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserRegisterLoginLogoutTests {
    ServiceInitializer serviceInitializer;
    UserService userService;
    UserRepository userRepository;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        userRepository = userService.getUserFacade().getUserRepository();
    }

    @Test
    public void registerUserSuccessTest(){
        // Attempt to register a new user with a password that meets the requirements
        userService.register("testUser","Password123!");
        // Assert that the registration was successful
        Assert.assertTrue(userRepository.getSubscribers().containsKey("testUser"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
        Assert.assertTrue(PasswordEncoderUtil.matches("Password123!",userRepository.getSubscribers().get("testUser").getPassword()));
    }

    @Test
    public void registerUserFailureTest(){
        // Register a user with username
        userService.register("existingUser","Password123!");

        // Try to register another user with the same username
        userService.register("existingUser","Password123!");

        // The registration should fail, so the success status should be false
        Assert.assertTrue(userRepository.getSubscribers().containsKey("existingUser"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
    }

    /* @Test
    public void registerUserInvalidUsernameTest(){
        // Try to register a user with a username that does not meet the requirements
        userService.register("","Password123!");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(userRepository.getSubscribers().containsKey(""));
        Assert.assertEquals(userRepository.getSubscribers().size(), 0);
    }*/

    @Test
    public void registerUserInvalidPasswordTest(){
        // Try to register a user with a password that does not meet the requirements
        userService.register("tomer","Password1");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(userRepository.getSubscribers().containsKey("tomer"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 0);
    }

    @Test
    public void loginAsSubscriberSuccessTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");
        Assert.assertTrue(userRepository.getSubscribers().containsKey("validUser"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
        Assert.assertTrue(PasswordEncoderUtil.matches("ValidPassword123!",userRepository.getSubscribers().get("validUser").getPassword()));
        // Attempt to log in as a subscriber with valid credentials
        userService.loginAsSubscriber("validUser","ValidPassword123!");
        Assert.assertTrue(userRepository.getSubscribersLoggedIn().contains("validUser"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 1);
    }

    @Test
    public void loginAsSubscriberInvalidUsernameTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");
        Assert.assertTrue(userRepository.getSubscribers().containsKey("validUser"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
        Assert.assertTrue(PasswordEncoderUtil.matches("ValidPassword123!",userRepository.getSubscribers().get("validUser").getPassword()));
        // Attempt to log in as a subscriber with invalid username
        userService.loginAsSubscriber("invalidUser","ValidPassword123!");
        Assert.assertFalse(userRepository.getSubscribersLoggedIn().contains("invalidUser"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 0);
    }

    @Test
    public void loginAsSubscriberInvalidPasswordTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");
        Assert.assertTrue(userRepository.getSubscribers().containsKey("validUser"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
        Assert.assertTrue(PasswordEncoderUtil.matches("ValidPassword123!",userRepository.getSubscribers().get("validUser").getPassword()));
        // Attempt to log in as a subscriber with invalid password
        userService.loginAsSubscriber("validUser","InvalidPassword123!");
        Assert.assertFalse(userRepository.getSubscribersLoggedIn().contains("validUser"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 0);
    }

    @Test
    public void loginAsSubscriberInvalidTokenTest(){
        // Register a new user with valid credentials
        userService.register("validUser","ValidPassword123!");
        Assert.assertTrue(userRepository.getSubscribers().containsKey("validUser"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
        Assert.assertTrue(PasswordEncoderUtil.matches("ValidPassword123!",userRepository.getSubscribers().get("validUser").getPassword()));
        // Attempt to log in as a subscriber with valid credentials
        userService.loginAsSubscriber("validUser","ValidPassword123!");
        Assert.assertTrue(userRepository.getSubscribersLoggedIn().contains("validUser"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 1);

        // Generate a valid token for a different user
        String invalidToken = TokenHandler.generateJWT("anotherUser");

        // Check if the token is valid for the original user
        boolean isValid = userService.isValidToken(invalidToken, "validUser");

        // The token should be invalid for the original user, so isValid should be false
        Assert.assertFalse(isValid);
    }

    @Test
    public void logoutAsSubscriberSuccessTest(){
        // Register and login a new user
        userService.register("nivn96","Password123!");
        userService.loginAsSubscriber("nivn96","Password123!");
        Assert.assertTrue(userRepository.getSubscribers().containsKey("nivn96"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
        Assert.assertTrue(PasswordEncoderUtil.matches("Password123!",userRepository.getSubscribers().get("nivn96").getPassword()));
        Assert.assertTrue(userRepository.getSubscribersLoggedIn().contains("nivn96"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 1);

        // Attempt to log out as a subscriber
        userService.logoutAsSubscriber("nivn96");
        Assert.assertFalse(userRepository.getSubscribersLoggedIn().contains("nivn96"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 0);
    }

    @Test
    public void logoutAsSubscriberNotLoggedInTest(){
        // Register a new user but do not log them in
        userService.register("nivn96","Password123!");
        Assert.assertTrue(userRepository.getSubscribers().containsKey("nivn96"));
        Assert.assertEquals(userRepository.getSubscribers().size(), 1);
        Assert.assertTrue(PasswordEncoderUtil.matches("Password123!",userRepository.getSubscribers().get("nivn96").getPassword()));
        Assert.assertFalse(userRepository.getSubscribersLoggedIn().contains("nivn96"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 0);
        // Attempt to log out as a subscriber
        userService.logoutAsSubscriber("nivn96");
        Assert.assertFalse(userRepository.getSubscribersLoggedIn().contains("nivn96"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 0);
    }

    @Test
    public void logoutAsNonExistentSubscriberTest(){
        // Attempt to log out a user who does not exist
        Assert.assertEquals(userRepository.getSubscribers().size(), 0);
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 0);
        userService.logoutAsSubscriber("nonExistentUser");
        Assert.assertFalse(userRepository.getSubscribersLoggedIn().contains("nonExistentUser"));
        Assert.assertEquals(userRepository.getSubscribersLoggedIn().size(), 0);
    }

}
