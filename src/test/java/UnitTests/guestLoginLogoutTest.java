package UnitTests;

import Domain.Users.User;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class guestLoginLogoutTest {
    UserService userService;

    @Before
    public void init(){
        userService = new UserService();
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
}