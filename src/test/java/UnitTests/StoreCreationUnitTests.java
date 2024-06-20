package UnitTests;

import Domain.Repo.StoreRepository;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoreCreationUnitTests {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, subscriber2;
    Store store, store2;
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
        subscriber = userService.getUserFacade().getUserRepository().getUser("yair12312");
    }

    @Test
    public void testStoreCreation() {
        storeService.addStore("newStore", "yair12312", subscriber.getToken());
        store = storeRepository.getStore(0);
        Assertions.assertNotNull(store);
    }


    @Test
    public void testStoreOwner() {
        storeService.addStore("newStore", "yair12312", subscriber.getToken());
        Assertions.assertEquals(storeRepository.getStores().size(), 1);
        store = storeRepository.getStore(0);
        Assertions.assertFalse(storeService.isStoreOwner(store.getId(), "yair12312"));
    }

    @Test
    public void testStoreManager() {
        storeService.addStore("newStore", "yair12312", subscriber.getToken());
        Assertions.assertEquals(storeRepository.getStores().size(), 1);
        store = storeRepository.getStore(0);
        Assertions.assertFalse(storeService.isStoreManager(store.getId(), "yair12312"));
    }

    @Test
    public void testStoreCreator() {
        storeService.addStore("newStore", "yair12312", subscriber.getToken());
        Assertions.assertEquals(storeRepository.getStores().size(), 1);
        store = storeRepository.getStore(0);
        Assertions.assertTrue(storeService.isStoreCreator(store.getId(), "yair12312"));
    }


    @Test
    public void testStoreID() {
        storeService.addStore("newStore", "yair12312", subscriber.getToken());
        storeService.addStore("newStore", "yair12312", subscriber.getToken());
        Assertions.assertEquals(storeRepository.getStores().size(), 2);
        Assertions.assertTrue(storeRepository.getStores().containsKey(0) && storeRepository.getStores().containsKey(1));
    }

    @Test
    public void closeStoreSuccessTest(){
        storeService.addStore("yairStore", "yair12312", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(0);

        storeService.closeStore(0, subscriber.getUsername(), subscriber.getToken());
        Assertions.assertTrue(storeService.getStoreFacade().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isOpenedStore(store.getId()));
    }

    @Test
    public void closeStoreNotCreatorTest(){
        storeService.addStore("yairStore", "yair12312", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(0);
        userService.register("tomer", "Password123!");
        userService.loginAsSubscriber("tomer", "Password123!");
        subscriber2 = userService.getUserFacade().getUserRepository().getUser("tomer");

        storeService.closeStore(store.getId(), subscriber2.getUsername(), subscriber.getToken());
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertTrue(storeService.getStoreFacade().getStoreRepository().isOpenedStore(store.getId()));
    }

    @Test
    public void closeNonExistentStoreTest(){
        storeService.addStore("yairStore", "yair12312", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(0);

        storeService.closeStore(10, subscriber.getUsername(), subscriber.getToken());
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isClosedStore(10));
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isOpenedStore(10));
    }

    @Test
    public void closeAlreadyClosedStoreTest(){
        storeService.addStore("yairStore", "yair12312", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(0);

        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assertions.assertTrue(storeService.getStoreFacade().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertFalse(storeService.getStoreFacade().getStoreRepository().isOpenedStore(store.getId()));
    }
}
