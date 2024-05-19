package src.Tests;

import org.junit.Before;
import org.junit.Test;
import src.main.java.Domain.Store.Store;
import src.main.java.Domain.Users.Subscriber.Subscriber;
//import Service.Service;
import src.main.java.Service.*;
import src.main.java.Utilities.Response;
import org.junit.Assert;

public class StoreCreation {

    Service service;
    StoreService storeService;
    Subscriber subscriber;
    Store store;

    @Before
    public void init(){
        service = new Service();
        service.getUserService().register("mia","22");
        subscriber=service.getUserService().getUser("mia");
        storeService = service.getStoreService();


    }

    @Test
    public void testStoreCreation(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        store = storeService.getStore(response.getData());
        Assert.assertNotNull(store);
    }

    @Test
    public void testStoreOwner(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStore(response.getData());
        Assert.assertFalse(storeService.isStoreOwner(store.getId(), "mia"));
    }

    @Test
    public void testStoreManager(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStore(response.getData());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), "mia"));
    }

    @Test
    public void testStoreCreator(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStore(response.getData());
        Assert.assertTrue(storeService.isStoreCreator(store.getId(), "mia"));
    }

    //test that two stores have different IDs
    @Test
    public void testStoreID(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStore(response.getData());
        Response<String> response2 = storeService.addStore("Dor", "mia",subscriber.getToken());
        store = storeService.getStore(response2.getData());
        Assert.assertNotEquals(response.getData(), response2.getData());
    }


}
