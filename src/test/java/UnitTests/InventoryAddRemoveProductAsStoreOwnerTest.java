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
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class InventoryAddRemoveProductAsStoreOwnerTest {

    private Subscriber subscriber, subscriber2;
    private ServiceInitializer serviceInitializer;
    private Store store;
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
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");

        userService.register("mor", "MorPass123!");
        userService.loginAsSubscriber("mor", "MorPass123!");
        userService.SendStoreOwnerNomination(store.getId(), "itay", "mor", subscriber.getToken());
        subscriber2 = serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mor");
        userService.ownerNominationResponse("mor", true, subscriber2.getToken());

    }

    @Test
    public void addProductTest(){
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
        Response<String> response = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 10, "mor", subscriber2.getToken());
        Assert.assertTrue(response.isSuccess());
    }

}
