package AcceptanceTests;

import Service.ServiceInitializer;
import org.junit.BeforeClass;
import org.junit.Test;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;

public class StoreManagerPermissionsTests {
    static ServiceInitializer serviceInitializer;
    static UserService userService;
    static StoreService storeService;
    static Store store;
    static Subscriber subscriber;
    static Subscriber buyer;

    @BeforeClass
    public static void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService=serviceInitializer.getUserService();
        storeService=serviceInitializer.getStoreService();
        userService.register("yair","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getSubscriber("yair");

        userService.register("yair2","Password123!");
        buyer=userService.getUserFacade().getUserRepository().getSubscriber("yair2");
        storeService.addStore("yairStore","yair",subscriber.getToken());
        store=storeService.getStoreFacade().getStoreRepository().getActiveStore(0);

        //add manager

    }

//    @Test
//    public void executeAction(){
//        //add manager permission
//
//        //service.getUserService().addManagerPermissions("0",)
//
//        //execute action x
//
//        //assert action execute
//    }
//
//    public void failedActionExecution(){
//        //remove manager permission
//
//        //execute action x
//
//        //assert action not-execute
//    }



}
