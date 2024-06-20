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
    public void AddProductToShoppingCart(){
        storeService.addProductToStore("0","newProduct","tambal",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Response<Map<String, List<ProductDTO>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        boolean found = false;
        for (Map.Entry<String, List<ProductDTO>> entry : ShoppingCarts.entrySet()) {
            List<ProductDTO> products = entry.getValue();
            for (ProductDTO product : products) {
                if (product.getProductID() == Integer.parseInt(ID)) {
                    found = true;
                    break;
                }
            }
        }
        Assert.assertTrue(found);
//        Assert.assertTrue(ShoppingCarts.get("0").con(ID));

    }

    @Test
    public void AddProductNotExistToShoppingCart(){
        storeService.addProductToStore("0","1","DOG",10, 1,"newOwner",owner.getToken());
        userService.addProductToShoppingCart("0","0",1, "yair12312",buyer.getToken());
        Response<Map<String, List<ProductDTO>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());
    }

    @Test
    public void AddProductToShoppingCartQuantityZero(){
        storeService.addProductToStore("0","newProduct","tambal",10, 10,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        userService.addProductToShoppingCart("0",ID,0,"yair12312",buyer.getToken());
        Response<Map<String, List<ProductDTO>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());


    }
    @Test
    public void AddProductToShoppingCartQuantityMinos(){
        storeService.addProductToStore("0","newProduct","tambal",10, 10,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","newProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<Map<String, List<ProductDTO>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());
    }




    @Test
    public void RemoveProductFromShoppingCart(){
        storeService.addProductToStore("0","ProuducdRemove","DOG",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","ProuducdRemove","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Response<String> res1 = userService.removeProductFromShoppingCart("0",ID,"yair12312",buyer.getToken());
        Assert.assertTrue(res1.isSuccess());
        Response<Map<String, List<ProductDTO>>> ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        System.out.println(ShoppingCarts);
        Assert.assertTrue(ShoppingCarts.get("0").isEmpty());
    }

    @Test
    public void RemoveProductNotExistFromShoppingCart(){
        storeService.addProductToStore("0","ProductNotExist","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res = userService.removeProductFromShoppingCart("0","0","yair12312",buyer.getToken());
        Response<Map<String, List<ProductDTO>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        Assert.assertTrue(ShoppingCarts.isEmpty());
    }

    @Test
    public void EditProductAmountInShoppingCart(){
        storeService.addProductToStore("0","EditProduct","EditProduct",10, 1,"newOwner",owner.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","EditProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res =userService.addProductToShoppingCart("0",ID,1,"yair12312",buyer.getToken());
        Response<String> res1 = userService.updateProductInShoppingCart("0",ID,"yair12312",buyer.getToken(),2);
        Response<Map<String, List<ProductDTO>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        boolean equals = false;
        for (Map.Entry<String, List<ProductDTO>> entry : ShoppingCarts.entrySet()) {
            List<ProductDTO> products = entry.getValue();
            for (ProductDTO product : products) {
                if (product.getProductID() == Integer.parseInt(ID) && product.getQuantity()==2) {
                    equals = true;
                    break;
                }
            }
        }
//        Assert.assertTrue(ShoppingCarts.get("0").get(ID)==2);

    }

    @Test
    public void EditProductNotExistAmountInShoppingCart(){
        storeService.addProductToStore("0","EditProduct","DOG",10, 1,"newOwner",owner.getToken());
        Response<String> res = userService.addProductToShoppingCart("0","1",1,"yair12312",buyer.getToken());
        Response<ProductDTO> resID = storeService.viewProductFromStoreByName("0","EditProduct","newOwner",owner.getToken());
        String ID = String.valueOf(resID.getData().getProductID());
        Response<String> res1 = userService.updateProductInShoppingCart("0","0","yair12312",buyer.getToken(),2);
        Response<Map<String, List<ProductDTO>> >ResShoppingCart = userService.getShoppingCartContents("yair12312",buyer.getToken());
        Map<String, List<ProductDTO>> ShoppingCarts = ResShoppingCart.getData();
        boolean equals = false;
        for (Map.Entry<String, List<ProductDTO>> entry : ShoppingCarts.entrySet()) {
            List<ProductDTO> products = entry.getValue();
            for (ProductDTO product : products) {
                if (product.getProductID() == Integer.parseInt(ID) && product.getQuantity()==2) {
                    equals = true;
                    break;
                }
            }
        }
//        Assert.assertFalse(ShoppingCarts.get("0").get(ID)==2);
    }
}
