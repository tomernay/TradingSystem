package AcceptanceTests;

import Service.ServiceInitializer;
import Service.StoreService;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

public class StoreRole {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber subscriber, subscriber2, subscriber3;
    Store store;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService=serviceInitializer.getStoreService();
        userService=serviceInitializer.getUserService();

        userService.register("yair12312","Password123!");
        userService.loginAsSubscriber("yair12312","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getUser("yair12312");

        storeService.addStore("yairStore","yair12312",subscriber.getToken());
        store=storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

    @Test
    public void selfNominationStoreOwnerTest(){
        Response<String> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber.getUsername(), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void selfNominationStoreManagerTest(){
        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void selfAddPermissionTest(){
        Response<String> response = storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), subscriber.getUsername(), "ADD_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void selfRemovePermissionTest(){
        Response<String> response = storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), subscriber.getUsername(), "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeStoreOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.ownerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());
        Assert.assertTrue(response1.isSuccess());
    }

    @Test
    public void makeStoreManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());
        Assert.assertTrue(response1.isSuccess());
    }

    @Test
    public void makeStoreOwnerDeclineTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.ownerNominationResponse(response.getData(), subscriber2.getUsername(), false, subscriber2.getToken());
        Assert.assertTrue(response1.isSuccess());
    }

    @Test
    public void makeStoreManagerDeclineTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), false, subscriber2.getToken());
        Assert.assertTrue(response1.isSuccess());
    }

    @Test
    public void notOwnerMakeStoreOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.register("niv1212","Password123!");
        userService.loginAsSubscriber("niv1212","Password123!");
        subscriber3=userService.getUserFacade().getUserRepository().getUser("niv1212");

        Response<String> response = userService.SendOwnerNominationRequest(store.getId(), subscriber3.getUsername(), subscriber2.getUsername(), subscriber3.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void notOwnerMakeStoreManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.register("niv1212","Password123!");
        userService.loginAsSubscriber("niv1212","Password123!");
        subscriber3=userService.getUserFacade().getUserRepository().getUser("niv1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber3.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber3.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeExistingStoreOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        userService.ownerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());
        Response<String> response2 = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void makeExistingStoreManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());
        Response<String> response2 = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void addPermissionSuccessTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());
        Response<String> response2 = storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), "VIEW_PRODUCTS", subscriber.getToken());
        Assert.assertTrue(response2.isSuccess());
    }

    @Test
    public void removePermissionSuccessTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());
        Response<String> response2 = storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), "MANAGE_PRODUCTS", subscriber.getToken());
        Assert.assertTrue(response2.isSuccess());
    }

    @Test
    public void addPermissionNotOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.register("niv1212","Password123!");
        userService.loginAsSubscriber("niv1212","Password123!");
        subscriber3=userService.getUserFacade().getUserRepository().getUser("niv1212");

        Response<String> response = storeService.addManagerPermissions(store.getId(), subscriber3.getUsername(), subscriber2.getUsername(), "MANAGE_PRODUCTS", subscriber3.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.register("niv1212","Password123!");
        userService.loginAsSubscriber("niv1212","Password123!");
        subscriber3=userService.getUserFacade().getUserRepository().getUser("niv1212");

        Response<String> response = storeService.removeManagerPermissions(store.getId(), subscriber3.getUsername(), subscriber2.getUsername(), "MANAGE_PRODUCTS", subscriber3.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionNotManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = storeService.addManagerPermissions(store.getId(), subscriber2.getUsername(), subscriber.getUsername(), "MANAGE_PRODUCTS", subscriber2.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNotManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = storeService.removeManagerPermissions(store.getId(), subscriber2.getUsername(), subscriber.getUsername(), "MANAGE_PRODUCTS", subscriber2.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeStoreOwnerNonExistentUserTest(){
        Response<String> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), "nonExistentUser", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void makeStoreManagerNonExistentUserTest(){
        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), "nonExistentUser", Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addPermissionNonExistentUserTest(){
        Response<String> response = storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), "nonExistentUser", "MANAGE_PRODUCTS", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void removePermissionNonExistentUserTest(){
        Response<String> response = storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), "nonExistentUser", "permission", subscriber.getToken());
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void addNonExistentPermissionTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber.getToken());
        Response<String> response2 = storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), "NON_EXISTENT_PERMISSION", subscriber.getToken());
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void removeNonExistentPermissionTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES"), subscriber.getToken());
        userService.managerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber.getToken());
        Response<String> response2 = storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), "NON_EXISTENT_PERMISSION", subscriber.getToken());
        Assert.assertFalse(response2.isSuccess());
    }

    @Test
    public void waiveOwnerRoleTest() {
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        Response<String> response = userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response1 = userService.ownerNominationResponse(response.getData(), subscriber2.getUsername(), true, subscriber2.getToken());
        Assert.assertTrue(response1.isSuccess());

        // New owner waives their role
        Response<String> waiveResponse = userService.waiveOwnership(store.getId(), subscriber2.getUsername(), subscriber2.getToken());
        Assert.assertTrue(waiveResponse.isSuccess());
    }


}