package UnitTests;

import Domain.Store.Inventory.Inventory;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.*;

public class InventoryEditProductDetailsAsStoreCreatorTest {

    private Subscriber subscriber;
    private Store store;
    Inventory inventory;
    private StoreService storeService;

    @BeforeEach
    public void init() {
        ServiceInitializer.reset();
        ServiceInitializer serviceInitializer = ServiceInitializer.getInstance();
        UserService userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();

        userService.register("mormor", "pass1Pas@");
        userService.loginAsSubscriber("mormor", "pass1Pas@");
        subscriber = userService.getUserFacade().getUserRepository().getSubscriber("mormor");

        storeService.addStore("morStore", "mormor", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getActiveStore(0);
        inventory = store.getInventory();
        //if a product added without category, it will be added to General category
        Response<String> addProductResponse = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 20, "mormor", subscriber.getToken());
    }

    @Test
    public void editProductQuantity() {
        System.out.println("----------------------editProductQuantity----------------------");
        storeService.setProductQuantity(1, 30, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals(30, inventory.productsList.get(1).getQuantity());
        storeService.setProductQuantity(1, -8, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals(30, inventory.productsList.get(1).getQuantity());
        storeService.setProductQuantity(2, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNull(inventory.productsList.get(2));
    }

    @Test
    public void editAddProductQuantity() {
        System.out.println("----------------------editAddProductQuantity----------------------");
        storeService.addProductQuantity(1, -10, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals(10, inventory.productsList.get(1).getQuantity());
        storeService.addProductQuantity(1, -11, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals(10, inventory.productsList.get(1).getQuantity());
        storeService.addProductQuantity(2, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNull(inventory.productsList.get(2));
    }

    @Test
    public void editProductPrice() {
        System.out.println("----------------------editProductPrice----------------------");
        storeService.setProductPrice(1, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals(20, inventory.productsList.get(1).getPrice());
        storeService.setProductPrice(1, -8, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals(20, inventory.productsList.get(1).getPrice());
        storeService.setProductPrice(2, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNull(inventory.productsList.get(2));
    }

    @Test
    public void editProductName() {
        System.out.println("----------------------editProductName----------------------");
        storeService.setProductName(1, "NAME CHANGED", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals("NAME CHANGED", inventory.productsList.get(1).getName());
        storeService.setProductName(1, "", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNotEquals("", inventory.productsList.get(1).getName());
        storeService.setProductName(2, null, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNull(inventory.productsList.get(2));
    }

    @Test
    public void editProductDec() {
        System.out.println("----------------------editProductDec----------------------");
        storeService.setProductDescription(1, "DESC CHANGED", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertEquals("DESC CHANGED", inventory.productsList.get(1).getDescription());
        storeService.setProductDescription(2, "CHANGE MY DESC", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNull(inventory.productsList.get(2));
        storeService.setProductDescription(1, "", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNotEquals("", inventory.productsList.get(1).getDescription());
    }

    @Test
    public void editProductCategory() {
        System.out.println("----------------------editProductCategory----------------------");
        storeService.assignProductToCategory(1, "category1", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(inventory.categories.get("category1").contains(inventory.productsList.get(1).getProductID()));
        storeService.assignProductToCategory(1, "category1", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(inventory.categories.get("category1").contains(inventory.productsList.get(1).getProductID()));
        storeService.assignProductToCategory(0, "category1", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertNull(inventory.productsList.get(0));
        storeService.assignProductToCategory(1, "", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(inventory.categories.get("General").contains(inventory.productsList.get(1).getProductID()));
    }

    @Test
    public void editAssignProductToCategory() {
        System.out.println("----------------------editAssignProductToCategory----------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mormor", subscriber.getToken());
        storeService.assignProductToCategory(1, "category2", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(inventory.categories.get("category2").contains(inventory.productsList.get(1).getProductID()));
    }

    @Test
    public void editRemoveCategoryFromStore() {
        System.out.println("----------------------editRemoveCategoryFromStore----------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mormor", subscriber.getToken());
        Assertions.assertFalse(inventory.categories.containsKey("General"));
    }
}
