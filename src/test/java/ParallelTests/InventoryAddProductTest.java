package ParallelTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InventoryAddProductTest {

    private Subscriber subscriber;
    private ServiceInitializer serviceInitializer;
    private Store store;
    private StoreService storeService;
    private UserService userService;
    ArrayList<String> threadsOrder;


    @Before
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();
        threadsOrder = new ArrayList<>();

        userService.register("itay", "ItayPass123!");
        userService.loginAsSubscriber("itay", "ItayPass123!");
        subscriber = userService.getUserFacade().getUserRepository().getUser("itay");
        storeService.addStore("itayStore", "itay", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

    @Test
    public void addProductTest() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Response<String>>> futures = new ArrayList<>();
        List<String> userNames = new ArrayList<>();
        List<String> tokens = new ArrayList<>();

        // Create 7 users and nominate them as store owners
        for (int i = 1; i <= 7; i++) {
            String userName = "user" + i;
            userService.register(userName, userName + "Pass123!");
            userService.loginAsSubscriber(userName, userName + "Pass123!");
            Subscriber newSubscriber = userService.getUserFacade().getUserRepository().getUser(userName);
            userService.SendOwnerNominationRequest(store.getId(), "itay", userName, subscriber.getToken());
            userService.ownerNominationResponse(userName, true, newSubscriber.getToken());

            userNames.add(userName);
            tokens.add(newSubscriber.getToken());
        }

        // Create and submit tasks to add products using different users
        for (int i = 0; i < 10; i++) {
            final int productId = i + 1;
            final String userName = userNames.get(i % userNames.size());
            final String token = tokens.get(i % tokens.size());
            int finalI = i;
            Callable<Response<String>> task = () -> {
                // Log the thread name and action
                System.out.println("Thread " + Thread.currentThread().getName() + " is adding product" + productId + " by the user: " + userName);

                // Add product to the store
                Response<String> response = storeService.addProductToStore(
                        store.getId(),
                        "product" + productId,
                        "product" + Thread.currentThread().getName() + "Desc",
                        1.0 * productId,
                        1 * productId,
                        userName,
                        token
                );

                // Log the response
                System.out.println("Thread " + Thread.currentThread().getName() + " added " + response.getData());
                threadsOrder.add(Thread.currentThread().getName());

                return response;
            };
            futures.add(executor.submit(task));
        }

        // List to store the product names added
        List<String> productNames = new ArrayList<>();

        // Retrieve and verify the results
        for (Future<Response<String>> future : futures) {
            Response<String> response = future.get();
            Assert.assertTrue(response.isSuccess());
            productNames.add(response.getData());
        }

        // Print the product names to see the order they were added
        System.out.println("Products added in order: " + productNames);

        // Shut down the executor service
        executor.shutdown();

        // Get all products from the store and verify
        Response<ArrayList<ProductDTO>> allProductsResponse = storeService.getAllProductsFromStore(store.getId(), "itay", subscriber.getToken());
        ArrayList<ProductDTO> allProducts = allProductsResponse.getData();

        System.out.println("All products in the store:");
        for(ProductDTO product : allProducts) {
            System.out.println(product);
        }
        System.out.println("Threads order: " + threadsOrder);
        

    }

}
