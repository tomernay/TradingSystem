package src.main.java.Domain.Users.Subscriber.Cart;
import src.main.java.Utilities.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {
    private List<Basket> baskets;

    public ShoppingCart() {
        this.baskets = new ArrayList<>();
    }

    public List<Basket> getBaskets() {
        return baskets;
    }

    public Response<String> addProductToCart(String storeID, String productID, int quantity) {
        for (Basket basket : baskets) {
            if (basket.getStoreID().equals(storeID)) {
                return basket.addProductToBasket(productID, quantity);
            }
        }
        Basket newBasket = new Basket(storeID);
        baskets.add(newBasket);
        return newBasket.addProductToBasket(productID, quantity);
    }

    public Response<String> removeProductFromCart(String storeID, String productID) {
        for (Basket basket : baskets) {
            if (basket.getStoreID().equals(storeID)) {
                return basket.removeProductFromBasket(productID);
            }
        }
        return Response.error("Error - can't remove product from cart", null);
    }

    public Response<String> updateProductInCart(String storeID, String productID, int quantity) {
        for (Basket basket : baskets) {
            if (basket.getStoreID().equals(storeID)) {
                return basket.updateProductInBasket(productID, quantity);
            }
        }
        return Response.error("Error - can't update product in cart", null);
    }

    public Response<Map<String, Map<String, Integer>>> getShoppingCartContents() {
        Map<String,Map<String, Integer>> userProducts = new HashMap<>();
        for (Basket basket : baskets) {
            userProducts.put(basket.getStoreID(), basket.getProductsQuantityMap());
        }
        return Response.success("get ShoppingCart Contents successfull", userProducts);
    }
}