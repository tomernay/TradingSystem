package UnitTests;

import Domain.Repo.StoreRepository;
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
    StoreRepository storeRepository;

    @BeforeEach
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        storeRepository = storeService.getStoreFacade().getStoreRepository();
        userService.register("yair12312", "Password123!");
        userService.loginAsSubscriber("yair12312", "Password123!");
        userService.register("newOwner", "Password123!");
        userService.loginAsSubscriber("newOwner", "Password123!");
        userService.register("newManager", "Password123!");
        userService.loginAsSubscriber("newManager", "Password123!");
        userService.register("notOwner", "Password123!");
        userService.loginAsSubscriber("notOwner", "Password123!");

        subscriber = userService.getUserFacade().getUserRepository().getUser("yair12312");
        newOwner = userService.getUserFacade().getUserRepository().getUser("newOwner");
        newManager = userService.getUserFacade().getUserRepository().getUser("newManager");
        notOwner = userService.getUserFacade().getUserRepository().getUser("notOwner");

        storeService.addStore("yairStore", "yair12312", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

    @Test
    public void testStoreCreation() {
        Response<String> response = storeService.addStore("newStore", "newOwner", newOwner.getToken());
        Assertions.assertTrue(response.isSuccess());
        store = storeRepository.getStore(response.getData());
        Assertions.assertNotNull(store);
    }


    @Test
    public void testStoreOwner() {
//        init();
        Response<String> response = storeService.addStore("newStore", "newOwner", newOwner.getToken());
        store = storeRepository.getStore(response.getData());
        Assertions.assertFalse(storeService.isStoreOwner(store.getId(), "newOwner"));
    }

    @Test
    public void testStoreManager() {
//        init();
        Response<String> response = storeService.addStore("newStore", "newOwner", newOwner.getToken());
        store = storeRepository.getStore(response.getData());
        Assertions.assertFalse(storeService.isStoreManager(store.getId(), "newOwner"));
    }

    @Test
    public void testStoreCreator() {
//        init();
        Response<String> response = storeService.addStore("newStore", "newOwner", newOwner.getToken());
        store = storeRepository.getStore(response.getData());
        Assertions.assertTrue(storeService.isStoreCreator(store.getId(), "newOwner"));
    }


    @Test
    public void testStoreID() {
//        init();
        Response<String> response = storeService.addStore("newStore", "newOwner", newOwner.getToken());
        store = storeRepository.getStore(response.getData());
        Response<String> response2 = storeService.addStore("newStore", "newOwner", newOwner.getToken());
        store = storeRepository.getStore(response2.getData());
        Assertions.assertNotEquals(response.getData(), response2.getData());
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
