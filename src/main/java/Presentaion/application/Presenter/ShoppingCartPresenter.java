package Presentaion.application.Presenter;

import Domain.Store.Inventory.ProductDTO;
import Presentaion.application.View.ShoppingCartView;
import Presentaion.application.CookiesHandler;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
public class ShoppingCartPresenter {

    private ShoppingCartView view;
    private UserService userService;
    private StoreService storeService;
    private HttpServletRequest request;

    public ShoppingCartPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
        this.storeService = ServiceInitializer.getInstance().getStoreService();
    }

    public void attachView(ShoppingCartView view) {
        this.view = view;
    }

    public void removeProductFromCart(String productId, String storeId, String username) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.removeProductFromShoppingCart(storeId, productId, username, token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
            view.removeCartItem(productId); // Use the same product identifier as in the productList
        } else {
            view.showError(response.getMessage());
        }
    }

    public void getShoppingCartContents() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<Map<String, Map<String, Integer>>> response = userService.getShoppingCartContents(username,token);
        if (response.isSuccess()) {
            view.showCartItems(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void checkout() {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.checkout(CookiesHandler.getUsernameFromCookies(request), token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void clearCart() {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.clearCart(CookiesHandler.getUsernameFromCookies(request), token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void getAllProductsFromStore(String storeID) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<ArrayList<ProductDTO>> response = storeService.getAllProductsFromStore(storeID, username, token);
        if (response.isSuccess()) {
            view.showProducts(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void viewProductFromStoreByID(int productID, String storeID) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<ProductDTO> response = storeService.viewProductFromStoreByID(productID, storeID, username, token);
        if (response.isSuccess()) {
            view.showProduct(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }
}