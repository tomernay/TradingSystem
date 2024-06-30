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
    private double totalPrice; // Store the total price

    public ShoppingCartPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        productList = new ArrayList<>();
    }

    public void attachView(ShoppingCartView view) {
        this.view = view;
    }

    public void removeProductFromCart(Integer productId, Integer storeId, String username) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.removeProductFromShoppingCart(storeId, productId, username, token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
            removeCartItem(productId); // Use the same product identifier as in the productList
            updateTotalPriceInCart();
        } else {
            view.showError(response.getMessage());
        }
    }

    public void getShoppingCartContents() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<Map<Integer, Map<Integer, Integer>>> response = userService.getShoppingCartContents(username, token);
        if (response.isSuccess()) {
            Map<Integer, Map<Integer, Integer>> products = response.getData();
            productList.clear(); // Clear existing items
            products.forEach((storeID, storeProducts) -> {
                storeProducts.forEach((product, quantity) -> {
                    Response<ProductDTO> productResponse = storeService.viewProductFromStoreByID(product, storeID, username, token);
                    if (productResponse.isSuccess()) {
                        ProductDTO productDTO = productResponse.getData();
                        productDTO.setQuantity(quantity); // Set quantity in the DTO
                        productList.add(productDTO);
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
        Response<Double> totalPriceResponse = userService.calculateShoppingCartPrice(username, token);

        if (totalPriceResponse.isSuccess()) {
            double totalPrice = totalPriceResponse.getData();
            Response<Double> discountResponse = userService.CalculateDiscounts(username, token);

            if (discountResponse.isSuccess()) {
                double discountAmount = discountResponse.getData();
                double discountPercentage = (discountAmount / totalPrice) * 100;
                view.updateTotalPrice(totalPrice, discountPercentage);
                this.totalPrice = totalPrice - discountAmount; // Store the discounted price
            } else {
                view.showError(discountResponse.getMessage());
                view.updateTotalPrice(totalPrice, 0); // No discount if calculation fails
                this.totalPrice = totalPrice; // Store the total price without discount
            }
        } else {
            view.showError(totalPriceResponse.getMessage());
        }
    }

    public boolean hasAlcoholItemsInCart() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        return storeService.isExistAlcohol(username, token).getData();
    }

    public void initiateCheckout() {
        if (hasAlcoholItemsInCart()) {
            view.showAlcoholConfirmationDialog();
        } else {
            checkout(null);
        }
    }

    public void checkout(Boolean isOver18) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<List<ProductDTO>> response = userService.lockShoppingCart(username, token, isOver18);
        if (response.isSuccess()) {
            view.navigateToPayment(this.totalPrice);
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

    public void removeCartItem(Integer productId) {
        productList.removeIf(item -> item.getProductID().equals(productId));
        view.updateCartGrid(productList); // Update the view with grouped grids
    }

    public String getUsername() {
        return CookiesHandler.getUsernameFromCookies(request);
    }

    public void updateProductQuantityInCart(Integer productId, Integer storeId, Integer quantity, String username) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.updateProductQuantityInCart(storeId, productId, quantity, username, token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
            // Update the local quantity in the productList
            productList.stream()
                    .filter(item -> item.getProductID().equals(productId) && item.getStoreID().equals(storeId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));
            view.updateCartGrid(productList); // Update the view with grouped grids
        } else {
            view.showError(response.getMessage());
        }
        updateTotalPriceInCart();
    }

    public boolean isLoggedIn() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
