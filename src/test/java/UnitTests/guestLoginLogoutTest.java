package UnitTests;

import Domain.Repo.UserRepository;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class guestLoginLogoutTest {
    ServiceInitializer serviceInitializer;
    UserService userService;
    UserRepository userRepository;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        userRepository = serviceInitializer.getUserService().getUserFacade().getUserRepository();
    }

    @Test
    public void loginAsGuestTest(){
        Assert.assertEquals(userRepository.getGuests().size(), 0);
        // Act guest0
        userService.loginAsGuest();

        // Assert guest0
        Assert.assertTrue(userRepository.getGuests().containsKey("Guest0"));
        Assert.assertEquals(userRepository.getGuests().size(), 1);

        // Act guest1
        userService.loginAsGuest();

        // Assert guest1
        Assert.assertTrue(userRepository.getGuests().containsKey("Guest1"));
        Assert.assertEquals(userRepository.getGuests().size(), 2);
    }

//    @Test
//    public void failedLoginAsGuestTest(){
//        Assert.assertEquals(userRepository.getGuests().size(), 0);
//        // Act guest0
//        Response<List<String>> response = userService.loginAsGuest();
//
//        // Assert guest0
//
//        Assert.assertTrue(response.isSuccess());
//        Assert.assertNotEquals("Guest1", response.getData().get(0));
//
//    }

    @Test
    public void logoutAsGuestSuccessTest(){
        Assert.assertEquals(userRepository.getGuests().size(), 0);
        // Act: Login as a guest
        Response<List<String>> loginResponse = userService.loginAsGuest();
        Assert.assertTrue(userRepository.getGuests().containsKey("Guest0"));
        Assert.assertEquals(userRepository.getGuests().size(), 1);

        // Act: Logout as the same guest
        userService.logoutAsGuest(loginResponse.getData().get(0));

        // Assert: Logout should be successful
        Assert.assertFalse(userRepository.getGuests().containsKey("Guest0"));
        Assert.assertEquals(userRepository.getGuests().size(), 0);
    }

    @Test
    public void logoutAsGuestFailureTest(){
        Assert.assertEquals(userRepository.getGuests().size(), 0);
        Assert.assertFalse(userRepository.getGuests().containsKey("NonExistentGuest"));
        // Act: Try to log out as a guest who is not logged in
        userService.logoutAsGuest("NonExistentGuest");

        // Assert: Logout should fail
        Assert.assertEquals(userRepository.getGuests().size(), 0);
    }
}