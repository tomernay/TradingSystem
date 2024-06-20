package AcceptanceTests;

import Domain.Store.Inventory.ProductDTO;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShoppingCartAcceptanceTest {
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
        Response<String> res=userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Assert.assertTrue(res.isSuccess());
    }

    @Test
    public void AddProuducdNotExistToShoppingcart(){
        storeService.addProductToStore("0","1","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res=userService.addProductToShoppingCart("0","0",1,"yair12312",buyer.getToken());
        Assert.assertFalse(res.isSuccess());
    }
    @Test
    public void AddProuducdNotExistToShoppingcartAlready(){
        storeService.addProductToStore("0","newProduct","DOG",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Response<String> res1=userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Assert.assertFalse(res1.isSuccess());
    }
    @Test
    public void AddProuducdToShoppingcartQuantityZero(){
        storeService.addProductToStore("0","newProduct","tambal",10, 10,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,0,"yair12312",buyer.getToken());
        Assert.assertFalse(res.isSuccess());

    }

    @Test
    public void AddProuducdToShoppingcartQuantityMinos(){
        storeService.addProductToStore("0","newProduct","tambal",10, 10,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,-1,"yair12312",buyer.getToken());
        Assert.assertFalse(res.isSuccess());

    }


    @Test
    public void AddProuducdToShoppingcartShopNotExist(){
        storeService.addProductToStore("0","newProduct","DOG",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("10","ID",1,"yair12312",buyer.getToken());
        Assert.assertFalse(res.isSuccess());

    }

    @Test
    public void RemoveProuducdFromShoppingcart(){
        storeService.addProductToStore("0","ProuducdRemove","DOG",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","ProuducdRemove","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Response<String> res1 = userService.removeProductFromShoppingCart("0",ID,"yair12312",buyer.getToken());
        Assert.assertTrue(res1.isSuccess());
    }

    @Test
    public void RemoveProuducdNotExistromShoppingcart(){
        storeService.addProductToStore("0","ProuducdNotExist","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res = userService.removeProductFromShoppingCart("0","0","yair12312",buyer.getToken());
        Assert.assertFalse(res.isSuccess());
    }




    @Test
    public void EditProductAmountInShoppingcart(){
        storeService.addProductToStore("0","EditProduct","EditProduct",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","EditProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Response<String> res1 = userService.updateProductInShoppingCart("0",ID,"yair12312",buyer.getToken(),2);
        Assert.assertTrue(res1.isSuccess());
    }

    @Test
    public void EditProductNotExistAmountInShoppingcart(){
        storeService.addProductToStore("0","EditProduct","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res=userService.addProductToShoppingCart("0","1",1,"yair12312",buyer.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","EditProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res1 = userService.updateProductInShoppingCart("0","0","yair12312",buyer.getToken(),2);
        Assert.assertFalse(res1.isSuccess());
    }

    @Test
    public void EditProductAmountInShoppingcartMinos(){
        storeService.addProductToStore("0","EditProduct","EditProduct",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","EditProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res=userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Response<String> res1 = userService.updateProductInShoppingCart("0",ID,"yair12312",buyer.getToken(),-2);
        Assert.assertFalse(res1.isSuccess());
    }

}
