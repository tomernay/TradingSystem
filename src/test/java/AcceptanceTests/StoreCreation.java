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
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        userService.register("miaa","Password123!");
        userService.loginAsSubscriber("miaa","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getSubscriber("miaa");
        storeService = serviceInitializer.getStoreService();
        userService.register("notOwner","Password123!");
        userService.loginAsSubscriber("notOwner","Password123!");
        notOwner=userService.getUserFacade().getUserRepository().getSubscriber("notOwner");
        storeService.addStore("newStore", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(0);
    }

    @Test
    public void testStoreCreation(){
        Response<Integer> response = storeService.addStore("zivv", "miaa",subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(response.getData());
        Assert.assertNotNull(store);
    }

    @Test
    public void testStoreOwner(){
        init();
        Response<Integer> response = storeService.addStore("zivv", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(response.getData());
        Assert.assertFalse(storeService.isStoreOwner(store.getId(), "miaa"));
    }

    @Test
    public void testStoreManager(){
        init();
        Response<Integer> response = storeService.addStore("zivv", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(response.getData());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), "miaa"));
    }

    @Test
    public void testStoreCreator(){
        init();
        Response<Integer> response = storeService.addStore("zivv", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(response.getData());
        Assert.assertTrue(storeService.isStoreCreator(store.getId(), "miaa"));
    }

    //test that two stores have different IDs
    @Test
    public void testStoreID(){
        init();
        Response<Integer> response = storeService.addStore("zivv", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(response.getData());
        Response<Integer> response2 = storeService.addStore("Dorr", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(response2.getData());
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
        Response<String> response = storeService.closeStore(10, subscriber.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void closeAlreadyClosedStoreTest(){
        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Response<String> response = storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }


}
