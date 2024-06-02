package AcceptanceTests;

import Service.ServiceInitializer;
import Service.StoreService;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class ShoppingCart {
    ServiceInitializer serviceInitializer;
    StoreService storeService;
    UserService userService;
    Subscriber buyer,owner;
    Store store;

    @Before
    public void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        storeService=serviceInitializer.getStoreService();
        userService=serviceInitializer.getUserService();
        userService.register("yair12312","Password123!");
        userService.register("newOwner","Password123!");
        userService.register("yair12312","Password123!");
        userService.loginAsSubscriber("yair12312","Password123!");
        userService.loginAsSubscriber("newManager","Password123!");
        buyer=userService.getUserFacade().getUserRepository().getUser("yair12312");
        owner=userService.getUserFacade().getUserRepository().getUser("newOwner");
        storeService.addStore("newStore","newOwner",owner.getToken());



    }


    @Test
    public void AddProuducdToShoppingcart(){
        storeService.addProductToStore("newStore","newProduct","",10, 1,"newOwner",owner.getToken());
        Response<String> res=userService.addProductToShoppingCart("newStore","newProduct","yair12312",buyer.getToken(),1);
        userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
        Response<String> shoppingCart=userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertTrue(shoppingCart.getData().contains("newProduct"));

    }
    @Test
    public void RemoveProuducdFromShoppingcart(){
        storeService.addProductToStore("newStore","newProduct","",10, 1,"newOwner",owner.getToken());
        Response<String> res=userService.addProductToShoppingCart("newStore","newProduct","yair12312",buyer.getToken(),1);
        userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
        Response<String> shoppingCart=userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertTrue(shoppingCart.getData().contains("newProduct"));
        Response<String> res2=userService.removeProductFromShoppingCart("newStore","newProduct","yair12312",buyer.getToken());
        Assert.assertTrue(res2.isSuccess());
        Response<String> shoppingCart2=userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertFalse(shoppingCart2.getData().contains("newProduct"));
    }

    @Test
    public void EditProductAmountInShoppingcart(){
        storeService.addProductToStore("newStore","newProduct","",10, 1,"newOwner",owner.getToken());
        Response<String> res=userService.addProductToShoppingCart("newStore","newProduct","yair12312",buyer.getToken(),1);
        userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
        Response<String> shoppingCart=userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertTrue(shoppingCart.getData().contains("newProduct"));
        Response<String> res2=userService.updateProductInShoppingCart("newStore","newProduct","yair12312",buyer.getToken(),2);
        Assert.assertTrue(res2.isSuccess());
        Response<String> shoppingCart2=userService.getShoppingCartContents("yair12312",buyer.getToken());
        Assert.assertTrue(shoppingCart2.getData().contains("newProduct 2"));
    }



}