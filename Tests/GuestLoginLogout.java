package Tests;

import Domain.Users.User;
import Service.StoreService;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.Service;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
public class GuestLoginLogout {
    Service service;
    UserService userService;
    User guest1;
    User guest2;

    @Before
    public void init(){
        service=new Service();
        userService = service.getUserService();
        guest1 = new User();
        guest2 = new User();
    }

    @Test
    public void loginAsGuestTest(){
        Response<String> response = service.getUserService().loginAsGuest(guest1);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void notLoginAsGuestTest(){
        Response<String> response = service.getUserService().loginAsGuest(guest1);
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void logoutAsGuestTest(){
        Response<String> response = service.getUserService().logoutAsGuest(guest1);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void notLogoutAsGuestTest(){
        Response<String> response = service.getUserService().logoutAsGuest(guest1);
        Assert.assertFalse(response.isSuccess());
    }

}
