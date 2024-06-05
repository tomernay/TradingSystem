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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryEditProductDetailsAsStoreOwnerTest {
    private Subscriber subscriber, subscriber2;
    private ServiceInitializer serviceInitializer;
    private Store store;
    private StoreService storeService;

    @BeforeEach
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

        userService.register("mor","MorPass123!");
        userService.loginAsSubscriber("mor","MorPass123!");
        userService.SendStoreOwnerNomination(store.getId(), "itay", "mor", subscriber.getToken());
        subscriber2 = serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mor");
        userService.ownerNominationResponse("mor",true, subscriber2.getToken());

        //if a product added without category, it will be added to General category
        Response<String> addProductResponse = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 20, "mor", subscriber2.getToken());
    }

    @Test
    @Order(1)
    public void editProductQuantity() {
        System.out.println("--------------------------------------------------------");
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        System.out.println(res.getData().size());
        Response<String> response = storeService.setProductQuantity(1, 30, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductQuantity(1, -8, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductQuantity(2, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(2)
    public void editAddProductQuantity() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.addProductQuantity(2, -10, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.addProductQuantity(2, -11, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.addProductQuantity(3, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(3)
    public void editProductPrice() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.setProductPrice(3, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductPrice(3, -8, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductPrice(2, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(4)
    public void editProductName() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.setProductName(4, "NAME CHANGED", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductName(4, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductName(6, null, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(5)
    public void editProductDec() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.setProductDescription(5, "DESC CHANGED", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductDescription(6, "CHANGE MY DESC", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductDescription(5, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    @Order(6)
    public void editProductCategory() {
        System.out.println("--------------------------------------------------------");
        Response<String> response = storeService.assignProductToCategory(6, "category1", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.assignProductToCategory(6, "category1", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.assignProductToCategory(0, "category1", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
        Response<String> response4 = storeService.assignProductToCategory(6, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response4.isSuccess());
    }

    @Test
    @Order(7)
    public void editAssignProductToCategory() {
        System.out.println("--------------------------------------------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mor", subscriber2.getToken());
        Response<String> response = storeService.assignProductToCategory(7, "category2", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    @Order(8)
    public void editRemoveCategoryFromStore() {
        System.out.println("--------------------------------------------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mor", subscriber2.getToken());
        Response<String> response = storeService.isCategoryExist(store.getId(), "General", "mor", subscriber2.getToken());
        Assertions.assertFalse(response.isSuccess());
    }
}
