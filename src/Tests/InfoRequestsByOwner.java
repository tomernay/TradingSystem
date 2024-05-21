package src.Tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import src.main.java.Domain.Store.Store;
import src.main.java.Domain.Store.StoreData.Permissions;
import src.main.java.Domain.Users.StateOfSubscriber.SubscriberState;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import src.main.java.Service.StoreService;
import src.main.java.Service.UserService;
import src.main.java.Utilities.Response;

import java.util.List;
import java.util.Map;

public class InfoRequestsByOwner {
    StoreService storeService;
    Subscriber subscriber;
    Store store;
    UserService userService;

    @Before
    public void init(){
        storeService = new StoreService();
        userService = new UserService();
        userService.register("mia","22");
        subscriber = userService.getUser("mia");
        Response<String> response = storeService.addStore("ziv", "mia", subscriber.getToken());
        store = storeService.getStore(response.getData());
    }

    @Test
    public void testSubscribersListNoSubscribersToTheStore(){
        Response <Map<String, SubscriberState>> response = userService.requestEmployeesStatus(store.getId(),"mia" ,subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void testManagersListNoManagersToTheStore(){
        Response <Map<String, List<Permissions>>> response = userService.requestManagersPermissions(store.getId(),"mia" ,subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }


    public static void main(String[] args) {
        InfoRequestsByOwner ownerInfoRequests = new InfoRequestsByOwner();
        ownerInfoRequests.init();
        ownerInfoRequests.testSubscribersListNoSubscribersToTheStore();
        ownerInfoRequests.testManagersListNoManagersToTheStore();
    }

}
