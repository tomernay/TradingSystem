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
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class InventoryAddRemoveProductAsStoreOwnerTest {

    private Subscriber subscriber, subscriber2;
    private ServiceInitializer serviceInitializer;
    private Store store, store2;
    private StoreService storeService;


    @Before
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        UserService userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();


        userService.register("itay", "ItayPass123!");
        userService.loginAsSubscriber("itay", "ItayPass123!");
        subscriber = userService.getUserFacade().getUserRepository().getUser("itay");
        storeService.addStore("itayStore", "itay", subscriber.getToken());
        storeService.addStore("itayStore2", "itay", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
        store2 = storeService.getStoreFacade().getStoreRepository().getStore("1");


        userService.register("mor", "MorPass123!");
        userService.loginAsSubscriber("mor", "MorPass123!");
        userService.SendStoreOwnerNomination(store.getId(), "itay", "mor", subscriber.getToken());
        subscriber2 = serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mor");
        userService.ownerNominationResponse("mor", true, subscriber2.getToken());

    }

    @Test
    public void addProductTest(){
        System.out.println("---------------------------------------------------------------------------");
        //if a product added without category, it will be added to General category
        Response<String> response = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 10, "mor", subscriber2.getToken());
        Assert.assertEquals("product1", response.getData());
        Response<String> response1 = storeService.addProductToStore(store.getId(), "product2", "product2Dec", 20, 30, "mor", subscriber2.getToken());
        Assert.assertEquals("product2", response1.getData());
        Response<ArrayList<ProductDTO>> response2 = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        System.out.println(response2.getData());
    }

    @Test
    public void removeProductTest(){
        System.out.println("---------------------------------------------------------------------------");
        Response<String> response = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 10, "mor", subscriber2.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.addProductToStore(store.getId(), "product2", "product2Dec", 20.0, 15, "mor", subscriber2.getToken());
        Assert.assertTrue(response2.isSuccess());
        Response<String> response3 = storeService.addProductToStore(store.getId(), "product3", "product3Dec", 25.0, 60, "mor", subscriber2.getToken());
        Assert.assertTrue(response3.isSuccess());
        Response<String> response4 = storeService.addProductToStore(store2.getId(), "product1", "product1Dec", 20.0, 15, "mor", subscriber2.getToken());
        Assert.assertFalse(response4.isSuccess());
        Response<ArrayList<ProductDTO>> before = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        System.out.println("products in store :1 before removing");
        for(ProductDTO p : before.getData()){
            System.out.println(p);
        }
        Response<String> response5 = storeService.removeProductFromStore(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertTrue(response5.isSuccess());
        Response<String> response6 = storeService.removeProductFromStore(3, store.getId(), "mor", subscriber2.getToken());
        Assert.assertTrue(response6.isSuccess());
        Response<String> response7 = storeService.removeProductFromStore(1, store2.getId(), "mor", subscriber2.getToken());
        Assert.assertFalse(response7.isSuccess());
        Response<ArrayList<ProductDTO>> after = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        System.out.println("products in store :1 after removing");
        for(ProductDTO p : after.getData()){
            System.out.println(p);
        }
    }

}
