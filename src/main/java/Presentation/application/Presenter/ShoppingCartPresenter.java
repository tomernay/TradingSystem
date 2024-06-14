package Presentation.application.Presenter;

import Domain.Store.Inventory.ProductDTO;
import Presentation.application.View.ShoppingCartView;
import Presentation.application.CookiesHandler;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ShoppingCartPresenter {

    private ShoppingCartView view;
    private UserService userService;
    private StoreService storeService;
    private final List<ProductDTO> productList;
    private HttpServletRequest request;

    public ShoppingCartPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        productList = new ArrayList<>();
    }

    public void attachView(ShoppingCartView view) {
        this.view = view;
    }

    public void removeProductFromCart(String productId, String storeId, String username) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.removeProductFromShoppingCart(storeId, productId, username, token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
            removeCartItem(productId); // Use the same product identifier as in the productList
        } else {
            view.showError(response.getMessage());
        }
    }

    public void getShoppingCartContents() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<Map<String, Map<String, Integer>>> response = userService.getShoppingCartContents(username, token);
        if (response.isSuccess()) {
            Map<String, Map<String, Integer>> products = response.getData();
            productList.clear(); // Clear existing items
            products.forEach((storeID, storeProducts) -> {
                storeProducts.forEach((productID, quantity) -> {
                    Response<ProductDTO> productResponse = storeService.viewProductFromStoreByID(Integer.parseInt(productID), storeID, username, token);
                    if (productResponse.isSuccess()) {
                        ProductDTO product = productResponse.getData();
                        product.setQuantity(quantity); // Set quantity in the DTO
                        productList.add(product);
                    } else {
                        view.showError("Error fetching product details: " + productResponse.getMessage());
                    }
                });
            });
            view.updateCartGrid(productList); // Update the view with grouped grids
        } else {
            view.showError(response.getMessage());
        }
        updateTotalPriceInCart();
    }

    private void updateTotalPriceInCart() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<Double> response = userService.calculateTotalPriceInCart(username, token);
        if (response.isSuccess()) {
            double totalPrice = response.getData();
            view.updateTotalPrice(totalPrice);
        } else {
            view.showError(response.getMessage());
        }
    }

    public void checkout() {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.LockShoppingCartAndCalculatedPrice(CookiesHandler.getUsernameFromCookies(request), token);
        if (response.isSuccess()) {
            view.navigateToPayment(response.getData());
        } else {
            view.showError(response.getMessage());
        }
        updateTotalPriceInCart();
    }

    public void clearCart() {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.clearCart(CookiesHandler.getUsernameFromCookies(request), token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
            productList.clear(); // Clear the cart items after successful clearing
            view.updateCartGrid(productList); // Update the view with grouped grids
        } else {
            view.showError(response.getMessage());
        }
        updateTotalPriceInCart();
    }

    public void removeCartItem(String productId) {
        productList.removeIf(item -> item.getProductID().toString().equals(productId));
        view.updateCartGrid(productList); // Update the view with grouped grids
    }

    public String getUsername() {
        return CookiesHandler.getUsernameFromCookies(request);
    }

    public void updateProductQuantityInCart(String productId, String storeId, int quantity, String username) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.updateProductQuantityInCart(storeId, productId, quantity, username, token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
            // Update the local quantity in the productList
            productList.stream()
                    .filter(item -> item.getProductID().toString().equals(productId) && item.getStoreID().equals(storeId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));
            view.updateCartGrid(productList); // Update the view with grouped grids
        } else {
            view.showError(response.getMessage());
        }
    }
}
