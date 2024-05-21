package AcceptanceTests;

import Domain.Store.Inventory.Inventory;
import Domain.Users.Subscriber.Subscriber;
import org.junit.Before;
import Service.StoreService;
import Service.UserService;

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


