package Domain.Users.Subscriber.Cart;

import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Basket {
    private final String storeID;
    private List<ProductDTO> products;
    private Map<String, Integer> productsQuantityMap;// <productID, quantity>

    // Constructor
    public Basket(String storeID) {
        this.storeID = storeID;
        this.productsQuantityMap = new HashMap<>();
        products = new ArrayList<>();
    }

    // Getter for storeName
    public String getStoreName() {
        return storeID;
    }


    public Response<String> addProductToBasket(ProductDTO product) {
        if (product.getQuantity() <= 0) {
            SystemLogger.error("[ERROR] Can't add product to basket - quantity is invalid");
            return Response.error("Error - can't add product to basket - quantity is invalid",null);
        }
        for (ProductDTO p : products) {
            if (p.getProductID().equals(product.getProductID())) {
                SystemLogger.error("[ERROR] Can't add product to basket - product already exists in basket");
                return Response.error("Error - can't add product to basket - product already exists in basket",null);
            }
        }
//        if (productsQuantityMap.containsKey(productID)) {
//            SystemLogger.error("[ERROR] Can't add product to basket - product already exists in basket");
//            return Response.error("Error - can't add product to basket - product already exists in basket",null);
//        }
        products.add(product);
//        productsQuantityMap.put(productID, productsQuantityMap.getOrDefault(productID,0) + quantity);
        SystemLogger.info("[SUCCESS] Product added to basket successfully");
        return Response.success("Product added to basket successfully",null);
    }

    public String getStoreID() {
        return storeID;
    }

    public Response<String> removeProductFromBasket(String productID) {
        for (ProductDTO p : products) {
            if (String.valueOf(p.getProductID()).equals(productID)) {
                products.remove(p);
                SystemLogger.info("[SUCCESS] Product removed from basket successfully");
                return Response.success("Product removed from basket successfully",null);
            }
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

    public Response<String> updateProductInBasket(String productID, int quantity) {
        if (quantity < 0) {
            SystemLogger.error("[ERROR] Can't update product in basket - quantity is invalid");
            return Response.error("Error - can't update product in basket - quantity is invalid",null);
        }
        if (quantity == 0) {
            return removeProductFromBasket(productID);
        }
        for (ProductDTO p : products) {
            if (String.valueOf(p.getProductID()).equals(productID)) {
                p.setQuantity(quantity);
                SystemLogger.info("[SUCCESS] Product updated in basket successfully");
                return Response.success("Product updated in basket successfully",null);
            }
        }
//        if(productsQuantityMap.containsKey(productID)){
//            productsQuantityMap.put(productID, quantity);
//            SystemLogger.info("[SUCCESS] Product updated in basket successfully");
//            return Response.success("Product updated in basket successfully",null);
//        }
        SystemLogger.error("[ERROR] Can't update product in basket");
        return Response.error("Error - can't update product in basket",null);
    }

    public Map<String, Integer> getProductsQuantityMap() {
        return productsQuantityMap;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProductsQuantityMap(Map<String, Integer> value) {
        productsQuantityMap = value;
    }

    public Response<String> updateProductQuantityInBasket(String productId, Integer quantity) {
        if (quantity <= 0) {
            SystemLogger.error("[ERROR] Can't update product quantity in basket - quantity is invalid");
            return Response.error("Error - can't update product quantity in basket - quantity is invalid",null);
        }
        for (ProductDTO p : products) {
            if (String.valueOf(p.getProductID()).equals(productId)) {
                p.setQuantity(quantity);
                SystemLogger.info("[SUCCESS] Product quantity updated in basket successfully");
                return Response.success("Product quantity updated in basket successfully",null);
            }
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