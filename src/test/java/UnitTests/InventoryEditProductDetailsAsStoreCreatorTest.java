package UnitTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

// To whom it may concern:
// I set the order of the execution of the tests because the "addProductToStore" method generates a productID automatically,
// and I need to know the productID in order to test it.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryEditProductDetailsAsStoreCreatorTest {

    private Subscriber subscriber;
    private Store store;
    private StoreService storeService;

    @BeforeEach
    public void init() {
        ServiceInitializer.reset();
        ServiceInitializer serviceInitializer = ServiceInitializer.getInstance();
        UserService userService = serviceInitializer.getUserService();
        storeService = serviceInitializer.getStoreService();

        userService.register("mormor", "pass1Pas@");
        userService.loginAsSubscriber("mormor", "pass1Pas@");
        subscriber = userService.getUserFacade().getUserRepository().getUser("mormor");

        storeService.addStore("morStore", "mormor", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
        //if a product added without category, it will be added to General category
        Response<String> addProductResponse = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 20, "mormor", subscriber.getToken());
    }

    @Test
    @Order(1)
    public void editProductQuantity() {
        System.out.println("--------------------------------------------------------");
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(store.getId(), "mormor", subscriber.getToken());
        System.out.println(res.getData().size());
        Response<String> response = storeService.setProductQuantity(1, 30, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductQuantity(1, -8, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductQuantity(2, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(2)
    public void editAddProductQuantity() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.addProductQuantity(2, -10, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.addProductQuantity(2, -11, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.addProductQuantity(3, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(3)
    public void editProductPrice() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.setProductPrice(3, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductPrice(3, -8, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductPrice(2, 20, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(4)
    public void editProductName() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.setProductName(4, "NAME CHANGED", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductName(4, "", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductName(6, null, store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(5)
    public void editProductDec() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.setProductDescription(5, "DESC CHANGED", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductDescription(6, "CHANGE MY DESC", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductDescription(5, "", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(6)
    public void editProductCategory() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.assignProductToCategory(6, "category1", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.assignProductToCategory(6, "category1", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.assignProductToCategory(0, "category1", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response3.isSuccess());
        Response<String> response4 = storeService.assignProductToCategory(6, "", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertFalse(response4.isSuccess());
    }

    @Test
    @Order(7)
    public void editAssignProductToCategory() {
        System.out.println("--------------------------------------------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mormor", subscriber.getToken());
        Response<String> response = storeService.assignProductToCategory(7, "category2", store.getId(), "mormor", subscriber.getToken());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    @Order(8)
    public void editRemoveCategoryFromStore() {
        System.out.println("--------------------------------------------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mormor", subscriber.getToken());
        Response<String> response = storeService.isCategoryExist(store.getId(), "General", "mormor", subscriber.getToken());
        Assertions.assertFalse(response.isSuccess());
    }
}
