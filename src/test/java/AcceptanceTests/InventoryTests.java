package AcceptanceTests;

import Domain.Store.Inventory.Inventory;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import org.junit.Before;
import Service.StoreService;
import Service.UserService;

public class InventoryTests {
    ServiceInitializer serviceInitializer;
    Subscriber subscriber;
    Inventory inventory;
    UserService userService;

    @Before
    public void init(){
        serviceInitializer = new ServiceInitializer();
        StoreService storeService = serviceInitializer.getStoreService();
        userService.register("mor","shuker");
        storeService.addStore("morStore","mor",subscriber.getToken());
        storeService.getStoreFacade().getStoreRepository().getStore("morStore");


    }





}


