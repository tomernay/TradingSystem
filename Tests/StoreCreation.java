package Tests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
//import Service.Service;
import Service.*;
import Utilities.Response;
import org.junit.Assert;

public class StoreCreation {

    Service service;
    StoreService storeService;
    Subscriber subscriber;
    Store store;

    public void init(){
        service = new Service();
        service.getUserService().register("mia","22");
        subscriber=service.getUserService().getUser("mia");
        storeService = service.getStoreService();


    }

    public void testStoreCreation(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        store = storeService.getStore(response.getData());
        Assert.assertNotNull(store);
    }

    public void testStoreOwner(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStore(response.getData());
        Assert.assertFalse(storeService.isStoreOwner(store.getId(), "mia"));
    }
    //TODO - is the creator become the owner or manager immediately after creating the store?

    public void testStoreManager(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStore(response.getData());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), "mia"));
    }
    //TODO - is the creator become the owner or manager immediately after creating the store?

    public void testStoreCreator(){
        init();
        Response<String> response = storeService.addStore("ziv", "mia",subscriber.getToken());
        store = storeService.getStore(response.getData());
        Assert.assertTrue(storeService.isStoreCreator(store.getId(), "mia"));
    }


    public static void main(String[] args) {
        StoreCreation test = new StoreCreation();
        test.testStoreCreation();
        test.testStoreOwner();
        test.testStoreManager();
        test.testStoreCreator();
    }


}
