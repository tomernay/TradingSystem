package Domain.Users.Subscriber.Cart;

import Domain.Store.Inventory.Product;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.HashMap;
import java.util.Map;

public class Basket {
    private String storeID;
    private Map<String, Integer> productsQuantityMap;// <productID, quantity>

    // Constructor
    public Basket(String storeID) {
        this.storeID = storeID;
        this.productsQuantityMap = new HashMap<>();
    }

    // Getter for storeName
    public String getStoreName() {
        return storeID;
    }


    public Response<String> addProductToBasket(String productID, int quantity) {
        productsQuantityMap.put(productID, productsQuantityMap.getOrDefault(productID,0) + quantity);
        SystemLogger.info("[SUCCESS] Product added to basket successfully");
        return Response.success("Product added to basket successfully",null);
    }

    public String getStoreID() {
        return storeID;
    }

    public Response<String> removeProductFromBasket(String productID) {
        if(productsQuantityMap.containsKey(productID)){
            productsQuantityMap.remove(productID);
            SystemLogger.info("[SUCCESS] Product removed from basket successfully");
            return Response.success("Product removed from basket successfully",null);
        }
        SystemLogger.error("[ERROR] Can't remove product from basket");
        return Response.error("Error - can't remove product from basket",null);
    }

    public Response<String> updateProductInBasket(String productID, int quantity) {
        if(productsQuantityMap.containsKey(productID)){
            productsQuantityMap.put(productID, quantity);
            SystemLogger.info("[SUCCESS] Product updated in basket successfully");
            return Response.success("Product updated in basket successfully",null);
        }
        SystemLogger.error("[ERROR] Can't update product in basket");
        return Response.error("Error - can't update product in basket",null);
    }

    public Map<String, Integer> getProductsQuantityMap() {
        return productsQuantityMap;
    }
}