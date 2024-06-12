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
import java.util.Map;

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
        buyer=userService.getUserFacade().getUserRepository().getUser("yair12312");
        owner=userService.getUserFacade().getUserRepository().getUser("newOwner");
        storeService.addStore("newStore","newOwner",owner.getToken());
    }


    @Test
    public void AddProuducdToShoppingcart(){
        storeService.addProductToStore("0","newProduct","tambal",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,"yair12312",buyer.getToken(),1);
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCardres.get("0").containsKey(ID));

    }

    @Test
    public void AddProuducdNotExistToShoppingcart(){
        storeService.addProductToStore("0","1","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res=userService.addProductToShoppingCart("0","0","yair12312",buyer.getToken(),1);
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCardres.isEmpty());
    }

    @Test
    public void AddProuducdToShoppingcartQuantityZero(){
        storeService.addProductToStore("0","newProduct","tambal",10, 10,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,"yair12312",buyer.getToken(),0);
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCardres.isEmpty());


    }
    @Test
    public void AddProuducdToShoppingcartQuantityMinos(){
        storeService.addProductToStore("0","newProduct","tambal",10, 10,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCardres.isEmpty());
    }




    @Test
    public void RemoveProuducdFromShoppingcart(){
        storeService.addProductToStore("0","ProuducdRemove","DOG",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","ProuducdRemove","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        userService.addProductToShoppingCart("0",ID,"yair12312",buyer.getToken(),1);
        Response<String> res1 = userService.removeProductFromShoppingCart("0",ID,"yair12312",buyer.getToken());
        Assert.assertTrue(res1.isSuccess());
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        System.out.println(ShoppingCardres);
        Assert.assertTrue(ShoppingCardres.get("0").isEmpty());
    }

    @Test
    public void RemoveProuducdNotExistromShoppingcart(){
        storeService.addProductToStore("0","ProuducdNotExist","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res = userService.removeProductFromShoppingCart("0","0","yair12312",buyer.getToken());
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCardres.isEmpty());
    }

    @Test
    public void EditProductAmountInShoppingcart(){
        storeService.addProductToStore("0","EditProduct","EditProduct",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","EditProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res =userService.addProductToShoppingCart("0",ID,"yair12312",buyer.getToken(),1);
        Response<String> res1 = userService.updateProductInShoppingCart("0",ID,"yair12312",buyer.getToken(),2);
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCardres.get("0").get(ID)==2);

    }

    @Test
    public void EditProductNotExistAmountInShoppingcart(){
        storeService.addProductToStore("0","EditProduct","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res = userService.addProductToShoppingCart("0","1","yair12312",buyer.getToken(),1);
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","EditProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res1 = userService.updateProductInShoppingCart("0","0","yair12312",buyer.getToken(),2);
        Response<Map<String,Map<String,Integer>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String,Map<String,Integer>> ShoppingCardres = ResShoppingCart.getData();
        Assert.assertFalse(ShoppingCardres.get("0").get(ID)==2);
    }
}
