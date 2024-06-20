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

public class InventoryRemoveProductTest {

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
        store = storeService.getStoreFacade().getStoreRepository().getStore(0);

        // Adding 5 products to the store
        for (int i = 1; i <= 5; i++) {
            storeService.addProductToStore(
                    store.getId(),
                    "product" + i,
                    "description of product" + i,
                    10.0 * i,
                    10 * i,
                    "itay",
                    subscriber.getToken()
            );
        }
    }

    @Test
    public void removeProductTest() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(7);
        List<Future<Response<String>>> futures = new ArrayList<>();
        List<String> userNames = new ArrayList<>();
        List<String> tokens = new ArrayList<>();

        // Create 7 users and nominate them as store owners
        for (int i = 1; i <= 7; i++) {
            String userName = "user" + i;
            userService.register(userName, userName + "Pass123!");
            userService.loginAsSubscriber(userName, userName + "Pass123!");
            Subscriber newSubscriber = userService.getUserFacade().getUserRepository().getUser(userName);
            Response<Integer> response = userService.SendOwnerNominationRequest(store.getId(), "itay", userName, subscriber.getToken());
            userService.ownerNominationResponse(response.getData(), userName, true, newSubscriber.getToken());

            userNames.add(userName);
            tokens.add(newSubscriber.getToken());
        }

        // Create and submit tasks to remove products using different users
        for (int i = 1; i <= 7; i++) {
            final int productId = i;
            final String userName = userNames.get(i % userNames.size());
            final String token = tokens.get(i % tokens.size());
            int finalI = i;
            Callable<Response<String>> task = () -> {
                // Log the thread name and action
                System.out.println("Thread " + Thread.currentThread().getName() + " is trying to remove product" + productId + " by the user: " + userName);

                // Remove product from the store
                Response<String> response = storeService.removeProductFromStore(
                        finalI,
                        store.getId(),
                        userName,
                        token
                );

                // Log the response
                if(response.getData() != null) {
                    System.out.println("User: " + userName + " by the Thread " + Thread.currentThread().getName() + " removed " + response.getData());
                    threadsOrder.add(Thread.currentThread().getName());
                }

                return response;
            };
            futures.add(executor.submit(task));
        }

        // List to store the product removal responses
        List<String> removalResponses = new ArrayList<>();

        // Retrieve and verify the results
        for (Future<Response<String>> future : futures) {
            Response<String> response = future.get();
            Assert.assertTrue(response.isSuccess() || response.getMessage().contains("Product with ID: 6 does not exist.") || response.getMessage().contains("Product with ID: 7 does not exist."));
            removalResponses.add(response.getData() == null ? response.getMessage() : response.getData());
        }

        // Print the removal responses to see the order and results
        System.out.println("Product removal responses: " + removalResponses);

        // Shut down the executor service
        executor.shutdown();

        // Get all remaining products from the store and verify
        Response<ArrayList<ProductDTO>> allProductsResponse = storeService.getAllProductsFromStore(store.getId(), "itay", subscriber.getToken());
        ArrayList<ProductDTO> allProducts = allProductsResponse.getData();

        System.out.println("All remaining products in the store:");
        if(allProducts.isEmpty()) {
            System.out.println("No products left in the store.");
            System.out.println("Threads order: " + threadsOrder);
        }
        else {
            for (ProductDTO product : allProducts) {
                System.out.println(product);
            }
        }
    }
}
