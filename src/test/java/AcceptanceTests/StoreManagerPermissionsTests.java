package AcceptanceTests;

import org.junit.BeforeClass;
import org.junit.Test;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;

public class StoreManagerPermissionsTests {
    static UserService userService;
    static StoreService storeService;
    static Store store;
    static Subscriber subscriber;
    static Subscriber buyer;

    @BeforeClass
    public static void init(){
        userService=new UserService();
        storeService=new StoreService();
        userService.register("yair","by");
        subscriber=userService.getUser("yair");

        userService.register("yair2","by2");
        buyer=userService.getUser("yair2");
        storeService.addStore("yairStore","yair",subscriber.getToken());
        store=storeService.getStore("0");

        //add manager

    }

    @Test
    public void executeAction(){
        //add manager permission

        //service.getUserService().addManagerPermissions("0",)

        //execute action x

        //assert action execute
    }

    public void failedActionExecution(){
        //remove manager permission

        //execute action x

        //assert action not-execute
    }



}
