package Tests;

import Domain.Users.User;
import Service.Service;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GuestLogin {
    Service service;
    User guest1;
    User guest2;

    @Before
    public void init(){
        service=new Service();
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

//    @Test
//    public void makeStoreOwnerTest(){
//        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername());
//        Assert.assertTrue(response.isSuccess());
//    }
//
//    @Test
//    public void makeStoreManagerTest(){
//        Response<String> response = service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
//        Assert.assertTrue(response.isSuccess());
//    }
//
//    @Test
//    public void notOwnerMakeStoreOwnerTest(){
//        Response<String> response = service.makeStoreOwner(store.getId(), notOwner.getUsername(), newOwner.getUsername());
//        Assert.assertFalse(response.isSuccess());
//    }
//
//    @Test
//    public void notOwnerMakeStoreManagerTest(){
//        Response<String> response = service.makeStoreManager(store.getId(), notOwner.getUsername(), newManager.getUsername(), null);
//        Assert.assertFalse(response.isSuccess());
//    }
//
//    @Test
//    public void makeExistingStoreOwnerTest(){
//        service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername());
//        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername());
//        Assert.assertFalse(response.isSuccess());
//    }
//
//    @Test
//    public void makeExistingStoreManagerTest(){
//        service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
//        Response<String> response = service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
//        Assert.assertFalse(response.isSuccess());
//    }
//
//    public void addPermissionSuccessTest(){
//        service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
//        Response<String> response = service.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "permission");
//        Assert.assertTrue(response.isSuccess());
//    }
//
//    @Test
//    public void removePermissionSuccessTest(){
//        service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
//        service.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "permission");
//        Response<String> response = service.removeManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "permission");
//        Assert.assertTrue(response.isSuccess());
//    }
//
//    @Test
//    public void addPermissionNotOwnerTest(){
//        Response<String> response = service.addManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "permission");
//        Assert.assertFalse(response.isSuccess());
//    }
//
//    @Test
//    public void removePermissionNotOwnerTest(){
//        Response<String> response = service.removeManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "permission");
//        Assert.assertFalse(response.isSuccess());
//    }
//
//    @Test
//    public void addPermissionNotManagerTest(){
//        Response<String> response = service.addManagerPermissions(store.getId(), subscriber.getUsername(), newOwner.getUsername(), "permission");
//        Assert.assertFalse(response.isSuccess());
//    }
//
//    @Test
//    public void removePermissionNotManagerTest(){
//        Response<String> response = service.removeManagerPermissions(store.getId(), subscriber.getUsername(), newOwner.getUsername(), "permission");
//        Assert.assertFalse(response.isSuccess());
//    }
}
