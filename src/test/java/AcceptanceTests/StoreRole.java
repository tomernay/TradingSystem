package AcceptanceTests;

import Service.StoreService;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class StoreRole {
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, newOwner, newManager, notOwner;
    Store store;

    @Before
    public void init(){

        storeService=new StoreService();
        userService=new UserService();
        userService.register("yair12312","password123");
        subscriber=userService.getUser("yair12312");

        userService.register("newOwner","by2");
        newOwner=userService.getUser("newOwner");

        userService.register("newManager","by3");
        newManager=userService.getUser("newManager");

        userService.register("notOwner","by4");
        notOwner=userService.getUser("notOwner");

        storeService.addStore("yairStore","yair12312",subscriber.getToken());
        store=storeService.getStore("0");
    }

    @Test
    public void makeStoreOwnerTest(){
        Response<String> response = userService.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
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
        Response<String> response = userService.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
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
        Response<String> response = userService.makeStoreOwner(store.getId(), notOwner.getUsername(), newOwner.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void notOwnerMakeStoreManagerTest(){
        Response<String> response = userService.makeStoreManager(store.getId(), notOwner.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeExistingStoreOwnerTest(){
        userService.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
        userService.messageResponse(newOwner.getUsername(), true, newOwner.getToken());
        Response<String> response = userService.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername(), subscriber.getToken());
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
        Response<String> response = storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "ADD_PRODUCT", subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void removePermissionSuccessTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, newManager.getToken());
        Response<String> response = storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void addPermissionNotOwnerTest(){
        Response<String> response = storeService.addManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotOwnerTest(){
        Response<String> response = storeService.removeManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "EDIT_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionNotManagerTest(){
        Response<String> response = storeService.addManagerPermissions(store.getId(), newOwner.getUsername(), subscriber.getUsername(), "EDIT_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotManagerTest(){
        Response<String> response = storeService.removeManagerPermissions(store.getId(), newOwner.getUsername(), subscriber.getUsername(), "EDIT_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeStoreOwnerNonExistentUserTest(){
        Response<String> response = userService.makeStoreOwner(store.getId(), subscriber.getUsername(), "nonExistentUser", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeStoreManagerNonExistentUserTest(){
        Response<String> response = userService.makeStoreManager(store.getId(), subscriber.getUsername(), "nonExistentUser", Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionNonExistentUserTest(){
        Response<String> response = storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), "nonExistentUser", "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNonExistentUserTest(){
        Response<String> response = storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), "nonExistentUser", "permission", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addNonExistentPermissionTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, subscriber.getToken());
        Response<String> response = storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "NON_EXISTENT_PERMISSION", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removeNonExistentPermissionTest(){
        userService.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.messageResponse(newManager.getUsername(), true, subscriber.getToken());
        Response<String> response = storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "NON_EXISTENT_PERMISSION", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }


}