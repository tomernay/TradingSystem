package UnitTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;


public class InventoryAddRemoveProductAsStoreCreatorTest {
    ServiceInitializer serviceInitializer;
    Subscriber subscriber, manager;
    Store store;
    UserService userService;
    StoreService storeService;
    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();

        userService.register("mormor","pass1Pas@");
        userService.loginAsSubscriber("mormor","pass1Pas@");
        subscriber = userService.getUserFacade().getUserRepository().getUser("mormor");

        storeService.addStore("morStore","mormor",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
        //  storeService.addManagerPermissions(store.getId(), subscriber.getUsername(), subscriber.getUsername(), "ADD_PRODUCT", subscriber.getToken());
    }

    @Test
    public void addProductTest(){
        Response<String> response = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 10, "mormor", subscriber.getToken());
        Assert.assertEquals("product1", response.getData());
        Response<String> response1 = storeService.addProductToStore(store.getId(), "product2", "product2Dec", 20, 30, "mormor", subscriber.getToken());
        Assert.assertEquals("product2", response1.getData());
        Response<ArrayList<ProductDTO>> response2 = storeService.getAllProductsFromStore(store.getId(), "mormor", subscriber.getToken());
        System.out.println(response2.getData());
    }

    @Test
    public void removeProductTest(){
        Response<String> response = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 10, "mormor", subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
    }






}
