package Presentaion.application.Presenter;

import Presentaion.application.View.ShoppingCartView;
import Presentaion.application.CookiesHandler;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public class ShoppingCartPresenter {

    private ShoppingCartView view;
    private UserService userService;
    private HttpServletRequest request;

    public ShoppingCartPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
    }

    public void attachView(ShoppingCartView view) {
        this.view = view;
    }

    public void addProductToCart(String storeID,String username, String productID, int quantity) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.addProductToShoppingCart(storeID, productID,username,token, quantity);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void removeProductFromCart(String storeID,String username ,String productID) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.removeProductFromShoppingCart(storeID, productID, username, token);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void updateProductInCart(String storeID, String username, String productID, int quantity) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.updateProductInShoppingCart(storeID, productID,username, token, quantity);
        if (response.isSuccess()) {
            view.showSuccess(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void getShoppingCartContents() {
        String token = CookiesHandler.getTokenFromCookies(request);
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
}