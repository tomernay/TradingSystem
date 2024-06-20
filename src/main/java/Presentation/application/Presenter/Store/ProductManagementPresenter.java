package Presentation.application.Presenter.Store;

import Domain.Store.Inventory.ProductDTO;
import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.ProductManagementView;
import Service.ServiceInitializer;
import Service.StoreService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ProductManagementPresenter {
    private ProductManagementView view;
    private final StoreService storeService;
    private HttpServletRequest request;

    public ProductManagementPresenter(HttpServletRequest request) {
        this.storeService = ServiceInitializer.getInstance().getStoreService(); // Assume this service is implemented
        this.request = request;
    }

    public void attachView(ProductManagementView view) {
        this.view = view;
    }

    public void loadProducts(Integer storeID) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        List<ProductDTO> products = storeService.getAllProductsFromStore(storeID, username, token).getData();
        view.setProducts(products);
    }

    public void addProduct(String name, String description, Double price, Integer quantity, Set<String> categories) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res;

        ArrayList<String> categoriesList = new ArrayList<>(categories);

        if (categories.isEmpty()) {
            res = storeService.addProductToStore(view.getStoreId(), name, description, price, quantity, username, token);
        } else {
            res = storeService.addProductToStore(view.getStoreId(), name, description, price, quantity, categoriesList, username, token);
        }

        if (res.isSuccess()) {
            loadProducts(view.getStoreId()); // Refresh the product list
        } else {
            view.showError(res.getMessage()); // Show error message
        }
    }

    public void updateProductName(Integer productId, String name) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res = storeService.setProductName(productId, name, view.getStoreId(), username, token);
        handleResponse(res);
    }

    public void updateProductDescription(Integer productId, String description) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res = storeService.setProductDescription(productId, description, view.getStoreId(), username, token);
        handleResponse(res);
    }

    public void updateProductPrice(Integer productId, Double price) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res = storeService.setProductPrice(productId, price, view.getStoreId(), username, token);
        handleResponse(res);
    }

    public void updateProductQuantity(Integer productId, Integer quantity) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res = storeService.setProductQuantity(productId, quantity, view.getStoreId(), username, token);
        handleResponse(res);
    }

    public void addProductCategory(Integer productId, String category) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res = storeService.assignProductToCategory(productId, category, view.getStoreId(), username, token);
        handleResponse(res);
    }

    public void removeProductCategory(Integer productId, String category) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res = storeService.removeProductFromCategory(productId, category, view.getStoreId(), username, token);
        handleResponse(res);
    }

    public void removeProduct(Integer id) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> res = storeService.removeProductFromStore(id, view.getStoreId(), username, token);
        handleResponse(res);
    }

    private void handleResponse(Response<String> res) {
        if (res.isSuccess()) {
            loadProducts(view.getStoreId()); // Refresh the product list
        } else {
            view.showError(res.getMessage()); // Show error message
        }
    }

    public boolean hasPermission(Integer storeId, String permission) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        return storeService.hasPermission(storeId, username, permission);
    }

    public boolean isActiveStore(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.isStoreActive(storeId, username, token);
    }
}
