package AcceptanceTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

public class InventoryEditProductDetailsAsStoreManagerTest {
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
        store = storeService.getStoreFacade().getStoreRepository().getStore(0);

        userService.register("mor","MorPass123!");
        userService.loginAsSubscriber("mor","MorPass123!");
        Response<Integer> res = userService.SendManagerNominationRequest(store.getId(), "itay", "mor", List.of("MANAGE_PRODUCTS", "MANAGE_DISCOUNTS_POLICIES") ,subscriber.getToken());
        subscriber2 = serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mor");
        userService.managerNominationResponse(res.getData(), "mor",true, subscriber2.getToken());

        //if a product added without category, it will be added to General category
        Response<String> addProductResponse = storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 20, "mor", subscriber2.getToken());
    }

    @Test
    public void editProductQuantity() {
        System.out.println("------------------editProductQuantity------------------------");
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
    public void editAddProductQuantity() {
        System.out.println("------------------editAddProductQuantity------------------------");
        Response<String> response = storeService.addProductQuantity(1, -10, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.addProductQuantity(1, -11, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.addProductQuantity(3, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    public void editProductPrice() {
        System.out.println("------------------editProductPrice------------------------");
        Response<String> response = storeService.setProductPrice(1, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductPrice(1, -8, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductPrice(2, 20, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    public void editProductName() {
        System.out.println("------------------editProductName------------------------");
        Response<String> response = storeService.setProductName(1, "NAME CHANGED", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductName(1, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductName(6, null, store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
    }

    @Test
    public void editProductDec() {
        System.out.println("------------------editProductDec------------------------");
        Response<String> response = storeService.setProductDescription(1, "DESC CHANGED", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.setProductDescription(6, "CHANGE MY DESC", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response2.isSuccess());
        Response<String> response3 = storeService.setProductDescription(1, "", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertFalse(response3.isSuccess());
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
        Response<String> response = storeService.assignProductToCategory(1, "category2", store.getId(), "mor", subscriber2.getToken());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    public void editRemoveCategoryFromStore() {
        System.out.println("------------------editRemoveCategoryFromStore------------------------");
        storeService.removeCategoryFromStore(store.getId(), "General", "mor", subscriber2.getToken());
        Response<String> response = storeService.isCategoryExist(store.getId(), "General", "mor", subscriber2.getToken());
        Assertions.assertFalse(response.isSuccess());
    }


}
