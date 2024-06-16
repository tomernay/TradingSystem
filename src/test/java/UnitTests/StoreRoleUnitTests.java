package UnitTests;

import Domain.Store.Store;
import Domain.Users.StateOfSubscriber.StoreManager;
import Domain.Users.StateOfSubscriber.StoreOwner;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StoreRoleUnitTests {
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
        userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber.getUsername(), subscriber.getToken());
        List<Message> messages = new ArrayList<>(subscriber.getMessages());
        for (Message message : messages) {
            Assert.assertFalse(message instanceof nominateOwnerMessage && Objects.equals(((nominateOwnerMessage) message).getStoreID(), store.getId()) && ((nominateOwnerMessage) message).getNominatorUsername().equals(subscriber.getUsername()));
        }
    }

    @Test
    public void selfNominationStoreManagerTest(){
        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        List<Message> messages = new ArrayList<>(subscriber.getMessages());
        for (Message message : messages) {
            Assert.assertFalse(message instanceof nominateManagerMessage && Objects.equals(((nominateManagerMessage) message).getStoreID(), store.getId()) && ((nominateManagerMessage) message).getNominatorUsername().equals(subscriber.getUsername()));
        }
    }

    @Test
    public void selfAddPermissionTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());

        storeService.addManagerPermissions(store.getId(), subscriber2.getUsername(), subscriber2.getUsername(), "ADD_PRODUCT", subscriber2.getToken());
        Assert.assertFalse(store.getManagerPermissions(subscriber2.getUsername()).contains("ADD_PRODUCT"));
    }

    @Test
    public void selfRemovePermissionTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());

        storeService.removeManagerPermissions(store.getId(), subscriber2.getUsername(), subscriber2.getUsername(), "REMOVE_PRODUCT", subscriber2.getToken());
        Assert.assertTrue(store.getManagerPermissions(subscriber2.getUsername()).contains("REMOVE_PRODUCT"));
    }

    @Test
    public void makeStoreOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        userService.ownerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());
        Assert.assertTrue(storeService.isStoreOwner(store.getId(), subscriber2.getUsername()));
    }

    @Test
    public void makeStoreManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());
        Assert.assertTrue(storeService.isStoreManager(store.getId(), subscriber2.getUsername()));
    }

    @Test
    public void makeStoreOwnerDeclineTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        userService.ownerNominationResponse(subscriber2.getUsername(), false, subscriber2.getToken());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), subscriber2.getUsername()));
    }

    @Test
    public void makeStoreManagerDeclineTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(subscriber2.getUsername(), false, subscriber2.getToken());
        Assert.assertFalse(storeService.isStoreManager(store.getId(), subscriber2.getUsername()));
    }

    @Test
    public void notOwnerMakeStoreOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.register("niv1212","Password123!");
        userService.loginAsSubscriber("niv1212","Password123!");
        subscriber3=userService.getUserFacade().getUserRepository().getUser("niv1212");

        userService.SendOwnerNominationRequest(store.getId(), subscriber3.getUsername(), subscriber2.getUsername(), subscriber3.getToken());
        Assert.assertFalse(subscriber2.getMessages().stream().anyMatch(message -> message instanceof nominateOwnerMessage && ((nominateOwnerMessage) message).getStoreID() == store.getId() && ((nominateOwnerMessage) message).getNominatorUsername().equals(subscriber3.getUsername())));
    }

    @Test
    public void notOwnerMakeStoreManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.register("niv1212","Password123!");
        userService.loginAsSubscriber("niv1212","Password123!");
        subscriber3=userService.getUserFacade().getUserRepository().getUser("niv1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber3.getUsername(), subscriber2.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber3.getToken());
        Assert.assertFalse(subscriber2.getMessages().stream().anyMatch(message -> message instanceof nominateManagerMessage && ((nominateManagerMessage) message).getStoreID() == store.getId() && ((nominateManagerMessage) message).getNominatorUsername().equals(subscriber3.getUsername())));
    }

    @Test
    public void makeExistingStoreOwnerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        userService.ownerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());
        userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        Assert.assertFalse(subscriber2.getMessages().stream().anyMatch(message -> message instanceof nominateOwnerMessage && ((nominateOwnerMessage) message).getStoreID() == store.getId() && ((nominateOwnerMessage) message).getNominatorUsername().equals(subscriber.getUsername())));
    }

    @Test
    public void makeExistingStoreManagerTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());
        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertFalse(subscriber2.getMessages().stream().anyMatch(message -> message instanceof nominateManagerMessage && ((nominateManagerMessage) message).getStoreID() == store.getId() && ((nominateManagerMessage) message).getNominatorUsername().equals(subscriber.getUsername())));
    }

    @Test
    public void addPermissionSuccessTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());
        storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), "ADD_PRODUCT", subscriber.getToken());
        Assert.assertTrue(store.getManagerPermissions(subscriber2.getUsername()).contains("ADD_PRODUCT"));
    }

    @Test
    public void removePermissionSuccessTest(){
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        userService.managerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());
        storeService.removeManagerPermissions(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), "REMOVE_PRODUCT", subscriber.getToken());
        Assert.assertFalse(store.getManagerPermissions(subscriber2.getUsername()).contains("REMOVE_PRODUCT"));
    }

    @Test
    public void makeStoreOwnerNonExistentUserTest(){
        userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), "nonExistentUser", subscriber.getToken());
        Assert.assertFalse(store.getSubscribers().containsKey("nonExistentUser") && store.getSubscribers().get("nonExistentUser") instanceof StoreOwner);
    }

    @Test
    public void makeStoreManagerNonExistentUserTest(){
        userService.SendManagerNominationRequest(store.getId(), subscriber.getUsername(), "nonExistentUser", Arrays.asList("ADD_PRODUCT", "REMOVE_PRODUCT", "EDIT_PRODUCT", "ADD_DISCOUNT", "REMOVE_DISCOUNT"), subscriber.getToken());
        Assert.assertFalse(store.getSubscribers().containsKey("nonExistentUser") && store.getSubscribers().get("nonExistentUser") instanceof StoreManager);
    }

    @Test
    public void waiveOwnerRoleTest() {
        userService.register("tomer1212","Password123!");
        userService.loginAsSubscriber("tomer1212","Password123!");
        subscriber2=userService.getUserFacade().getUserRepository().getUser("tomer1212");

        userService.SendOwnerNominationRequest(store.getId(), subscriber.getUsername(), subscriber2.getUsername(), subscriber.getToken());
        userService.ownerNominationResponse(subscriber2.getUsername(), true, subscriber2.getToken());
        Assert.assertTrue(storeService.isStoreOwner(store.getId(), subscriber2.getUsername()));

        // New owner waives their role
        userService.waiveOwnership(store.getId(), subscriber2.getUsername(), subscriber2.getToken());
        Assert.assertFalse(storeService.isStoreOwner(store.getId(), subscriber2.getUsername()));
    }


}
