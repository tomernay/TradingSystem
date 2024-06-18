package UnitTests;
import Domain.Repo.*;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfoRequestsByOwner {
    UserRepository userRepository;
    StoreRepository storeRepository;
    OrderRepository orderRepository;
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    Store store;
    Subscriber subscriber;
    Response<String> res;


    //use mock to test the following functions: getOrdersHistory, requestEmployeesStatus,requestManagersPermissions, addStore
    //mock the following classes: OrderFacade, StoreFacade, UserFacade, OrderService, StoreService, UserService
    //test the functions abov in the repository classes
    //mock the parts that the function is dependant on them and check if the function works as expected

    public InfoRequestsByOwner() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService = serviceInitializer.getStoreService();
        userRepository = serviceInitializer.getUserService().getUserFacade().getUserRepository();
        storeRepository = serviceInitializer.getStoreService().getStoreFacade().getStoreRepository();
        orderRepository = serviceInitializer.getOrderService().getOrderFacade().getOrderRepository();
    }


    @Before
    public void init() {
        userRepository.register("mia","Password123!");
        userRepository.loginAsSubscriber("mia","Password123!");
        subscriber = userRepository.getUser("mia");
        res = storeService.addStore("newStore", "mia",userRepository.getUser("mia").getToken());
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
        userRepository.register("ziv","Password123!");
        userRepository.loginAsSubscriber("ziv","Password123!");
        serviceInitializer.getUserService().SendOwnerNominationRequest("0", "mia", "ziv", serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mia").getToken());
        serviceInitializer.getUserService().ownerNominationResponse("ziv",true, serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("ziv").getToken());
        //subscribe dor
        userRepository.register("dor","Password123!");
        userRepository.loginAsSubscriber("dor","Password123!");
        serviceInitializer.getUserService().SendOwnerNominationRequest("0", "mia", "dor", serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mia").getToken());
        serviceInitializer.getUserService().ownerNominationResponse("dor",true, serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("dor").getToken());
        //subscribe niv
        userRepository.register("niv","Password123!");
        userRepository.loginAsSubscriber("niv","Password123!");
    }

    public void initManagers(){
        //subscribe ziv
        userRepository.register("ziv","Password123!");
        userRepository.loginAsSubscriber("ziv","Password123!");
        //make ziv manager
        List<String> perms = new ArrayList<>();
        perms.add("MANAGE_PRODUCTS");

        //send nomination msg
        serviceInitializer.getUserService().SendManagerNominationRequest(res.getData(), "mia", "ziv", perms, serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mia").getToken());
        //accept the msg
        serviceInitializer.getUserService().managerNominationResponse("ziv",true, serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("ziv").getToken());

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
        Response<Map<String,String>> response = storeRepository.requestEmployeesStatus(res.getData());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),1);
    }

    @Test
    public void testRequestEmployeesStatus() {
        initSubscribers();
        Response<Map<String,String>> response = storeRepository.requestEmployeesStatus(res.getData());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),3);

    }

    @Test
    public void testRequestManagersPermissionsNoAddedManagers() {
        Response<Map<String, List<String>>> response = storeRepository.requestManagersPermissions(res.getData());
        Assert.assertTrue(response.isSuccess()); //the creator is also a manager
        Assert.assertEquals(response.getData().size(),0);
    }


    @Test
    public void testRequestManagersPermissions() {
        initManagers();
        Response<Map<String, List<String>>> response = storeRepository.requestManagersPermissions(res.getData());
        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(response.getData().size(),1);

    }


}



