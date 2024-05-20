package src.Tests;

import org.junit.BeforeClass;
import org.junit.Test;
import src.main.java.Domain.Store.Store;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import src.main.java.Service.Service;

public class StoreManagerPermissionsTests {
    static Service service;
    static Store store;
    static Subscriber subscriber;
    static Subscriber buyer;

    @BeforeClass
    public static void init(){
        service = new Service();
        service.getUserService().register("yair","by");
        subscriber=service.getUserService().getUser("yair");

        service.getUserService().register("yair2","by2");
        buyer=service.getUserService().getUser("yair2");
        service.getStoreService().addStore("yairStore","yair",subscriber.getToken());
        store=service.getStoreService().getStore("0");

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
