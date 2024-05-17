package Tests;

import Domain.Market.Market;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.Service;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StoreRole {
    Service service;
    Subscriber subscriber, newOwner, newManager, notOwner;
    Store store;

    @Before
    public void init(){
        service=new Service();
        service.getUserService().register("yair","by");
        subscriber=service.getUserService().getUser("yair");

        service.getUserService().register("newOwner","by2");
        newOwner=service.getUserService().getUser("newOwner");

        service.getUserService().register("newManager","by3");
        newManager=service.getUserService().getUser("newManager");

        service.getUserService().register("notOwner","by4");
        notOwner=service.getUserService().getUser("notOwner");

        service.getStoreService().addStore("yairStore","yair",subscriber.getToken());
        store=service.getStoreService().getStore("yairStore");
    }

    @Test
    public void makeStoreOwnerTest(){
        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void makeStoreManagerTest(){
        Response<String> response = service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void notOwnerMakeStoreOwnerTest(){
        Response<String> response = service.makeStoreOwner(store.getId(), notOwner.getUsername(), newOwner.getUsername());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void notOwnerMakeStoreManagerTest(){
        Response<String> response = service.makeStoreManager(store.getId(), notOwner.getUsername(), newManager.getUsername(), null);
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeExistingStoreOwnerTest(){
        service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername());
        Response<String> response = service.makeStoreOwner(store.getId(), subscriber.getUsername(), newOwner.getUsername());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeExistingStoreManagerTest(){
        service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
        Response<String> response = service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
        Assert.assertFalse(response.isSuccess());
    }

    public void addPermissionSuccessTest(){
        service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
        Response<String> response = service.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "permission");
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void removePermissionSuccessTest(){
        service.makeStoreManager(store.getId(), subscriber.getUsername(), newManager.getUsername(), null);
        service.addManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "permission");
        Response<String> response = service.removeManagerPermissions(store.getId(), subscriber.getUsername(), newManager.getUsername(), "permission");
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void addPermissionNotOwnerTest(){
        Response<String> response = service.addManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "permission");
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotOwnerTest(){
        Response<String> response = service.removeManagerPermissions(store.getId(), notOwner.getUsername(), newManager.getUsername(), "permission");
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionNotManagerTest(){
        Response<String> response = service.addManagerPermissions(store.getId(), subscriber.getUsername(), newOwner.getUsername(), "permission");
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotManagerTest(){
        Response<String> response = service.removeManagerPermissions(store.getId(), subscriber.getUsername(), newOwner.getUsername(), "permission");
        Assert.assertFalse(response.isSuccess());
    }
}