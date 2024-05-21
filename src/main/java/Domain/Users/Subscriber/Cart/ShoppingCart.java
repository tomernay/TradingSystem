package src.main.java.Domain.Users.Subscriber.Cart;
import src.main.java.Domain.Store.Inventory.Product;
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
}