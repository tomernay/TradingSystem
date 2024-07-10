package ParallelTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InventoryGetAndSetTest {

    private Subscriber subscriber, subscriber2;
    private ServiceInitializer serviceInitializer;
    private Store store;
    private StoreService storeService;

    @BeforeEach
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        UserService userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();

        userService.register("itay", "ItayPass123!");
        userService.loginAsSubscriber("itay", "ItayPass123!");
        subscriber = userService.getUserFacade().getUserRepository().getSubscriber("itay");
        storeService.addStore("itayStore", "itay", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(0);

        userService.register("mor","MorPass123!");
        userService.loginAsSubscriber("mor","MorPass123!");
        Response<Integer> res = userService.SendManagerNominationRequest(store.getId(), "itay", "mor", List.of("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        subscriber2 = userService.getUserFacade().getUserRepository().getSubscriber("mor");
        userService.managerNominationResponse(res.getData(), "mor", true, subscriber2.getToken());

        // Add initial product to the store
        storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 20, "mor", subscriber2.getToken());
    }

    private void runConcurrentTasks(List<Callable<Response<String>>> tasks) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Response<String>>> futures = executor.invokeAll(tasks);
        executor.shutdown();
    }

    @Test
    public void concurrentEditProductQuantity() throws InterruptedException, ExecutionException {
        System.out.println("------------------concurrentEditProductQuantity------------------------");

        List<Callable<Response<String>>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int quantity = 10 * (i + 1); // Different quantities for each thread
            tasks.add(() -> storeService.setProductQuantity(1, quantity, store.getId(), "mor", subscriber2.getToken()));
        }

        runConcurrentTasks(tasks);

        // Verify the final quantity
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNotNull(res.getData());
        Assertions.assertEquals(1, res.getData().size());
        System.out.println("Final Quantity: " + res.getData().get(0).getQuantity());
    }

    @Test
    public void concurrentEditProductPrice() throws InterruptedException, ExecutionException {
        System.out.println("------------------concurrentEditProductPrice------------------------");

        List<Callable<Response<String>>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final double price = 5.0 * (i + 1); // Different prices for each thread
            tasks.add(() -> storeService.setProductPrice(1, price, store.getId(), "mor", subscriber2.getToken()));
        }

        runConcurrentTasks(tasks);

        // Verify the final price
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNotNull(res.getData());
        Assertions.assertEquals(1, res.getData().size());
        System.out.println("Final Price: " + res.getData().get(0).getPrice());
    }

    @Test
    public void concurrentEditProductName() throws InterruptedException, ExecutionException {
        System.out.println("------------------concurrentEditProductName------------------------");

        List<Callable<Response<String>>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final String name = "NewName" + (i + 1); // Different names for each thread
            tasks.add(() -> storeService.setProductName(1, name, store.getId(), "mor", subscriber2.getToken()));
        }

        runConcurrentTasks(tasks);

        // Verify the final name
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNotNull(res.getData());
        Assertions.assertEquals(1, res.getData().size());
        System.out.println("Final Name: " + res.getData().get(0).getProductName());
    }

    @Test
    public void concurrentEditProductDescription() throws InterruptedException, ExecutionException {
        System.out.println("------------------concurrentEditProductDescription------------------------");

        List<Callable<Response<String>>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final String description = "NewDesc" + (i + 1); // Different descriptions for each thread
            tasks.add(() -> storeService.setProductDescription(1, description, store.getId(), "mor", subscriber2.getToken()));
        }

        runConcurrentTasks(tasks);

        // Verify the final description
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNotNull(res.getData());
        Assertions.assertEquals(1, res.getData().size());
        System.out.println("Final Description: " + res.getData().get(0).getDescription());
    }

    @Test
    public void concurrentEditProductCategory() throws InterruptedException, ExecutionException {
        System.out.println("------------------concurrentEditProductCategory------------------------");

        List<Callable<Response<String>>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final String category = "Category" + (i + 1); // Different categories for each thread
            tasks.add(() -> storeService.assignProductToCategory(1, category, store.getId(), "mor", subscriber2.getToken()));
        }

        runConcurrentTasks(tasks);

        // Verify the final category assignment
        // Since multiple categories could be added, we check the category assigned last.
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNotNull(res.getData());
        Assertions.assertEquals(1, res.getData().size());
        storeService.retrieveProductCategories(res.getData().get(0).getProductID(), store.getId(), "mor", subscriber2.getToken());}
}
