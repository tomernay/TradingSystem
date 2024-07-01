package AcceptanceTests;

import Service.OrderService;
import Service.ServiceInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoRequestsByOwnerAT {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    OrderService orderService;
    Response<Integer> store;



    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        orderService = serviceInitializer.getOrderService();
        userService.register("mia","Password123!");
        userService.loginAsSubscriber("mia","Password123!");
        store = storeService.addStore("newStore", "mia",userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
    }


    public void initManagers(){
        //subscribe ziv
        userService.register("ziv","Password123!");
        userService.loginAsSubscriber("ziv","Password123!");
        //make ziv manager
        List<String> perms = new ArrayList<>();
        perms.add("MANAGE_PRODUCTS");
//        perms.add("ADD_MANAGER");
        perms.add("MANAGE_PRODUCTS");
        Response<Integer> res = userService.SendManagerNominationRequest(0, "mia", "ziv", perms, userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        userService.managerNominationResponse(res.getData(), "ziv",true, userService.getUserFacade().getUserRepository().getSubscriber("ziv").getToken());

    }

    public void initSubscribers(){
        //subscribe ziv
        userService.register("ziv","Password123!");
        userService.loginAsSubscriber("ziv","Password123!");
        Response<Integer> res = userService.SendOwnerNominationRequest(0, "mia", "ziv", userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        userService.ownerNominationResponse(res.getData(), "ziv",true, userService.getUserFacade().getUserRepository().getSubscriber("ziv").getToken());

        //subscribe dor
        userService.register("dor","Password123!");
        userService.loginAsSubscriber("dor","Password123!");
        Response<Integer> res2 = userService.SendOwnerNominationRequest(1, "mia", "dor", userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        userService.ownerNominationResponse(res2.getData(),"dor",true, userService.getUserFacade().getUserRepository().getSubscriber("dor").getToken());

        //subscribe niv
        userService.register("niv","Password123!");
        userService.loginAsSubscriber("niv","Password123!");

    }

    @Test
    public void testGetOrdersHistoryNoOrders() {
        Response<Map<String,String>> response = serviceInitializer.getOrderService().getOrderFacade().getOrdersHistory(store.getData());
        Assert.assertFalse(response.isSuccess());

    }

    @Test
    public void testSubscribersListNoSubscribersToTheStore(){
//        Response <Map<String, String>> response = userService.requestEmployeesStatus(store.getId(),"miaa" ,subscriber.getToken());
        Response <Map<String, String>> response = userService.requestEmployeesStatus(0 ,"mia" ,userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        Assert.assertTrue(response.isSuccess()); //creator is a subscriber
        Assert.assertEquals(response.getData().size(),1);
    }

    @Test
    public void testSubscribersList(){
        initSubscribers();
        Response <Map<String, String>> response = userService.requestEmployeesStatus(0 ,"mia" ,userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),2);
    }

    @Test
    public void testManagersListNoAddedManagersToTheStore(){
       // Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getId(),"miaa" ,subscriber.getToken());
        Response <Map<String, List<String>>> response = userService.requestManagersPermissions(0 ,"mia" ,userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),0);
    }

    @Test
    public void testManagersList(){
        initManagers();
        Response <Map<String, List<String>>> response = userService.requestManagersPermissions(0 ,"mia" ,userService.getUserFacade().getUserRepository().getSubscriber("mia").getToken());
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
        Response<Map<String,String>> response = orderService.getOrderHistory(store.getData());
        Assert.assertFalse(response.isSuccess());
    }

}
