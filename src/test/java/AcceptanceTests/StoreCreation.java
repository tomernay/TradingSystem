package AcceptanceTests;

import Service.ServiceInitializer;
import org.junit.Before;
import org.junit.Test;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;

public class StoreCreation {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, notOwner;
    Store store;

    @Before
    public void init(){
        serviceInitializer = new ServiceInitializer();
        userService = serviceInitializer.getUserService();
        userService.register("mia","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getUser("mia");
        storeService = serviceInitializer.getStoreService();
        userService.register("notOwner","Password123!");
        notOwner=userService.getUserFacade().getUserRepository().getUser("notOwner");
        storeService.addStore("newStore", "mia",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

    @Test
    public void testStoreCreation(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        store = storeService.getStoreFacade().getStoreRepository().getStore(response.getData());
        Assert.assertNotNull(store);
    }

    @Test
    public void testStoreOwner(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(response.getData());
        Assert.assertFalse(storeService.isStoreOwner(store.getId(), "mia"));
    }

    @Test
    public void testStoreManager(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(response.getData());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), "mia"));
    }

    @Test
    public void testStoreCreator(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(response.getData());
        Assert.assertTrue(storeService.isStoreCreator(store.getId(), "mia"));
    }

    //test that two stores have different IDs
    @Test
    public void testStoreID(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(response.getData());
        Response<String> response2 = storeService.addStore("Dor", "mia",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(response2.getData());
        Assert.assertNotEquals(response.getData(), response2.getData());
    }

    @Test
    public void closeStoreSuccessTest(){
        Response<String> response = storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void closeStoreNotCreatorTest(){
        Response<String> response = storeService.closeStore(store.getId(), notOwner.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void closeNonExistentStoreTest(){
        Response<String> response = storeService.closeStore("nonExistentStoreId", subscriber.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void closeAlreadyClosedStoreTest(){
        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Response<String> response = storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }


}
