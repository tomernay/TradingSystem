package Domain.Users.Subscriber.Cart;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {
    private List<Basket> baskets;
    boolean inPurchaseProcess = false;

    public ShoppingCart() {
        this.baskets = new ArrayList<>();
    }

    public List<Basket> getBaskets() {
        return baskets;
    }

    public Response<String> addProductToCart(String storeID, String productID, int quantity) {
        if (!inPurchaseProcess){
            SystemLogger.error("[ERROR] Can't add product to cart - purchase process started");
            return Response.error("Error - can't add product to cart - purchase process started", null);

        }
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
        if (!inPurchaseProcess){
            SystemLogger.error("[ERROR] Can't add product to cart - purchase process  started");
            return Response.error("Error - can't add product to cart - purchase process  started", null);

        }
        for (Basket basket : baskets) {
            if (basket.getStoreID().equals(storeID)) {
                return basket.removeProductFromBasket(productID);
            }
        }
        SystemLogger.error("[ERROR] Can't remove product from cart");
        return Response.error("Error - can't remove product from cart", null);
    }

    public Response<String> updateProductInCart(String storeID, String productID, int quantity) {
        if (!inPurchaseProcess){
            SystemLogger.error("[ERROR] Can't add product to cart - purchase process started");
            return Response.error("Error - can't add product to cart - purchase process started", null);

        }
        for (Basket basket : baskets) {
            if (basket.getStoreID().equals(storeID)) {
                return basket.updateProductInBasket(productID, quantity);
            }
        }
        SystemLogger.error("[ERROR] Can't update product in cart");
        return Response.error("Error - can't update product in cart", null);
    }

    public Response<Map<String, Map<String, Integer>>> getShoppingCartContents() {
        Map<String,Map<String, Integer>> userProducts = new HashMap<>();
        for (Basket basket : baskets) {
            userProducts.put(basket.getStoreID(), basket.getProductsQuantityMap());
        }
        SystemLogger.info("[SUCCESS] get ShoppingCart Contents successfull");
        return Response.success("get ShoppingCart Contents successfull", userProducts);
    }

    public void setInPurchaseProcess(boolean b) {
        inPurchaseProcess = b;
    }

    public Boolean isInPurchaseProcess() {
        return inPurchaseProcess;
    }
}