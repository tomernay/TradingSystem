import Domain.Users.User;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class guestLoginLogout {
    UserService userService;
    User guest1;
    User guest2;

    @Before
    public void init(){
        userService = new UserService();
        guest1 = new User();
        guest2 = new User();
    }

    @Test
    public void loginAsGuestTest(){
        Response<String> response = userService.loginAsGuest(guest1);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void loginAsGuestFailureTest(){
        // Assuming that the loginAsGuest method returns failure when the user is already logged in
        userService.loginAsGuest(guest1); // First login should be successful
        Response<String> response = userService.loginAsGuest(guest1); // Second login should fail
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void logoutAsGuestTest(){
        userService.loginAsGuest(guest1);
        Response<String> response = userService.logoutAsGuest(guest1);
        Assert.assertTrue(response.isSuccess());
    }


    @Test
    public void logoutAsGuestFailureTest(){
        // Assuming that the logoutAsGuest method returns failure when the user is not logged in
        Response<String> response = userService.logoutAsGuest(guest2); // Logout should fail as the user is not logged in
        Assert.assertTrue(!response.isSuccess());
    }

}
