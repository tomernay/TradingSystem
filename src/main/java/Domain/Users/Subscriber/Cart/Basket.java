package src.main.java.Domain.Users.Subscriber.Cart;

import src.main.java.Domain.Store.Inventory.Product;
import src.main.java.Utilities.Response;

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
        return Response.success("Product added to basket successfully",null);
    }

    public String getStoreID() {
        return storeID;
    }
}