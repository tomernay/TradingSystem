package AcceptanceTests;

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

import java.util.List;
import java.util.Map;

public class InfoRequestsByOwner {
    ServiceInitializer serviceInitializer;
    Subscriber subscriber;
    Store store;
    StoreService storeService;
    UserService userService;

    @Before
    public void init(){
        serviceInitializer = new ServiceInitializer();
        storeService = serviceInitializer.getStoreService();
        userService = serviceInitializer.getUserService();
        userService.register("mia","Password123!");
        subscriber = userService.getUserFacade().getUserRepository().getUser("mia");
        Response<String> response = storeService.addStore("ziv", "mia", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore(response.getData());
    }

    @Test
    public void testSubscribersListNoSubscribersToTheStore(){
        Response <Map<String, String>> response = userService.requestEmployeesStatus(store.getId(),"mia" ,subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void testManagersListNoManagersToTheStore(){
        Response <Map<String, List<String>>> response = userService.requestManagersPermissions(store.getId(),"mia" ,subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }


    public static void main(String[] args) {
        InfoRequestsByOwner ownerInfoRequests = new InfoRequestsByOwner();
        ownerInfoRequests.init();
        ownerInfoRequests.testSubscribersListNoSubscribersToTheStore();
        ownerInfoRequests.testManagersListNoManagersToTheStore();
    }

}
