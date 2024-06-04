package AcceptanceTests;

import Service.OrderService;
import Service.ServiceInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoRequestsByOwnerAT {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    OrderService orderService;
    Response<String> store;



    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        orderService = serviceInitializer.getOrderService();
        userService.register("mia","Password123!");
        userService.loginAsSubscriber("mia","Password123!");
        store = storeService.addStore("newStore", "mia",userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        //make mia the store owner
//        storeService.addManagerPermissions(store.getData(), "mia", "ziv", "MANAGER", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
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
//        perms.add("ADD_MANAGER");
        perms.add("EDIT_PRODUCT");
        userService.SendStoreManagerNomination(store.getData(), "mia", "ziv", perms, userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        userService.managerNominationResponse("ziv",true, userService.getUserFacade().getUserRepository().getUser("ziv").getToken());

    }

    public void initSubscribers(){
        //subscribe ziv
        userService.register("ziv","Password123!");
        userService.loginAsSubscriber("ziv","Password123!");
        userService.SendStoreOwnerNomination(store.getData(), "mia", "ziv", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        userService.ownerNominationResponse("ziv",true, userService.getUserFacade().getUserRepository().getUser("ziv").getToken());

        //subscribe dor
        userService.register("dor","Password123!");
        userService.loginAsSubscriber("dor","Password123!");
        userService.SendStoreOwnerNomination(store.getData(), "mia", "dor", userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        userService.ownerNominationResponse("dor",true, userService.getUserFacade().getUserRepository().getUser("dor").getToken());

        //subscribe niv
        userService.register("niv","Password123!");
        userService.loginAsSubscriber("niv","Password123!");

    }

    public void initProducts(){
        //add product
    }

    @Test
    public void testSubscribersListNoSubscribersToTheStore(){
//        Response <Map<String, String>> response = userService.requestEmployeesStatus(store.getId(),"miaa" ,subscriber.getToken());
        Response <Map<String, String>> response = userService.requestEmployeesStatus(store.getData() ,"mia" ,userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        Assert.assertTrue(response.isSuccess()); //creator is a subscriber
        Assert.assertEquals(response.getData().size(),1);
    }

    @Test
    public void testSubscribersList(){
        initSubscribers();
        Response <Map<String, String>> response = userService.requestEmployeesStatus(store.getData() ,"mia" ,userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),3);
    }

    @Test
    public void testManagersListNoAddedManagersToTheStore(){
       // Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getId(),"miaa" ,subscriber.getToken());
        Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getData() ,"mia" ,userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),0);
    }

    @Test
    public void testManagersList(){
        initManagers();
        Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getData() ,"mia" ,userService.getUserFacade().getUserRepository().getUser("mia").getToken());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),1);
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
//    test order history with orders
//    @Test
//    public void testOrderHistoryWithOrders(){
//        storeService.addProductToStore("newStore","newProduct","",10, 1,"newOwner",userService.getUserFacade().getUserRepository().getUser("mia").getToken());
//        Response<String> res=userService.addProductToShoppingCart("newStore","newProduct","ziv",userService.getUserFacade().getUserRepository().getUser("ziv").getToken(),1);
//        Response<String> res1 = orderService.purchaseCart(store.getData(), "ziv", "credit", "123456789", "12/22", "123", "123456789", "123456789", "123456789", "123456789", "123456789", userService.getUserFacade().getUserRepository().getUser("ziv").getToken());
//    }
    //test order history with orders


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



//    public static void main(String[] args) {
//        InfoRequestsByOwnerAT ownerInfoRequests = new InfoRequestsByOwnerAT();
//        ownerInfoRequests.init();
//        ownerInfoRequests.testSubscribersListNoSubscribersToTheStore();
//        ownerInfoRequests.testManagersListNoAddedManagersToTheStore();
//        ownerInfoRequests.testManagersList();
//        ownerInfoRequests.testSubscribersList();
//    }

}
