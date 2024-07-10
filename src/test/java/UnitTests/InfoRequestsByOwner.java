package UnitTests;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Facades.OrderFacade;
import Facades.StoreFacade;
import Facades.UserFacade;
import Service.StoreService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoRequestsByOwner {
    UserFacade userFacade;
    StoreFacade storeFacade;
    OrderFacade orderFacade;
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    Store store;
    Subscriber subscriber;
    Response<Integer> res;


    //use mock to test the following functions: getOrdersHistory, requestEmployeesStatus,requestManagersPermissions, addStore
    //mock the following classes: OrderFacade, StoreFacade, UserFacade, OrderService, StoreService, UserService
    //test the functions abov in the repository classes
    //mock the parts that the function is dependant on them and check if the function works as expected

    public InfoRequestsByOwner() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userFacade = serviceInitializer.getUserService().getUserFacade();
        storeFacade = serviceInitializer.getStoreService().getStoreFacade();
        orderFacade = serviceInitializer.getOrderService().getOrderFacade();
    }


    @Before
    public void init() {
        userFacade.register("mia","Password123!");
        userFacade.loginAsSubscriber("mia","Password123!");
        subscriber = serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("mia");
        res = storeService.addStore("newStore", "mia",serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        //make mia the store owner
        //create a mock store using mockito

//        storeRepository.addStore("newStore", "mia",userRepository.getUser("mia").getToken());

//        Store mockStore = Mockito.mock(Store.class);
        // Inside your test class
//        Store mockStore = Mockito.mock(Store.class);

        // Define behavior
//        Mockito.when(mockStore.getData()).thenReturn("mock data");
    }

    public void initSubscribers(){
        //subscribe ziv
        userFacade.register("ziv","Password123!");
        userFacade.loginAsSubscriber("ziv","Password123!");
        Response<Integer> response = serviceInitializer.getUserService().SendOwnerNominationRequest(0, "mia", "ziv", serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        serviceInitializer.getUserService().ownerNominationResponse(response.getData(),"ziv",true, serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("ziv").getToken());
        //subscribe dor
        userFacade.register("dor","Password123!");
        userFacade.loginAsSubscriber("dor","Password123!");
        Response<Integer> response2 = serviceInitializer.getUserService().SendOwnerNominationRequest(0, "mia", "dor", serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        serviceInitializer.getUserService().ownerNominationResponse(response2.getData(),"dor",true, serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("dor").getToken());
        //subscribe niv
        userFacade.register("niv","Password123!");
        userFacade.loginAsSubscriber("niv","Password123!");
    }

    public void initManagers(){
        //subscribe ziv
        userFacade.register("ziv","Password123!");
        userFacade.loginAsSubscriber("ziv","Password123!");
        //make ziv manager
        List<String> perms = new ArrayList<>();
        perms.add("MANAGE_PRODUCTS");

        //send nomination msg
        Response<Integer> response = serviceInitializer.getUserService().SendManagerNominationRequest(res.getData(), "mia", "ziv", perms, serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("mia").getToken());
        //accept the msg
        serviceInitializer.getUserService().managerNominationResponse(response.getData(), "ziv",true, serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("ziv").getToken());

    }



//    @Test
//    public void testGetOrdersHistory() {
//        //add orders
//        //use mock
//        Response<Map<String,String>> response = orderRepository.getOrdersHistory(res.getData());
//        Assert.assertTrue(response.isSuccess());
//    }

    @Test
    public void testRequestEmployeesStatusNoSubscribers() {
        //use mock
        Response<Map<String,String>> response = storeFacade.requestEmployeesStatus(res.getData());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),1);
    }

    @Test
    public void testRequestEmployeesStatus() {
        initSubscribers();
        Response<Map<String,String>> response = storeFacade.requestEmployeesStatus(res.getData());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),3);

    }

    @Test
    public void testRequestManagersPermissionsNoAddedManagers() {
        Response<Map<String, List<String>>> response = storeFacade.requestManagersPermissions(res.getData());
        Assert.assertTrue(response.isSuccess()); //the creator is also a manager
        Assert.assertEquals(response.getData().size(),0);
    }


    @Test
    public void testRequestManagersPermissions() {
        initManagers();
        Response<Map<String, List<String>>> response = storeFacade.requestManagersPermissions(res.getData());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),1);

    }


}



