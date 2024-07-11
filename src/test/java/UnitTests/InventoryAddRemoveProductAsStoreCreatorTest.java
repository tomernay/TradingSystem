package UnitTests;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class InventoryAddRemoveProductAsStoreCreatorTest {
    ServiceInitializer serviceInitializer;
    Subscriber subscriber, subscriber2;
    Store store, store2;
    Inventory inventory, inventory2;
    UserService userService;
    StoreService storeService;

    @Before
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();

        userService.register("mor", "pass1Pas@");
        userService.loginAsSubscriber("mor", "pass1Pas@");
        subscriber = userService.getUserFacade().getUserRepository().getSubscriber("mor");
        subscriber2 = userService.getUserFacade().getUserRepository().getSubscriber("mor");

        storeService.addStore("morStore", "mor", subscriber.getToken());
        storeService.addStore("morStore2", "mor", subscriber2.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(0);
        store2 = storeService.getStoreFacade().getStoreRepository().getActiveStore(1);
        inventory = store.getInventory();
        inventory2 = store2.getInventory();
    }

    @Test
    public void addProductTest() {
        System.out.println("-----------------------addProductTest--------------------------");
        storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 10, "mor", subscriber.getToken());
        Assertions.assertTrue(inventory.productsList.containsKey(1));
        Assertions.assertEquals("product1", inventory.productsList.get(1).getName());
        Assertions.assertEquals("product1Dec", inventory.productsList.get(1).getDescription());
        Assertions.assertEquals(10, inventory.productsList.get(1).getPrice());
        Assertions.assertEquals(10, inventory.productsList.get(1).getQuantity());
        Assertions.assertTrue(inventory.categories.get("General").contains(inventory.productsList.get(1).getProductID()), "General");

        storeService.addProductToStore(store.getId(), "product2", "product2Dec", 20, 30, "mor", subscriber.getToken());
        Assertions.assertTrue(inventory.productsList.containsKey(2));
        Assertions.assertEquals("product2", inventory.productsList.get(2).getName());
        Assertions.assertEquals("product2Dec", inventory.productsList.get(2).getDescription());
        Assertions.assertEquals(20, inventory.productsList.get(2).getPrice());
        Assertions.assertEquals(30, inventory.productsList.get(2).getQuantity());
        Assertions.assertTrue(inventory.categories.get("General").contains(inventory.productsList.get(2).getProductID()), "General");
    }

    @Test
    public void removeProductTest() {
        System.out.println("-----------------------removeProductTest--------------------------");
        storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10.0, 10, "mor", subscriber.getToken());
        storeService.addProductToStore(store.getId(), "product2", "product2Dec", 20.0, 15, "mor", subscriber.getToken());
        storeService.addProductToStore(store.getId(), "product3", "product3Dec", 25.0, 60, "mor", subscriber.getToken());
        storeService.addProductToStore(store2.getId(), "product1", "product1Dec", 20.0, 15, "mor", subscriber2.getToken());
        Response<ArrayList<ProductDTO>> before = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber.getToken());
        System.out.println("products in store :1 before removing");
        for (ProductDTO p : before.getData()) {
            System.out.println(p);
        }
        storeService.removeProductFromStore(1, store.getId(), "mor", subscriber.getToken());
        Assertions.assertFalse(inventory.productsList.containsKey(1));
        storeService.removeProductFromStore(3, store.getId(), "mor", subscriber.getToken());
        Assertions.assertFalse(inventory.productsList.containsKey(3));

        Assertions.assertTrue(inventory.productsList.containsKey(2));
        Assertions.assertTrue(inventory2.productsList.containsKey(1));

        Response<ArrayList<ProductDTO>> after = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber.getToken());
        System.out.println("products in store :1 after removing");
        for (ProductDTO p : after.getData()) {
            System.out.println(p);
        }
    }
}
