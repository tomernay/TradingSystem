package AcceptanceTests;

import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class guestLoginLogout {
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
        // 2 users login as guests.
        Response<String> response = userService.loginAsGuest();
        Assert.assertTrue(response.isSuccess());
        Response<String> response2 = userService.loginAsGuest();
        Assert.assertTrue(response2.isSuccess());
    }


    @Test
    public void logoutAsGuestTest(){
        userService.loginAsGuest();
        Response<String> response = userService.logoutAsGuest("Guest0");
        Assert.assertTrue(response.isSuccess());
    }


    @Test
    public void logoutAsGuestFailureTest(){
        // Assuming that the logoutAsGuest method returns failure when the user is not logged in
        Response<String> response = userService.logoutAsGuest("Guest0"); // Logout should fail as the user is not logged in
        Assert.assertFalse(response.isSuccess());
    }

}
