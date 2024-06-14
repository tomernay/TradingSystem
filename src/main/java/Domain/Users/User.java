package Domain.Users;

import Domain.Externals.Security.Security;
import Domain.Users.Subscriber.Cart.ShoppingCart;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class User {
    protected String username;
    protected ShoppingCart shoppingCart;
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
        return shoppingCart.getShoppingCartContents();
    }
    public String getUsername() {
        return username;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Response<String> lockFlagShoppingCart() {
        return shoppingCart.setInPurchaseProcess(true);
    }

    public Response<String> unlockFlagShoppingCart() {
        return shoppingCart.setInPurchaseProcess(false);
    }

    public Boolean isFlagLock() {
        return shoppingCart.isInPurchaseProcess();
    }

    public Response<String> clearCart() {
        if(shoppingCart != null){
            shoppingCart = new ShoppingCart();
            return Response.success("Cart cleared successfully", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't clear cart", null);
    }

    public Response<String> checkout() {
        if(shoppingCart != null){
            shoppingCart = new ShoppingCart();
            return Response.success("Checkout successful", null);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't checkout", null);
    }

    public Response<String> updateProductQuantityInCart(String storeId, String productId, Integer quantity) {
        if(shoppingCart != null){
            return shoppingCart.updateProductQuantityInCart(storeId, productId, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't update product quantity in cart", null);
    }

    public CompletableFuture<String> startPurchaseTimer() {
        return shoppingCart.startPurchaseTimer();
    }

    public void interruptPurchaseTimer() {
        shoppingCart.cancelPurchaseProcess();
    }

    public Response<Map<String, Map<String, Integer>>> lockAndGetShoppingCartContents() {
        return shoppingCart.lockAndGetShoppingCartContents();
    }

    public boolean isInPurchaseProcess() {
        return shoppingCart.isInPurchaseProcess();
    }
}