package Domain.Users;

import Domain.Externals.Security.TokenHandler;
import Domain.Store.Inventory.ProductDTO;
import Domain.Users.Subscriber.Cart.ShoppingCart;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.List;
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
        Token = TokenHandler.generateJWT(this.username);
        return Token;
    }

    public void setToken(String token) {
        Token = token;
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


    public Response<String> addProductToShoppingCart(Integer storeID, Integer productID, Integer quantity) {
        if(quantity <= 0){
            SystemLogger.error("[ERROR] User " + username + " tried to add product with quantity 0 or less");
            return Response.error("Error - can't add product with quantity 0 or less", null);
        }
        if(shoppingCart != null){
            return shoppingCart.addProductToCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't add product to cart", null);
    }

    public Response<String> removeProductFromShoppingCart(Integer storeID, Integer productID) {
        if(shoppingCart != null){
            return shoppingCart.removeProductFromCart(storeID, productID);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't remove product from cart", null);
    }

    public Response<String> updateProductInShoppingCart(Integer storeID, Integer productID, Integer quantity) {
        if(shoppingCart != null){
            return shoppingCart.updateProductInCart(storeID, productID, quantity);
        }
        SystemLogger.error("[ERROR] User " + username + " does not have a shopping cart");
        return Response.error("Error - can't update product in cart", null);
    }

    public Response<Map<Integer, Map<Integer, Integer>>> getShoppingCartContents() {
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
            SystemLogger.info("[SUCCESS] User: " + username + " cleared their shopping cart");
            return Response.success("Cart cleared successfully", null);
        }
        SystemLogger.error("[ERROR] User: " + username + " does not have a shopping cart");
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

    public Response<String> updateProductQuantityInCart(Integer storeId, Integer productId, Integer quantity) {
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

    public boolean isInPurchaseProcess() {
        return shoppingCart.isInPurchaseProcess();
    }
}