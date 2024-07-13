package Domain.Users.Subscriber.Cart;

import Utilities.Response;
import Utilities.SystemLogger;
import jakarta.persistence.*;
import org.hibernate.engine.internal.Cascade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "basket_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_basket_shoppingCart"))
    private List<Basket> baskets;
    boolean inPurchaseProcess = false;
    private transient ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private transient ScheduledFuture<?> future;
    private transient CompletableFuture<String> purchaseFuture;


    public List<Basket> getBaskets() {
        return baskets;
    }

    public Response<String> addProductToCart(Integer storeID, Integer productID, Integer quantity) {
        if (inPurchaseProcess){
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

    public Response<String> removeProductFromCart(Integer storeID, Integer productID) {
        if (inPurchaseProcess){
            SystemLogger.error("[ERROR] Can't add product to cart - purchase process started");
            return Response.error("Error - can't add product to cart - purchase process started", null);
        }
        for (Basket basket : baskets) {
            if (basket.getStoreID().equals(storeID)) {
                return basket.removeProductFromBasket(productID);
            }
        }
        SystemLogger.error("[ERROR] Can't remove product from cart");
        return Response.error("Error - can't remove product from cart", null);
    }

    public Response<String> updateProductInCart(Integer storeID, Integer productID, Integer quantity) {
        if (inPurchaseProcess){
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

    public Response<Map<Integer, Map<Integer, Integer>>> getShoppingCartContents() {
        Map<Integer, Map<Integer, Integer>> products = new HashMap<>();
        for (Basket basket : baskets) {
            products.put(basket.getStoreID(), basket.getProducts());
        }
        SystemLogger.info("[SUCCESS] Retrieved shopping cart contents successfully.");
        return Response.success("Retrieved shopping cart contents successfully", products);
    }

    public Response<String> setInPurchaseProcess(boolean b) {
        if (b && inPurchaseProcess) {
            SystemLogger.error("[ERROR] Purchase process already started.");
            return Response.error("Error - purchase process already started.", null);
        }
        if (!b && !inPurchaseProcess) {
            SystemLogger.error("[ERROR] Purchase process already stopped.");
            return Response.error("Error - purchase process already stopped.", null);
        }
        inPurchaseProcess = b;
        SystemLogger.info("[SUCCESS] Purchase process status updated successfully.");
        return Response.success("Purchase process status updated successfully.", null);
    }

    public synchronized CompletableFuture<String> startPurchaseTimer() {
        purchaseFuture = new CompletableFuture<>();

        if (!inPurchaseProcess) {
            inPurchaseProcess = true;
            future = scheduler.schedule(() -> {
                synchronized (this) {
                    if (this.inPurchaseProcess) {
                        this.inPurchaseProcess = false;
                        SystemLogger.error("[ERROR] 10 minutes have passed. Purchase process cancelled.");
                        purchaseFuture.complete("10 minutes have passed. Purchase process cancelled.");
                    }
                }
            }, 60, TimeUnit.MINUTES);
        } else {
            cancelPurchaseProcess();
            purchaseFuture.complete("Purchase process stopped.");
        }

        return purchaseFuture;
    }

    public synchronized void cancelPurchaseProcess() {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        inPurchaseProcess = false;
        SystemLogger.info("[SUCCESS] Purchase process cancelled successfully.");
    }

    public Boolean isInPurchaseProcess() {
        return inPurchaseProcess;
    }

    public Response<String> updateProductQuantityInCart(Integer storeId, Integer productId, Integer quantity) {
        for (Basket basket : baskets) {
            if (basket.getStoreID().equals(storeId)) {
                return basket.updateProductQuantityInBasket(productId, quantity);
            }
        }
        SystemLogger.error("[ERROR] Can't update product quantity in cart");
        return Response.error("Error - can't update product quantity in cart", null);
    }

    public Response<Map<Integer, Map<Integer, Integer>>> lockAndGetShoppingCartContents() {
        if (inPurchaseProcess) {
            SystemLogger.error("[ERROR] Can't lock and get shopping cart contents - purchase process started");
            return Response.error("Error - can't lock and get shopping cart contents - purchase process started", null);
        }
        return getShoppingCartContents();
    }
}
