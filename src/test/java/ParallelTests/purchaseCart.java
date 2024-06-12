package ParallelTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Users.Subscriber.Subscriber;
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
import java.util.concurrent.*;

public class purchaseCart {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber owner;
    OrderService orderService;
    ArrayList<String> threadsOrder;

    @Before
    public void init() {
        ServiceInitializer.reset();
        threadsOrder = new ArrayList<>();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        orderService = serviceInitializer.getOrderService();
        userService.register("yair12312", "Password123!");
        userService.loginAsSubscriber("yair12312", "Password123!");
        owner = userService.getUserFacade().getUserRepository().getUser("yair12312");
        storeService.addStore("newStore0", "yair12312", owner.getToken());
        storeService.addStore("newStore1", "yair12312", owner.getToken());
        storeService.addProductToStore("0", "newOProduct1", "DOG", 5, 3, "yair12312", owner.getToken());
        storeService.addProductToStore("0", "newOProduct2", "DOG", 10, 3, "yair12312", owner.getToken());
        storeService.addProductToStore("1", "newOProduct3", "DOG", 3, 3, "yair12312", owner.getToken());
    }

    @Test
    public void purchaseCartTest() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Response<String>>> futures = new ArrayList<>();
        List<String> userNames = new ArrayList<>();
        List<String> tokens = new ArrayList<>();


        // Create 3 users
        for (int i = 1; i <= 3; i++) {
            String userName = "user" + i;
            userService.register(userName, userName + "Pass123!");
            userService.loginAsSubscriber(userName, userName + "Pass123!");
            Subscriber newSubscriber = userService.getUserFacade().getUserRepository().getUser(userName);
            userService.addProductToShoppingCart("0", "1", "user" + i, newSubscriber.getToken(), 3);
            userService.addProductToShoppingCart("0", "2", "user" + i, newSubscriber.getToken(), 3);
            userService.addProductToShoppingCart("1", "1", "user" + i, newSubscriber.getToken(), 3);

            userNames.add(userName);
            tokens.add(newSubscriber.getToken());
        }
        for (int i = 0; i < 10; i++) {
            final int productId = i + 1;
            final String userName = userNames.get(i % userNames.size());
            final String token = tokens.get(i % tokens.size());
            int finalI = i;
            Callable<Response<String>> task = () -> {
                // Log the thread name and action
                System.out.println("Thread " + Thread.currentThread().getName() + " locked cart and calculated price for user: " + userName);

                // Lock the cart and calculate the price
                Response<String> response = userService.LockShoppSingCartAndCalculatedPrice("user" + finalI, token);


                // Log the response
                System.out.println("Thread " + Thread.currentThread().getName() + " added " + response.getData());
                threadsOrder.add(Thread.currentThread().getName());

                return response;
            };
            futures.add(executor.submit(task));
        }

        int counter = 0;

        // Retrieve and verify the results
        for (Future<Response<String>> future : futures) {
            Response<String> response = future.get();
            if (response.isSuccess()){
                counter++;
            };
        }
        Assert.assertTrue(counter== 1 || counter ==0);
    }

}
