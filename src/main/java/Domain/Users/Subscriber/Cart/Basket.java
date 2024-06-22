package Domain.Users.Subscriber.Cart;

import Utilities.Response;
import Utilities.SystemLogger;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer storeID;

    @ElementCollection
    private Map<Integer, Integer> products; // <ProductID, Quantity>

    // Constructor
    public Basket() {
        this.products = new HashMap<>();
    }

    public Basket(Integer storeID) {
        this.storeID = storeID;
        this.products = new HashMap<>();
    }

    // Getter for storeID
    public Integer getStoreID() {
        return storeID;
    }

    public Response<String> addProductToBasket(Integer productID, Integer quantity) {
        if (quantity <= 0) {
            SystemLogger.error("[ERROR] Can't add product to basket - quantity is invalid");
            return Response.error("Error - can't add product to basket - quantity is invalid", null);
        }
        if (products.containsKey(productID)) {
            SystemLogger.error("[ERROR] Can't add product to basket - product already exists in basket");
            return Response.error("Error - can't add product to basket - product already exists in basket", null);
        }
        products.put(productID, quantity);
        SystemLogger.info("[SUCCESS] Product added to basket successfully");
        return Response.success("Product added to basket successfully", null);
    }

    public Response<String> removeProductFromBasket(Integer productID) {
        if (products.containsKey(productID)) {
            products.remove(productID);
            SystemLogger.info("[SUCCESS] Product removed from basket successfully");
            return Response.success("Product removed from basket successfully", null);
        }
        SystemLogger.error("[ERROR] Can't remove product from basket");
        return Response.error("Error - can't remove product from basket", null);
    }

    public Response<String> updateProductInBasket(Integer productID, Integer quantity) {
        if (quantity < 0) {
            SystemLogger.error("[ERROR] Can't update product in basket - quantity is invalid");
            return Response.error("Error - can't update product in basket - quantity is invalid", null);
        }
        if (quantity == 0) {
            return removeProductFromBasket(productID);
        }
        if (products.containsKey(productID)) {
            products.put(productID, quantity);
            SystemLogger.info("[SUCCESS] Product updated in basket successfully");
            return Response.success("Product updated in basket successfully", null);
        }
        SystemLogger.error("[ERROR] Can't update product in basket");
        return Response.error("Error - can't update product in basket", null);
    }

    public Map<Integer, Integer> getProducts() {
        return products;
    }

    public Response<String> updateProductQuantityInBasket(Integer productId, Integer quantity) {
        if (quantity <= 0) {
            SystemLogger.error("[ERROR] Can't update product quantity in basket - quantity is invalid");
            return Response.error("Error - can't update product quantity in basket - quantity is invalid", null);
        }
        if (products.containsKey(productId)) {
            products.put(productId, quantity);
            SystemLogger.info("[SUCCESS] Product quantity updated in basket successfully");
            return Response.success("Product quantity updated in basket successfully", null);
        }
        SystemLogger.error("[ERROR] Can't update product quantity in basket");
        return Response.error("Error - can't update product quantity in basket", null);
    }
}
