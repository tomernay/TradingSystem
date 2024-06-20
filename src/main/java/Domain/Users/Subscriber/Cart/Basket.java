package Domain.Users.Subscriber.Cart;

import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Basket {
    private final Integer storeID;
    private Map<Integer, Integer> products; //<ProductID, Quantity>
    private Map<String, Integer> productsQuantityMap;// <productID, quantity>

    // Constructor
    public Basket(Integer storeID) {
        this.storeID = storeID;
        this.productsQuantityMap = new HashMap<>();
        products = new HashMap<>();
    }

    // Getter for storeName
    public Integer getStoreName() {
        return storeID;
    }


    public Response<String> addProductToBasket(Integer productID, Integer quantity) {
        if (quantity <= 0) {
            SystemLogger.error("[ERROR] Can't add product to basket - quantity is invalid");
            return Response.error("Error - can't add product to basket - quantity is invalid",null);
        }
        if (products.containsKey(productID)) {
            SystemLogger.error("[ERROR] Can't add product to basket - product already exists in basket");
            return Response.error("Error - can't add product to basket - product already exists in basket",null);
        }
//        if (productsQuantityMap.containsKey(productID)) {
//            SystemLogger.error("[ERROR] Can't add product to basket - product already exists in basket");
//            return Response.error("Error - can't add product to basket - product already exists in basket",null);
//        }
        products.put(productID, quantity);
//        productsQuantityMap.put(productID, productsQuantityMap.getOrDefault(productID,0) + quantity);
        SystemLogger.info("[SUCCESS] Product added to basket successfully");
        return Response.success("Product added to basket successfully",null);
    }

    public Integer getStoreID() {
        return storeID;
    }

    public Response<String> removeProductFromBasket(Integer productID) {
        if (products.containsKey(productID)) {
            products.remove(productID);
            SystemLogger.info("[SUCCESS] Product removed from basket successfully");
            return Response.success("Product removed from basket successfully",null);
        }
        SystemLogger.error("[ERROR] Can't remove product from basket");
        return Response.error("Error - can't remove product from basket",null);
//        if(productsQuantityMap.containsKey(productID)){
//            productsQuantityMap.remove(productID);
//            SystemLogger.info("[SUCCESS] Product removed from basket successfully");
//            return Response.success("Product removed from basket successfully",null);
//        }
//        SystemLogger.error("[ERROR] Can't remove product from basket");
//        return Response.error("Error - can't remove product from basket",null);
    }

    public Response<String> updateProductInBasket(Integer productID, Integer quantity) {
        if (quantity < 0) {
            SystemLogger.error("[ERROR] Can't update product in basket - quantity is invalid");
            return Response.error("Error - can't update product in basket - quantity is invalid",null);
        }
        if (quantity == 0) {
            return removeProductFromBasket(productID);
        }
        if (products.containsKey(productID)) {
            products.put(productID, quantity);
            SystemLogger.info("[SUCCESS] Product updated in basket successfully");
            return Response.success("Product updated in basket successfully",null);
        }
//        if(productsQuantityMap.containsKey(productID)){
//            productsQuantityMap.put(productID, quantity);
//            SystemLogger.info("[SUCCESS] Product updated in basket successfully");
//            return Response.success("Product updated in basket successfully",null);
//        }
        SystemLogger.error("[ERROR] Can't update product in basket");
        return Response.error("Error - can't update product in basket",null);
    }

    public Map<Integer, Integer> getProducts() {
        return products;
    }

    public Response<String> updateProductQuantityInBasket(Integer productId, Integer quantity) {
        if (quantity <= 0) {
            SystemLogger.error("[ERROR] Can't update product quantity in basket - quantity is invalid");
            return Response.error("Error - can't update product quantity in basket - quantity is invalid",null);
        }
        if (products.containsKey(productId)) {
            products.put(productId, quantity);
            SystemLogger.info("[SUCCESS] Product quantity updated in basket successfully");
            return Response.success("Product quantity updated in basket successfully",null);
        }
//        if (productsQuantityMap.containsKey(productId)) {
//            productsQuantityMap.put(productId, quantity);
//            SystemLogger.info("[SUCCESS] Product quantity updated in basket successfully");
//            return Response.success("Product quantity updated in basket successfully",null);
//        }
        SystemLogger.error("[ERROR] Can't update product quantity in basket");
        return Response.error("Error - can't update product quantity in basket",null);
    }
}