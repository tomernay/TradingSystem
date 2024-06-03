package Domain.Users;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.Map;

public class User {
    protected String username;
    private ShoppingCart shoppingCart;

    public User(String username) {
        this.username = username;
        shoppingCart = new ShoppingCart();
    }

    public boolean logoutAsGuest(){
        if(shoppingCart != null){
            shoppingCart = null;
            return true;
        }
        return false;
    }


    public Response<String> addProductToShoppingCart(String storeID,String productID,int quantity) {
        if(shoppingCart != null){
            return shoppingCart.addProductToCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't add product to cart", null);
    }

    public Response<String> removeProductFromShoppingCart(String storeID, String productID) {
        if(shoppingCart != null){
            return shoppingCart.removeProductFromCart(storeID, productID);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't remove product from cart", null);
    }

    public Response<String> updateProductInShoppingCart(String storeID, String productID, int quantity) {
        if(shoppingCart != null){
            return shoppingCart.updateProductInCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't update product in cart", null);
    }

    public Response<Map<String, Map<String, Integer>>> getShoppingCartContents() {
        if(shoppingCart != null){
            return shoppingCart.getShoppingCartContents();
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't get shopping cart contents", null);
    }
    public String getUsername() {
        return username;
    }
}