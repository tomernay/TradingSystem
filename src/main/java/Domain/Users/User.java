package Domain.Users;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Utilities.Response;

import java.util.Map;

public class User {
    private String username;
    private ShoppingCart shoppingCart = null;



    public boolean loginAsGuest() {
        if(shoppingCart == null){
            shoppingCart = new ShoppingCart();
            return true;
        }
        return false;
    }

    public boolean logoutAsGuest(){
        if(shoppingCart != null){
            shoppingCart = null;
            return true;
        }
        return false;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }


    public Response<String> addProductToShoppingCart(String storeID,String productID,int quantity) {
        if(shoppingCart != null){
            return shoppingCart.addProductToCart(storeID, productID, quantity);
        }
        return Response.error("Error - can't add product to cart", null);
    }

    public Response<String> removeProductFromShoppingCart(String storeID, String productID) {
        if(shoppingCart != null){
            return shoppingCart.removeProductFromCart(storeID, productID);
        }
        return Response.error("Error - can't remove product from cart", null);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, int quantity) {
        if(shoppingCart != null){
            return shoppingCart.updateProductInCart(storeID, productID, quantity);
        }
        return Response.error("Error - can't update product in cart", null);
    }

    public Response<Map<String, Map<String, Integer>>> getShoppingCartContents() {
        if(shoppingCart != null){
            return shoppingCart.getShoppingCartContents();
        }
        return Response.error("Error - can't get shopping cart contents", null);
    }
}