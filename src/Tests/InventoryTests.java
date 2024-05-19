package Tests;

import Domain.Store.Inventory.Inventory;
import Domain.Users.Subscriber.Subscriber;
import Service.Service;
import Service.StoreService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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


