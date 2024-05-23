package UnitTests;

import Domain.Market.Market;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoreCreationUnitTests {
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, newOwner, newManager, notOwner;
    Store store;

    @BeforeEach
    public void init() {
        Market.getInstance().reset();
        storeService = new StoreService();
        userService = new UserService();
        userService.register("yair12312", "password123");
        userService.register("newOwner", "by2");
        userService.register("newManager", "by3");
        userService.register("notOwner", "by4");

        subscriber = Market.getInstance().getUser("yair12312");
        newOwner = Market.getInstance().getUser("newOwner");
        newManager = Market.getInstance().getUser("newManager");
        notOwner = Market.getInstance().getUser("notOwner");

        storeService.addStore("yairStore", "yair12312", subscriber.getToken());
        store = Market.getInstance().getStore("0");
    }

    @Test
    public void closeStoreSuccessTest(){
        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assertions.assertTrue(storeService.getMarket().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertFalse(storeService.getMarket().getStoreRepository().isOpenedStore(store.getId()));
    }

    @Test
    public void closeStoreNotCreatorTest(){
        Response<String> response = storeService.closeStore(store.getId(), notOwner.getUsername(), subscriber.getToken());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertFalse(storeService.getMarket().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertTrue(storeService.getMarket().getStoreRepository().isOpenedStore(store.getId()));
    }

    @Test
    public void closeNonExistentStoreTest(){
        Response<String> response = storeService.closeStore("nonExistentStoreId", subscriber.getUsername(), subscriber.getToken());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertFalse(storeService.getMarket().getStoreRepository().isClosedStore("nonExistentStoreId"));
        Assertions.assertFalse(storeService.getMarket().getStoreRepository().isOpenedStore("nonExistentStoreId"));
    }

    @Test
    public void closeAlreadyClosedStoreTest(){
        storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Response<String> response = storeService.closeStore(store.getId(), subscriber.getUsername(), subscriber.getToken());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(storeService.getMarket().getStoreRepository().isClosedStore(store.getId()));
        Assertions.assertFalse(storeService.getMarket().getStoreRepository().isOpenedStore(store.getId()));
    }
}
