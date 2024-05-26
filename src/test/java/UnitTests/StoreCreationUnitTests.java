package UnitTests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoreCreationUnitTests {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, newOwner, newManager, notOwner;
    Store store;

    @BeforeEach
    public void init() {
        serviceInitializer = new ServiceInitializer();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        userService.register("yair12312", "password123");
        userService.register("newOwner", "by2");
        userService.register("newManager", "by3");
        userService.register("notOwner", "by4");

        subscriber = userService.getUserFacade().getUserRepository().getUser("yair12312");
        newOwner = userService.getUserFacade().getUserRepository().getUser("newOwner");
        newManager = userService.getUserFacade().getUserRepository().getUser("newManager");
        notOwner = userService.getUserFacade().getUserRepository().getUser("notOwner");

        storeService.addStore("yairStore", "yair12312", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

    @Test
    public void closeStoreSuccessTest(){
        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assertions.assertTrue(storeService.getStoreFacade().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isOpenedStore(store.getId()));
    }

    @Test
    public void closeStoreNotCreatorTest(){
        Response<String> response = storeService.closeStore(store.getId(), notOwner.getUsername(), subscriber.getToken());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertTrue(storeService.getStoreFacade().getStoreRepository().isOpenedStore(store.getId()));
    }

    @Test
    public void closeNonExistentStoreTest(){
        Response<String> response = storeService.closeStore("nonExistentStoreId", subscriber.getUsername(), subscriber.getToken());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isClosedStore("nonExistentStoreId"));
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isOpenedStore("nonExistentStoreId"));
    }

    @Test
    public void closeAlreadyClosedStoreTest(){
        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Response<String> response = storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(storeService.getStoreFacade().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isOpenedStore(store.getId()));
    }
}
