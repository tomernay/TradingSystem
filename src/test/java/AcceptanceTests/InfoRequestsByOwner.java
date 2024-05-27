package AcceptanceTests;

import Service.OrderService;
import Service.ServiceInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Domain.Store.Store;
import Domain.Store.StoreData.Permissions;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoRequestsByOwner {
    ServiceInitializer serviceInitializer;
    //Subscriber subscriber;
    //Store store;
    StoreService storeService;
    UserService userService;
    OrderService orderService;
    Response<String> store;

    @Before
    public void init(){
        serviceInitializer = new ServiceInitializer();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        orderService = serviceInitializer.getOrderService();
        userService.register("mia","Password123!");
        userService.loginAsSubscriber("mia","Password123!");
        store = storeService.addStore("newStore", "mia",userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        //make mia the store owner
        storeService.addManagerPermissions(store.getData(), "mia", "ziv", "MANAGER", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        //subscriber = userService.getUserFacade().getUserRepository().getUser("miaa");
        //Response<String> response = storeService.addStore("ziv", "miaa", subscriber.getToken());
        //store = storeService.getStoreFacade().getStoreRepository().getStore(response.getData());
    }

    public void initManagers(){
        //subscribe ziv
        userService.register("ziv","Password123!");
        userService.loginAsSubscriber("ziv","Password123!");
        //make ziv manager
        List<String> perms = new ArrayList<>();
        perms.add("EDIT_PRODUCT");
        perms.add("ADD_MANAGER");
        perms.add("EDIT_PRODUCT");
        userService.SendStoreManagerNomination(store.getData(), "mia", "ziv", perms, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        userService.managerNominationResponse("ziv",true, userService.getUserFacade().getUserRepository().getUser("ziv").getToken());

    }

    @Test
    public void testSubscribersListNoSubscribersToTheStore(){
//        Response <Map<String, String>> response = userService.requestEmployeesStatus(store.getId(),"miaa" ,subscriber.getToken());
        Response <Map<String, String>> response = userService.requestEmployeesStatus(store.getData() ,"mia" ,userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void testManagersListNoAddedManagersToTheStore(){
       // Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getId(),"miaa" ,subscriber.getToken());
        Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getData() ,"mia" ,userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void testManagersList(){
        initManagers();
        Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getData() ,"mia" ,userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        Assert.assertTrue(response.isSuccess());
        //subscribe ziv
//        userService.register("ziv","Password123!");
//        userService.loginAsSubscriber("ziv","Password123!");
//        //make ziv manager
//        List<String> perms = new ArrayList<>();
//        perms.add("EDIT_PRODUCT");
//        perms.add("ADD_MANAGER");
//        perms.add("EDIT_PRODUCT");
////        storeService.nominateManager(store.getData(), "mia","ADD_MANAGER",
//        //nominate ziv to be a manager
//        userService.SendStoreManagerNomination(store.getData(), "mia", "ziv", perms, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        userService.managerNominationResponse("ziv",true, userService.getUserFacade().getUserRepository().getUser("ziv").getToken());
//        storeService.addManagerPermissions(store.getData(), "mia", "ziv", "ADD_MANAGER", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        storeService.addManagerPermissions(store.getData(), "mia", "ziv", "EDIT_PRODUCT", userService.getUserFacade().getUserRepository().getUser("mia").getToken());

    }

    //test order history
    //test order history with no orders
    @Test
    public void testOrderHistoryNoOrders(){
        Response<Map<String,String>> response = orderService.getOrderHistorty(store.getData());
        Assert.assertFalse(response.isSuccess());
    }
    //test order history with orders
//    @Test
//    public void testOrderHistoryWithOrders(){
//        //add product
//        storeService.addProduct(store.getData(), "mia", "product", 10, 10, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        //add product to cart
//        orderService.addProductToCart(store.getData(), "mia", "product", 1, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        //purchase cart
//        orderService.purchaseCart(store.getData(), "mia", "credit", "123456789", "12/22", "123", "123456789", "123456789", "123456789", "123456789", "123456789", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        Response<List<String>> response = orderService.requestOrderHistory(store.getData(), "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        Assert.assertTrue(response.isSuccess());
//    }
    //test order history with orders and products
//    @Test
//    public void testOrderHistoryWithOrdersAndProducts(){
//        //add product
//        storeService.addProduct(store.getData(), "mia", "product", 10, 10, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        //add product to cart
//        orderService.addProductToCart(store.getData(), "mia", "product", 1, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        //purchase cart
//        orderService.purchaseCart(store.getData(), "mia", "credit", "123456789", "12/22", "123", "123456789", "123456789", "123456789", "123456789", "123456789", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        Response<List<String>> response = orderService.requestOrderHistory(store.getData(), "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        Assert.assertTrue(response.isSuccess());
//    }
    //test order history with orders and products and prices
//    @Test
//    public void testOrderHistoryWithOrdersAndProductsAndPrices(){
//        //add product
//        storeService.addProduct(store.getData(), "mia", "product", 10, 10, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        //add product to cart
//        orderService.addProductToCart(store.getData(), "mia", "product", 1, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        //purchase cart
//        orderService.purchaseCart(store.getData(), "mia", "credit", "123456789", "12/22", "123", "123456789", "123456789", "123456789", "123456789", "123456789", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        Response<List<String>> response = orderService.requestOrderHistory(store.getData(), "mia", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        Assert.assertTrue(response.isSuccess());
//    }



    public static void main(String[] args) {
        InfoRequestsByOwner ownerInfoRequests = new InfoRequestsByOwner();
        ownerInfoRequests.init();
        ownerInfoRequests.testSubscribersListNoSubscribersToTheStore();
        ownerInfoRequests.testManagersListNoManagersToTheStore();
        ownerInfoRequests.testManagersList();
    }

}
