import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Domain.Users.Subscriber.Subscriber;
import Domain.Users.User;
import Service.UserService;
import Utilities.Response;

public class UserRegisterLoginLogout {
    UserService userService;
    User guest1;
    @Before
    public void init(){
        userService = new UserService();
        guest1 = new User();
    }

    @Test
    public void registerUserTest(){
        // Attempt to register a new user with a password that meets the requirements
        Response<String> response = userService.register("tomer","Password123");

        // Assert that the registration was successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void registerInvalidUsernameTest(){
        // Try to register a user with a username that does not meet the requirements
        Response<String> response = userService.register("","Password123");
        System.out.println(response.getMessage());
        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }
    @Test
    public void registerDuplicateUsernameTest(){
        // Register a user with username "niv"
        userService.register("nivn96","Password123");

        // Try to register another user with the same username
        Response<String> response = userService.register("nivn96","Password123");

        // The registration should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

//    @Test
//    public void registerInvalidPasswordTest(){
//        // Try to register a user with a password that does not meet the requirements
//        Response<String> response = userService.register("nivn967","123");
//
//        // The registration should fail, so the success status should be false
//        Assert.assertFalse(response.isSuccess());
//    }

    @Test
    public void loginAsSubscriberTest(){
        // Register a new user
        userService.register("nivn96","Password123");

        // Attempt to log in as a subscriber
        Subscriber subscriber = new Subscriber("nivn96","Password123");
        Response<String> response = userService.loginAsSubscriber(subscriber);

        // Assert that the login was successful
        Assert.assertTrue(response.isSuccess());
    }
    @Test
    public void loginAsSubscriberFailureTest(){
        // Attempt to log in as a subscriber who is not registered
        Subscriber subscriber = new Subscriber("nonExistentUser","Password123");
        Response<String> response = userService.loginAsSubscriber(subscriber);

        // The login should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void logoutAsSubscriberTest(){
        // Register and login a new user
        userService.register("nivn96","Password123");
        Subscriber subscriber = new Subscriber("nivn96","Password123");
        userService.loginAsSubscriber(subscriber);

        // Attempt to log out as a subscriber
        Response<String> response = userService.logoutAsSubscriber(subscriber);

        // Assert that the logout was successful
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void logoutAsSubscriberFailureTest(){
        // Attempt to log out as a subscriber who is not logged in
        Subscriber subscriber = new Subscriber("nonExistentUser","Password123");
        Response<String> response = userService.logoutAsSubscriber(subscriber);

        // The logout should fail, so the success status should be false
        Assert.assertFalse(response.isSuccess());
    }

}

