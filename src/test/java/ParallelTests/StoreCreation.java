package ParallelTests;

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
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Future<Response<Integer>> future1 = executorService.submit(() -> storeService.addStore("store1", "mia", userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken()));
        Future<Response<Integer>> future2 = executorService.submit(() -> storeService.addStore("store2", "mia", userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken()));
        Future<Response<Integer>> future3 = executorService.submit(() -> storeService.addStore("store3", "mia", userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken()));

        Response<Integer> res1 = future1.get();
        Response<Integer> res2 = future2.get();
        Response<Integer> res3 = future3.get();

        System.out.println(res1.getData());
        System.out.println(res2.getData());
        System.out.println(res3.getData());
        assertNotEquals(res1.getData(), res2.getData());
        assertNotEquals(res2.getData(), res3.getData());
        assertNotEquals(res1.getData(), res3.getData());

        executorService.shutdown();

    }


}
