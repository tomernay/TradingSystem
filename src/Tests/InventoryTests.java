package src.Tests;

import src.main.java.Domain.Store.Inventory.Inventory;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import org.junit.Before;
import src.main.java.Service.Service;
import src.main.java.Service.StoreService;

public class InventoryTests {
    Service service;
    Subscriber subscriber;
    Inventory inventory;

    @Before
    public void init(){
        service = new Service();
        StoreService storeService = service.getStoreService();
        service.getUserService().register("mor","shuker");
        service.getStoreService().addStore("morStore","mor",subscriber.getToken());
        service.getStoreService().getStore("morStore");


    }





}


