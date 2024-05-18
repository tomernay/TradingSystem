package src.main.java.Domain.Users.Subscriber.Cart;

import src.main.java.Domain.Store.Inventory.Product;

import java.util.HashMap;
import java.util.Map;

public class Basket {
    private String storeName;
    private Map<Product, Integer> productsQuantityMap;

    // Constructor
    public Basket(String storeName) {
        this.storeName = storeName;
        this.productsQuantityMap = new HashMap<>();
    }

    // Getter for storeName
    public String getStoreName() {
        return storeName;
    }

    // Method to add a product to the basket
    public void addProduct(Product product) {
        addProduct(product, 1); // Default quantity is 1
    }

    // Method to add a product with specified quantity to the basket
    public void addProduct(Product product, int quantity) {
        int currentQuantity = productsQuantityMap.getOrDefault(product, 0);
        productsQuantityMap.put(product, currentQuantity + quantity);
    }

    // Method to remove a product from the basket
    public void removeProduct(Product product) {
        productsQuantityMap.remove(product);
    }

    // Method to get quantity of a product in the basket
    public int getQuantity(Product product) {
        return productsQuantityMap.getOrDefault(product, 0);
    }

    public int try_purchase_basekt() {
////      id_order = service_shop.try_purchase_basekt(productsQuantityMap);
//        return id_order
        return 0;

    }

    public void purchase_basekt() {
//        if(service_shop.try_purchase_basekt(productsQuantityMap){
//            return true;
//        }
//        return false

    }
}