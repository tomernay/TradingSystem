package UnitTests;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.*;

import java.util.List;

public class InventoryEditProductDetailsAsStoreManagerTest {
    private Subscriber subscriber, subscriber2;
    private ServiceInitializer serviceInitializer;
    private Store store;
    private Inventory inventory;
    private StoreService storeService;

    @BeforeEach
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        UserService userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();


        userService.register("itay", "ItayPass123!");
        userService.loginAsSubscriber("itay", "ItayPass123!");
        subscriber = userService.getUserFacade().getUserRepository().getSubscriber("itay");
        storeService.addStore("itayStore", "itay", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(0);
        inventory = store.getInventory();

        userService.register("mor","MorPass123!");
        userService.loginAsSubscriber("mor","MorPass123!");
        Response<Integer> res = userService.SendManagerNominationRequest(store.getId(), "itay", "mor", List.of("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES") ,subscriber.getToken());
        subscriber2 = serviceInitializer.getUserService().getUserFacade().getUserRepository().getSubscriber("mor");
        userService.managerNominationResponse(res.getData(), "mor",true, subscriber2.getToken());

        //if a product added without category, it will be added to General category
        Response<String> addProductResponse = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 20, "mor", subscriber2.getToken());
    }

    @Test
    public void editProductQuantity() {
        System.out.println("------------------editProductQuantity------------------------");
        storeService.setProductQuantity(1, 30, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals(30, inventory.productsList.get(1).getQuantity());
        storeService.setProductQuantity(1, -8, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals(30, inventory.productsList.get(1).getQuantity());
        storeService.setProductQuantity(2, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNull(inventory.productsList.get(2));
    }

    @Test
    public void editAddProductQuantity() {
        System.out.println("------------------editAddProductQuantity------------------------");
        storeService.addProductQuantity(1, -10, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals(10, inventory.productsList.get(1).getQuantity());
        storeService.addProductQuantity(1, -11, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals(10, inventory.productsList.get(1).getQuantity());
        storeService.addProductQuantity(3, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNull(inventory.productsList.get(3));
    }

    @Test
    public void editProductPrice() {
        System.out.println("------------------editProductPrice------------------------");
        storeService.setProductPrice(1, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals(20, inventory.productsList.get(1).getPrice());
        storeService.setProductPrice(1, -8, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals(20, inventory.productsList.get(1).getPrice());
        storeService.setProductPrice(2, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNull(inventory.productsList.get(2));

    }

    @Test
    public void editProductName() {
        System.out.println("------------------editProductName------------------------");
        storeService.setProductName(1, "NAME CHANGED", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals("NAME CHANGED", inventory.productsList.get(1).getName());
        storeService.setProductName(1, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals("NAME CHANGED", inventory.productsList.get(1).getName());
        storeService.setProductName(6, null, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNull(inventory.productsList.get(6));
    }

    @Test
    public void editProductDec() {
        System.out.println("------------------editProductDec------------------------");
        storeService.setProductDescription(1, "DESC CHANGED", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals("DESC CHANGED", inventory.productsList.get(1).getDescription());
        storeService.setProductDescription(6, "CHANGE MY DESC", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertNull(inventory.productsList.get(6));
        storeService.setProductDescription(1, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertEquals("DESC CHANGED", inventory.productsList.get(1).getDescription());
    }

    @Test
    public void editProductCategory() {
        System.out.println("------------------editProductCategory------------------------");
        Response<String> response = storeService.assignProductToCategory(1, "category1", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.assignProductToCategory(1, "category1", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.assignProductToCategory(0, "category1", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
        Response<String> response4 = storeService.assignProductToCategory(1, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response4.isSuccess());
    }

    @Test
    public void editAssignProductToCategory() {
        System.out.println("------------------editAssignProductToCategory------------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mor", subscriber2.getToken());
        storeService.assignProductToCategory(1, "category2", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(inventory.categories.get("category2").contains(inventory.productsList.get(1).getProductID()));
        }

    @Test
    public void editRemoveCategoryFromStore() {
        System.out.println("------------------editRemoveCategoryFromStore------------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mor", subscriber2.getToken());
        storeService.isCategoryExist(store.getId(), "General", "mor", subscriber2.getToken());
        Assertions.assertFalse(inventory.categories.containsKey("General"));
    }


}
