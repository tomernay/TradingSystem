package src.Tests;

import src.main.java.Service.StoreService;
import src.main.java.Domain.Store.Store;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import src.main.java.Service.Service;
import src.main.java.Service.UserService;
import src.main.java.Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class StoreRole {
    Service service;
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, newOwner, newManager, notOwner;
    Store store;

    @Before
    public void init(){
        service=new Service();
        storeService=service.getStoreService();
        service.getUserService().register("yair12312","password123");
        subscriber=service.getUserService().getUser("yair12312");

        service.getUserService().register("newOwner","by2");
        newOwner=service.getUserService().getUser("newOwner");

        service.getUserService().register("newManager","by3");
        newManager=service.getUserService().getUser("newManager");

        service.getUserService().register("notOwner","by4");
        notOwner=service.getUserService().getUser("notOwner");

        service.getStoreService().addStore("yairStore","yair12312",subscriber.getToken());
        store=service.getStoreService().getStore("0");
    }

    @Test
    public void makeStoreOwnerTest(){
        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.messageResponse(newOwner.getUsername(), true, newOwner.getToken());
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(storeService.isStoreOwner(store.getId(), newOwner.getUsername()));
    }

    @Test
    public void makeStoreManagerTest(){
        Response<String> response = userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.messageResponse(newManager.getUsername(), true, newManager.getToken());
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(storeService.isStoreManager(store.getId(), newManager.getUsername()));
    }

    @Test
    public void makeStoreOwnerDeclineTest(){
        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.messageResponse(newOwner.getUsername(), false, newOwner.getToken());
        Assert.assertTrue(response1.isSuccess());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), newOwner.getUsername()));
    }

    @Test
    public void makeStoreManagerDeclineTest(){
        Response<String> response = userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.messageResponse(newManager.getUsername(), false, newManager.getToken());
        Assert.assertTrue(response1.isSuccess());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), newManager.getUsername()));
    }

    @Test
    public void notOwnerMakeStoreOwnerTest(){
        Response<String> response = service.makeStoreOwner(store.getId(), notOwner.getUsername(), newOwner.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void notOwnerMakeStoreManagerTest(){
        Response<String> response = userService.makeStoreManager(store.getId(), notOwner.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeExistingStoreOwnerTest(){
        service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
        userService.messageResponse(newOwner.getUsername(), true, newOwner.getToken());
        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeExistingStoreManagerTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, newManager.getToken());
        Response<String> response = userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionSuccessTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, newManager.getToken());
        Response<String> response = service.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "ADD_PRODUCT", subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void removePermissionSuccessTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, newManager.getToken());
        Response<String> response = service.removeManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void addPermissionNotOwnerTest(){
        Response<String> response = service.addManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotOwnerTest(){
        Response<String> response = service.removeManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "EDIT_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionNotManagerTest(){
        Response<String> response = service.addManagerPermissions(store.getId(), newOwner.getUsername(), subscriber.getUsername(), "EDIT_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotManagerTest(){
        Response<String> response = service.removeManagerPermissions(store.getId(), newOwner.getUsername(), subscriber.getUsername(), "EDIT_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeStoreOwnerNonExistentUserTest(){
        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), "nonExistentUser", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeStoreManagerNonExistentUserTest(){
        Response<String> response = userService.makeStoreManager(store.getId(), subscriber.getUsername(), "nonExistentUser", Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionNonExistentUserTest(){
        Response<String> response = service.addManagerPermissions(store.getId(), subscriber.getUsername(), "nonExistentUser", "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNonExistentUserTest(){
        Response<String> response = service.removeManagerPermissions(store.getId(), subscriber.getUsername(), "nonExistentUser", "permission", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addNonExistentPermissionTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, subscriber.getToken());
        Response<String> response = service.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "NON_EXISTENT_PERMISSION", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removeNonExistentPermissionTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, subscriber.getToken());
        Response<String> response = service.removeManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "NON_EXISTENT_PERMISSION", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void closeStoreSuccessTest(){
        Response<String> response = service.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void closeStoreNotCreatorTest(){
        Response<String> response = service.closeStore(store.getId(), notOwner.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void closeNonExistentStoreTest(){
        Response<String> response = service.closeStore("nonExistentStoreId", subscriber.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void closeAlreadyClosedStoreTest(){
        service.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Response<String> response = service.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }
}