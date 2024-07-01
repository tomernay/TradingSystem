package UnitTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShoppingCartUnitTest {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber buyer,owner;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService=serviceInitializer.getStoreService();
        userService=serviceInitializer.getUserService();
        userService.register("yair12312","Password123!");
        userService.register("newOwner","Password123!");
        userService.loginAsSubscriber("yair12312","Password123!");
        userService.loginAsSubscriber("newOwner","Password123!");
        buyer=userService.getUserFacade().getUserRepository().getSubscriber("yair12312");
        owner=userService.getUserFacade().getUserRepository().getSubscriber("newOwner");
        storeService.addStore("newStore","newOwner",owner.getToken());
    }


    @Test
    public void AddProductToShoppingCart(){
        storeService.addProductToStore(0,"newProduct","tambal",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName(0,"newProduct","newOwner",owner.getToken());
        Integer ID = resID.getData().getProductID();
        userService.addProductToShoppingCart(0,ID,1,"yair12312",buyer.getToken());
        Response<Map<Integer, Map<Integer, Integer>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        boolean found = false;
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : ShoppingCarts.entrySet()) {
            Map<Integer, Integer> products = entry.getValue();
            for (Map.Entry<Integer, Integer> product : products.entrySet()) {
                if (Objects.equals(product.getKey(), ID)) {
                    found = true;
                    break;
                }
            }
        }
        Assert.assertTrue(found);

    }

    @Test
    public void AddProductNotExistToShoppingCart(){
        storeService.addProductToStore(0,"1","DOG",10, 1,"newOwner",owner.getToken());
        userService.addProductToShoppingCart(0,0,1, "yair12312",buyer.getToken());
        Response<Map<Integer, Map<Integer, Integer>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());
    }

    @Test
    public void AddProductToShoppingCartQuantityZero(){
        storeService.addProductToStore(0,"newProduct","tambal",10, 10,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName(0,"newProduct","newOwner",owner.getToken());
        Integer ID = resID.getData().getProductID();
        userService.addProductToShoppingCart(0,ID,0,"yair12312",buyer.getToken());
        Response<Map<Integer, Map<Integer, Integer>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());


    }
    @Test
    public void AddProductToShoppingCartQuantityMinos(){
        storeService.addProductToStore(0,"newProduct","tambal",10, 10,"newOwner",owner.getToken());
        storeService.viewProductFromStoreByName(0,"newProduct","newOwner",owner.getToken());
        Response<Map<Integer, Map<Integer, Integer>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());
    }




    @Test
    public void RemoveProductFromShoppingCart(){
        storeService.addProductToStore(0,"ProuducdRemove","DOG",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName(0,"ProuducdRemove","newOwner",owner.getToken());
        Integer ID = resID.getData().getProductID();
        userService.addProductToShoppingCart(0,ID,1,"yair12312",buyer.getToken());
        Response<String> res1 = userService.removeProductFromShoppingCart(0,ID,"yair12312",buyer.getToken());
        Assert.assertTrue(res1.isSuccess());
        Response<Map<Integer, Map<Integer, Integer>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        System.out.println(ShoppingCarts);
        Assert.assertTrue(ShoppingCarts.get(0).isEmpty());
    }

    @Test
    public void RemoveProductNotExistFromShoppingCart(){
        storeService.addProductToStore(0,"ProductNotExist","DOG",10, 1,"newOwner",owner.getToken());
        userService.removeProductFromShoppingCart(0,0,"yair12312",buyer.getToken());
        Response<Map<Integer, Map<Integer, Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());
    }

    @Test
    public void EditProductAmountInShoppingCart(){
        storeService.addProductToStore(0,"EditProduct","EditProduct",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName(0,"EditProduct","newOwner",owner.getToken());
        Integer ID = resID.getData().getProductID();
        userService.addProductToShoppingCart(0,ID,1,"yair12312",buyer.getToken());
        userService.updateProductInShoppingCart(0,ID,"yair12312",buyer.getToken(),2);
        Response<Map<Integer, Map<Integer, Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        boolean equals = false;
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : ShoppingCarts.entrySet()) {
            Map<Integer, Integer> products = entry.getValue();
            for (Map.Entry<Integer, Integer> product : products.entrySet()) {
                if (product.getKey() == ID && product.getValue()==2) {
                    equals = true;
                    break;
                }
            }
        }
        Assert.assertTrue(equals);

    }

    @Test
    public void EditProductNotExistAmountInShoppingCart(){
        storeService.addProductToStore(0,"EditProduct","DOG",10, 1,"newOwner",owner.getToken());
        userService.addProductToShoppingCart(0,1,1,"yair12312",buyer.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName(0,"EditProduct","newOwner",owner.getToken());
        Integer ID = resID.getData().getProductID();
        userService.updateProductInShoppingCart(0,0,"yair12312",buyer.getToken(),2);
        Response<Map<Integer, Map<Integer, Integer>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<Integer, Map<Integer, Integer>> ShoppingCarts = ResShoppingCart.getData();
        boolean equals = false;
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : ShoppingCarts.entrySet()) {
            Map<Integer, Integer> products = entry.getValue();
            for (Map.Entry<Integer, Integer> product : products.entrySet()) {
                if (product.getKey() == ID && product.getValue()==2) {
                    equals = true;
                    break;
                }
            }
        }
        Assert.assertFalse(equals);
    }
}
