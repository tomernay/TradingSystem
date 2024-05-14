package Domain.Store.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<Product, Integer> productsQuantityMap;

    // Constructor
    public Inventory() {
        this.productsQuantityMap = new HashMap<>();
    }

    // Method to add a product to the inventory
    public void addProduct(Product product, int quantity) {
        productsQuantityMap.put(product, quantity);
    }

    public void addProduct(Product product) {
        productsQuantityMap.put(product, 0);
    }

    // Method to remove a product from the inventory
    public void removeProduct(Product product) {
        productsQuantityMap.remove(product);
    }

    // Method to update quantity of a product in the inventory
    public void updateQuantity(Product product, int quantity) {
        productsQuantityMap.put(product, quantity);
    }

    // Method to get quantity of a product in the inventory
    public int getQuantity(Product product) {
        return productsQuantityMap.get(product);
    }
}
