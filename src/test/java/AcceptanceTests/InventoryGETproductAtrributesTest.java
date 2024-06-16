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
import java.util.Arrays;
import java.util.List;

public class InventoryGETproductAtrributesTest {
    private Subscriber subscriber, subscriber2;
    private ServiceInitializer serviceInitializer;
    private Store store, store2;
    private StoreService storeService;

    @Before
    public void setUp() {
        init();
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
        storeService.addStore("itayStore2", "itay", subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
        store2 = storeService.getStoreFacade().getStoreRepository().getStore("1");

        userService.register("mor", "MorPass123!");
        userService.loginAsSubscriber("mor", "MorPass123!");
        userService.SendManagerNominationRequest(store.getId(), "itay", "mor", List.of("ADD_PRODUCT", "REMOVE_PRODUCT", "REMOVE_CATEGORY", "EDIT_PRODUCT"), subscriber.getToken());
        subscriber2 = serviceInitializer.getUserService().getUserFacade().getUserRepository().getUser("mor");
        userService.managerNominationResponse("mor", true, subscriber2.getToken());
        userService.SendManagerNominationRequest(store2.getId(), "itay", "mor", List.of("ADD_PRODUCT", "REMOVE_PRODUCT", "REMOVE_CATEGORY", "EDIT_PRODUCT"), subscriber.getToken());
        userService.managerNominationResponse("mor", true, subscriber2.getToken());

        storeService.addProductToStore(store.getId(), "product1", "product1Dec", 10, 30, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product2", "product2Dec", 15, 20, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product3", "product3Dec", 20, 15, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product4", "product4Dec", 30, 10, "mor", subscriber2.getToken());
        storeService.addProductToStore(store.getId(), "product5", "product5Dec", 40, 10, new ArrayList<>(Arrays.asList("Food", "Electronic")), "mor", subscriber2.getToken());

        storeService.addProductToStore(store2.getId(), "product1", "product1Dec", 6.5, 44, "mor", subscriber2.getToken());
    }

    @Test
    public void getProductName() {
        System.out.println("-----------------------getProductName---------------------------------");
        Response<String> response1 = storeService.getProductName(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product1", response1.getData());
        Response<String> response2 = storeService.getProductName(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product4", response2.getData());
        Response<String> response3 = storeService.getProductName(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product5", response3.getData());
    }

    @Test
    public void getProductDescription() {
        System.out.println("-----------------------getProductDescription---------------------------------");
        Response<String> response1 = storeService.getProductDescription(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product1Dec", response1.getData());
        Response<String> response2 = storeService.getProductDescription(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product4Dec", response2.getData());
        Response<String> response3 = storeService.getProductDescription(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("product5Dec", response3.getData());
    }

    @Test
    public void getProductPrice() {
        System.out.println("-----------------------getProductPrice---------------------------------");
        Response<String> response1 = storeService.getProductPrice(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("10.0", response1.getData());
        Response<String> response2 = storeService.getProductPrice(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("30.0", response2.getData());
        Response<String> response3 = storeService.getProductPrice(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("40.0", response3.getData());
    }

    @Test
    public void getProductQuantity() {
        System.out.println("-----------------------getProductQuantity---------------------------------");
        Response<String> response1 = storeService.getProductQuantity(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("30", response1.getData());
        Response<String> response2 = storeService.getProductQuantity(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("10", response2.getData());
        Response<String> response3 = storeService.getProductQuantity(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("10", response3.getData());
    }

    @Test
    public void retrieveProductCategories() {
        System.out.println("-----------------------retrieveProductCategories---------------------------------");
        Response<String> response1 = storeService.retrieveProductCategories(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[\"General\"]", response1.getData());
        Response<String> response2 = storeService.retrieveProductCategories(4, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[\"General\"]", response2.getData());
        Response<String> response3 = storeService.retrieveProductCategories(5, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[\"Electronic\",\"Food\"]", response3.getData());

    }

    @Test
    public void viewProductsFromAllStoresByCategory() {
        System.out.println("-----------------------viewProductsFromAllStoresByCategory---------------------------------");
        Response<ArrayList<ProductDTO>> categories = storeService.viewProductFromAllStoresByCategory("General" ,"mor", subscriber2.getToken());
        Assert.assertEquals(5, categories.getData().size());
        System.out.println(categories.getData());

        Response<ArrayList<ProductDTO>> categories2 = storeService.viewProductFromAllStoresByCategory("Food" ,"mor", subscriber2.getToken());
        Assert.assertEquals(1, categories2.getData().size());
        System.out.println(categories2.getData());

        Response<ArrayList<ProductDTO>> categories3 = storeService.viewProductFromAllStoresByCategory("Electronic" ,"mor", subscriber2.getToken());
        Assert.assertEquals(1, categories3.getData().size());
        System.out.println(categories3.getData());
    }

    @Test
    public void retrieveProductsByCategoryFrom_OneStore() {
        System.out.println("-----------------------retrieveProductsByCategoryFrom_OneStore---------------------------------");
        Response<ArrayList<ProductDTO>> categories = storeService.retrieveProductsByCategoryFrom_OneStore(store.getId(), "General" ,"mor", subscriber2.getToken());
        Assert.assertEquals(4, categories.getData().size());
        System.out.println(categories.getData());

        Response<ArrayList<ProductDTO>> categories2 = storeService.retrieveProductsByCategoryFrom_OneStore(store.getId(), "Food" ,"mor", subscriber2.getToken());
        Assert.assertEquals(1, categories2.getData().size());
        System.out.println(categories2.getData());

        Response<ArrayList<ProductDTO>> categories3 = storeService.retrieveProductsByCategoryFrom_OneStore(store.getId(), "Electronic" ,"mor", subscriber2.getToken());
        Assert.assertEquals(1, categories3.getData().size());
        System.out.println(categories3.getData());
    }

    @Test
    public void assignProductToCategory(){
        System.out.println("-----------------------assignProductToCategory---------------------------------");
        Response<String> response = storeService.assignProductToCategory(1, "Food", store.getId(), "mor", subscriber2.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.retrieveProductCategories(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[\"General\",\"Food\"]", response2.getData());
    }

    @Test
    public void removeCategoryFromStore(){
        System.out.println("-----------------------removeCategoryFromStore---------------------------------");
        Response<String> response = storeService.removeCategoryFromStore(store.getId(), "General", "mor", subscriber2.getToken());
        Assert.assertTrue(response.isSuccess());
        Response<String> response2 = storeService.retrieveProductCategories(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("[]", response2.getData());
        Response<ArrayList<ProductDTO>> products = storeService.retrieveProductsByCategoryFrom_OneStore(store.getId(), "General" ,"mor", subscriber2.getToken());
        Assert.assertNull(products.getData());
        Response<ArrayList<ProductDTO>> products2 = storeService.getAllProductsFromStore(store.getId(), "mor", subscriber2.getToken());
        System.out.println("products in store :1");
        for(ProductDTO productDTO : products2.getData()){
            System.out.println(productDTO);
        }
    }

    @Test
    public void viewProductFromStoreByID(){
        System.out.println("-----------------------viewProductFromStoreByID---------------------------------");
        Response<ProductDTO> response = storeService.viewProductFromStoreByID(1, store.getId(), "mor", subscriber2.getToken());
        Assert.assertEquals("ProductINFO{storeID='0', productID=1, name='product1', desc='product1Dec', price=10.0, quantity=30, categories=[General]}", response.getData().toString());
    }

    @Test
    public void viewProductFromStoreByName(){
        System.out.println("-----------------------viewProductFromStoreByName---------------------------------");
        Response<ProductDTO> response = storeService.viewProductFromStoreByName(store.getId(), "product1", "mor", subscriber2.getToken());
        Assert.assertEquals("ProductINFO{storeID='0', productID=1, name='product1', desc='product1Dec', price=10.0, quantity=30, categories=[General]}", response.getData().toString());
        Response<ProductDTO> response2 = storeService.viewProductFromStoreByName(store.getId(), "product5", "mor", subscriber2.getToken());
        Assert.assertEquals("ProductINFO{storeID='0', productID=5, name='product5', desc='product5Dec', price=40.0, quantity=10, categories=[Electronic, Food]}", response2.getData().toString());
    }

    @Test
    public void viewProductFromAllStoresByName(){
        System.out.println("-----------------------viewProductFromAllStoresByName---------------------------------");
        Response<ArrayList<ProductDTO>> response = storeService.viewProductFromAllStoresByName("product1", "mor", subscriber2.getToken());
        Assert.assertEquals(2, response.getData().size());
        System.out.println(response.getData());
    }
}
