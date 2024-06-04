package ParallelTests;

import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotEquals;

public class StoreCreation {
    //test if two threads open a store at the same time the id is different
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Response<String> store;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        userService.register("mia","Password123!");
        userService.loginAsSubscriber("mia","Password123!");
    }


    @Test
    public void testStoreCreation() throws Exception{
        init();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Response<String>> future1 = executorService.submit(() -> storeService.addStore("store1", "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));
        Future<Response<String>> future2 = executorService.submit(() -> storeService.addStore("store2", "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));


        store = future1.get();
        String store1 = store.getData();
        store = future2.get();
        String store2 = store.getData();
        assertNotEquals(store1, store2);




    }
}
