package AcceptanceTests;

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
    Subscriber subscriber, subscriber2;
    Store store, store2;
    UserService userService;
    StoreService storeService;
    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();

        userService.register("mor","pass1Pas@");
        userService.loginAsSubscriber("mor","pass1Pas@");
        subscriber = userService.getUserFacade().getUserRepository().getUser("mor");
        subscriber2 = userService.getUserFacade().getUserRepository().getUser("mor");

        storeService.addStore("morStore","mor",subscriber.getToken());
        storeService.addStore("morStore2","mor",subscriber2.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
        store2 = storeService.getStoreFacade().getStoreRepository().getStore("1");
    }

    @Test
    public void addProductTest(){
        Response<String> response = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 10, "mor", subscriber.getToken());
        Assert.assertEquals("product1", response.getData());
        Response<String> response1 = storeService.addProductToStore(store.getId(), "product2", "product2Dec", 20, 30, "mor", subscriber.getToken());
        Assert.assertEquals("product2", response1.getData());
        Response<ArrayList<ProductDTO>> response2 = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber.getToken());
        System.out.println(response2.getData());
    }

    @Test
    public void removeProductTest(){
        System.out.println("---------------------------------------------------------------------------");
        Response<String> response = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10.0, 10, "mor", subscriber.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.addProductToStore(store.getId(), "product2", "product2Dec", 20.0, 15, "mor", subscriber.getToken());
        Assert.assertTrue(response2.isSuccess());
        Response<String> response3 = storeService.addProductToStore(store.getId(), "product3", "product3Dec", 25.0, 60, "mor", subscriber.getToken());
        Assert.assertTrue(response3.isSuccess());
        Response<String> response4 = storeService.addProductToStore(store2.getId(), "product1", "product1Dec", 20.0, 15, "mor", subscriber2.getToken());
        Assert.assertTrue(response4.isSuccess());
        Response<ArrayList<ProductDTO>> before = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber.getToken());
        System.out.println("products in store :1 before removing");
        for(ProductDTO p : before.getData()){
            System.out.println(p);
        }
        Response<String> response5 = storeService.removeProductFromStore(1, store.getId(), "mor", subscriber.getToken());
        Assert.assertTrue(response5.isSuccess());
        Response<String> response6 = storeService.removeProductFromStore(3, store.getId(), "mor", subscriber.getToken());
        Assert.assertTrue(response6.isSuccess());
        Response<ArrayList<ProductDTO>> after = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber.getToken());
        System.out.println("products in store :1 after removing");
        for(ProductDTO p : after.getData()){
            System.out.println(p);
        }
    }






}
