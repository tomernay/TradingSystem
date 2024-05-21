package src.Tests;

import src.main.java.Domain.Store.Inventory.Inventory;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import org.junit.Before;
import src.main.java.Service.StoreService;
import src.main.java.Service.UserService;

public class InventoryTests {
    Subscriber subscriber;
    Inventory inventory;
    UserService userService;

    @Before
    public void init(){
        StoreService storeService = new StoreService();
        userService.register("mor","shuker");
        storeService.addStore("morStore","mor",subscriber.getToken());
        storeService.getStore("morStore");


    }





}


