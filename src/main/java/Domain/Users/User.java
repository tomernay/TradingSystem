package Domain.Users;

import Domain.Externals.Security.Security;
import Domain.Users.Subscriber.Cart.ShoppingCart;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.Map;

public class User {
    protected String username;
    private ShoppingCart shoppingCart;
    protected String Token;

    public User(String username) {
        this.username = username;
        shoppingCart = new ShoppingCart();
    }

    public String generateToken() {
        Token = Security.generateJWT(this.username);
        return Token;
    }

    public boolean logoutAsGuest(){
        if(shoppingCart != null){
            shoppingCart = null;
            return true;
        }
        return false;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Response<String> addProductToShoppingCart(String storeID,String productName,int quantity) {
        if(quantity <= 0){
            SystemLogger.error("[ERROR] User " + username + " tried to add product with quantity 0 or less");
            return Response.error("Error - can't add product with quantity 0 or less", null);
        }
        if(shoppingCart != null){
            return shoppingCart.addProductToCart(storeID, productName, quantity);
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

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public void lockFlagShoppingCart() {
        shoppingCart.setInPurchaseProcess(true);
    }

    public void unlockFlagShoppingCart() {
        shoppingCart.setInPurchaseProcess(false);
    }

    public Boolean isFlagLock() {
        return shoppingCart.isInPurchaseProcess();
    }
}