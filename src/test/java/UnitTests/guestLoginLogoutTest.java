package UnitTests;

import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class guestLoginLogoutTest {
    ServiceInitializer serviceInitializer;
    UserService userService;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
    }

    @Test
    public void loginAsGuestTest(){
        // Act guest0
        Response<String> response = userService.loginAsGuest();

        // Assert guest0
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals("Guest0", response.getData());

        // Act guest1
        Response<String> response1 = userService.loginAsGuest();

        // Assert guest1
        Assert.assertTrue(response1.isSuccess());
        Assert.assertEquals("Guest1", response1.getData());
    }

    @Test
    public void failedLoginAsGuestTest(){
        // Act guest0
        Response<String> response = userService.loginAsGuest();

        // Assert guest0
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotEquals("Guest1", response.getData());

    }

    @Test
    public void logoutAsGuestSuccessTest(){
        // Act: Login as a guest
        Response<String> loginResponse = userService.loginAsGuest();
        Assert.assertTrue(loginResponse.isSuccess());

        // Act: Logout as the same guest
        Response<String> logoutResponse = userService.logoutAsGuest(loginResponse.getData());

        // Assert: Logout should be successful
        Assert.assertTrue(logoutResponse.isSuccess());
    }

    @Test
    public void logoutAsGuestFailureTest(){
        // Act: Try to log out as a guest who is not logged in
        Response<String> logoutResponse = userService.logoutAsGuest("NonExistentGuest");

        // Assert: Logout should fail
        Assert.assertFalse(logoutResponse.isSuccess());
    }
}