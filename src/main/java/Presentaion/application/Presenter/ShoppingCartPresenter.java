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

    public void showCartItems(Map<String, Integer> products, String storeID, String username, String token) {
        List<ProductDTO> productList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            String productID = entry.getKey();
            Response<ProductDTO> response = storeService.viewProductFromStoreByID(Integer.parseInt(productID), storeID, username, token);
            if (response.isSuccess()) {
                productList.add(response.getData());
            } else {
                System.out.println("Error: " + response.getMessage());
            }
        }
        view.getCartGrid().setItems(productList);
    }

    public void getShoppingCartContents() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<Map<String, Map<String, Integer>>> response = userService.getShoppingCartContents(username, token);
        if (response.isSuccess()) {
            Map<String, Map<String, Integer>> products = response.getData();
            for (Map.Entry<String, Map<String, Integer>> entry : products.entrySet()) {
                String storeID = entry.getKey();
                Map<String, Integer> storeProducts = entry.getValue();
                showCartItems(storeProducts, storeID, username, token);
            }
        } else {
            view.showError(response.getMessage());
        }
    }

    public void showProducts(ArrayList<ProductDTO> data) {
        productList.clear();
        productList.addAll(data);
        view.getCartGrid().setItems(productList);
    }

    public void showProduct(ProductDTO data) {
        productList.clear();
        productList.add(data);
        view.getCartGrid().setItems(productList);
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
            showProducts(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void viewProductFromStoreByID(int productID, String storeID) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<ProductDTO> response = storeService.viewProductFromStoreByID(productID, storeID, username, token);
        if (response.isSuccess()) {
            showProduct(response.getData());
        } else {
            view.showError(response.getMessage());
        }
    }

    public void removeCartItem(String productId) {
        productList.removeIf(item -> item.getProductID().toString().equals(productId));
        view.getCartGrid().setItems(productList);
    }

    public String getUsername() {
        return CookiesHandler.getUsernameFromCookies(request);
    }

    public void updateProductQuantityInCart(String productId, String storeId, int quantity, String username) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<Map<String, Map<String, Integer>>> response = userService.getShoppingCartContents(username, token);
        Response<String> response2 = userService.updateProductQuantityInCart(storeId, productId, response.getData().get(storeId).get(productId), username, token);
        if (response2.isSuccess()) {
            view.showSuccess(response2.getData());
        } else {
            view.showError(response2.getMessage());
        }
    }
}