package ParallelTests;

import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InfoRequestsByManager {

    //write tests to check if the manager can request
    // the employees status and the managers permissions from different threads while adding permissions to a manager

    ServiceInitializer serviceInitializer;
    UserService userService;
    OrderService orderService;
    StoreService storeService;
    Response<String> store;


    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        orderService = serviceInitializer.getOrderService();
        storeService = serviceInitializer.getStoreService();
        userService.register("mia","Password123!");
        userService.loginAsSubscriber("mia","Password123!");
        store = storeService.addStore("newStore", "mia",userService.getUserFacade().getUserRepository().getUser("mia").getToken());
    }


    public void initManagers(){
        //subscribe ziv
        userService.register("ziv","Password123!");
        userService.loginAsSubscriber("ziv","Password123!");
        //make ziv manager
        List<String> perms = new ArrayList<>();
        perms.add("EDIT_PRODUCT");
//        perms.add("ADD_MANAGER");
        perms.add("EDIT_PRODUCT");
        userService.SendStoreManagerNomination("0", "mia", "ziv", perms, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        userService.managerNominationResponse("ziv",true, userService.getUserFacade().getUserRepository().getUser("ziv").getToken());

    }

    public void initSubscribers(){
        //subscribe ziv
//        userService.register("ziv","Password123!");
//        userService.loginAsSubscriber("ziv","Password123!");
//        userService.SendStoreOwnerNomination(store.getData(), "mia", "ziv", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        userService.ownerNominationResponse("ziv",true, userService.getUserFacade().getUserRepository().getUser("ziv").getToken());

        //subscribe dor
        userService.register("dor","Password123!");
        userService.loginAsSubscriber("dor","Password123!");
        userService.SendStoreOwnerNomination("0", "mia", "dor", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        userService.ownerNominationResponse("dor",true, userService.getUserFacade().getUserRepository().getUser("dor").getToken());

        //subscribe niv
        userService.register("niv","Password123!");
        userService.loginAsSubscriber("niv","Password123!");

    }

    @Test
    public void testManagerPermissions() throws Exception {
        initManagers();
        initSubscribers();
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to get the employees status

        Future<Response<Map<String, String>>> future1 = executorService.submit(() -> userService.requestEmployeesStatus("0", "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));
        //add niv as an owner in a future task
        Future<Response<String>> future3 = executorService.submit(() -> userService.SendStoreOwnerNomination("0", "mia", "niv", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));
        Future<Response<String>> future4 = executorService.submit(() -> userService.ownerNominationResponse("niv",true, userService.getUserFacade().getUserRepository().getUser("niv").getToken()));
        Future<Response<Map<String, String>>> future2 = executorService.submit(() -> userService.requestEmployeesStatus("0", "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));


//        userService.SendStoreOwnerNomination(store.getData(), "mia", "niv", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        userService.ownerNominationResponse("niv",true, userService.getUserFacade().getUserRepository().getUser("niv").getToken());


        // Get the responses. This will block until the tasks are complete.
        Response<Map<String, String>> response1 = future1.get();
        Response<Map<String, String>> response2 = future2.get();
        Response<String> response3 = future3.get();
        Response<String> response4 = future4.get();

        // Assert that both requests were successful
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(response2.isSuccess());
        Assert.assertTrue(response3.isSuccess());
        Assert.assertTrue(response4.isSuccess());

        // check that the first response has 1 subscriber and the second has 2 subscribers
        Assert.assertEquals(response1.getData().size(), 3);
        Assert.assertEquals(response2.getData().size(), 4);
        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    public void testSubscribersList() throws Exception {
        initSubscribers();
        initManagers();
        // Create an executor service with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit two tasks to get the employees status

        Future<Response<Map<String, String>>> future1 = executorService.submit(() -> userService.requestEmployeesStatus("0", "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));
        //add niv as an owner in a future task
        Future<Response<String>> future3 = executorService.submit(() -> userService.SendStoreOwnerNomination("0", "mia", "niv", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));
        Future<Response<String>> future4 = executorService.submit(() -> userService.ownerNominationResponse("niv",true, userService.getUserFacade().getUserRepository().getUser("niv").getToken()));
        Future<Response<Map<String, String>>> future2 = executorService.submit(() -> userService.requestEmployeesStatus("0", "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken()));

        //make nir owner
//        userService.SendStoreOwnerNomination(store.getData(), "mia", "niv", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        userService.ownerNominationResponse("niv",true, userService.getUserFacade().getUserRepository().getUser("niv").getToken());


        // Get the responses. This will block until the tasks are complete.
        Response<Map<String, String>> response1 = future1.get();
        Response<Map<String, String>> response2 = future2.get();
        Response<String> response3 = future3.get();
        Response<String> response4 = future4.get();

        // Assert that both requests were successful
        Assert.assertTrue(response1.isSuccess());
        Assert.assertTrue(response2.isSuccess());
        Assert.assertTrue(response3.isSuccess());
        Assert.assertTrue(response4.isSuccess());

        // check that the first response has 1 subscriber and the second has 2 subscribers
        Assert.assertEquals(response1.getData().size(), 3);
        Assert.assertEquals(response2.getData().size(), 4);

        // Shut down the executor service
        executorService.shutdown();
    }
}
