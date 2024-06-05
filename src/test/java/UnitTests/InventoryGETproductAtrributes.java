package UnitTests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryGETproductAtrributes {
    private static InventoryGETproductAtrributes instance;
    private Subscriber subscriber, subscriber2;
    private ServiceInitializer serviceInitializer;
    private Store store;
    private StoreService storeService;

    @BeforeClass
    public static void setUpClass() {
        instance = new InventoryGETproductAtrributes();
        instance.init();
    }

    @Before
    public void setUp() {
        // Ensuring all tests use the same initialized instance
        this.subscriber = instance.subscriber;
        this.subscriber2 = instance.subscriber2;
        this.serviceInitializer = instance.serviceInitializer;
        this.store = instance.store;
        this.storeService = instance.storeService;
    }

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
        userService.SendStoreManagerNomination(store.getId(), "itay", "mor", List.of("ADD_PRODUCT", "REMOVE_PRODUCT", "VIEW_PRODUCT"), subscriber.getToken());
        subscriber2 = serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mor");
        userService.managerNominationResponse("mor", true, subscriber2.getToken());

        storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 30, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product2", "product2Dec", 15, 20, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product3", "product3Dec", 20, 15, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product4", "product4Dec", 30, 10, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product5", "product5Dec", 40, 10,  new ArrayList<String>(Collections.singleton("Food")),"mor", subscriber2.getToken());

    }

    @Test
    public void getProductName() {
        Response<String> response1 = storeService.getProductName(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product1", response1.getData());
        Response<String> response2 = storeService.getProductName(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product4", response2.getData());
        Response<String> response3 = storeService.getProductName(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product5", response3.getData());
    }

    @Test
    public void getProductDescription() {
        Response<String> response1 = storeService.getProductDescription(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product1Dec", response1.getData());
        Response<String> response2 = storeService.getProductDescription(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product4Dec", response2.getData());
        Response<String> response3 = storeService.getProductDescription(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product5Dec", response3.getData());
    }

    @Test
    public void getProductPrice() {
        Response<String> response1 = storeService.getProductPrice(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("10.0", response1.getData());
        Response<String> response2 = storeService.getProductPrice(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("30.0", response2.getData());
        Response<String> response3 = storeService.getProductPrice(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("40.0", response3.getData());
    }

    @Test
    public void getProductQuantity() {
        Response<String> response1 = storeService.getProductQuantity(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("30", response1.getData());
        Response<String> response2 = storeService.getProductQuantity(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("10", response2.getData());
        Response<String> response3 = storeService.getProductQuantity(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("10", response3.getData());
    }

    @Test
    public void retrieveProductCategories() {
        Response<String> response1 = storeService.retrieveProductCategories(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[\"General\"]", response1.getData());
        Response<String> response2 = storeService.retrieveProductCategories(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[\"General\"]", response1.getData());
        Response<String> response3 = storeService.retrieveProductCategories(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[\"Food\"]", response3.getData());
    }


}
