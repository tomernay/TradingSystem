package src.main.java.Domain.Users.Subscriber.Cart;
import src.main.java.Domain.Store.Inventory.Product;

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

    public void addProduct(Product product) {
        String storeName = product.getStoreName();

        // Iterate over existing baskets
        for (Basket basket : baskets) {
            // Check if basket belongs to the same store
            if (basket.getStoreName().equals(storeName)) {
                // Add product to the basket
                basket.addProduct(product);
                break;
            }
        }
    }

    public void removeProduct(Product product) {
        String storeName = product.getStoreName();

        // Iterate over existing baskets
        for (Basket basket : baskets) {
            // Check if basket belongs to the same store
            if (basket.getStoreName().equals(storeName)) {
                // Add product to the basket
                basket.removeProduct(product);
                break;
            }
        }
    }

    public void setBaskets(List<Basket> baskets) {
        this.baskets = baskets;
    }

    public void purchase() {
        Map<String, Integer> storeOrders = new HashMap<>();
        boolean can_purchase = true;
        for (Basket basket : baskets) {
            int id_order = basket.try_purchase_basekt();
            if (basket.try_purchase_basekt() != 0) {
                storeOrders.put(basket.getStoreName(), id_order);
            }
            else {
                can_purchase = false;
                break ;
            }
            if (can_purchase){
                // service_shop.purchase(storeOrders)
            }
        }
    }
}