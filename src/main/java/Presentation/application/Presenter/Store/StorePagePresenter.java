package Presentation.application.Presenter.Store;

import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.StorePageView;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class StorePagePresenter {
    StorePageView view;
    private final UserService userService; // Assuming you have a UserService
    private final StoreService storeService;
    private final HttpServletRequest request;


    public StorePagePresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.request = request;
    }

    public void attachView(StorePageView view) {
        this.view = view;
    }

    public void search(String search, int storeId) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        storeService.viewProductFromStoreByName(storeId, search, username, token);
    }

    public ArrayList<ProductDTO> getStoresProducts(int storeID) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<ArrayList<ProductDTO>> res = storeService.getAllProductsFromStore(storeID, username, token);
        ArrayList<ProductDTO> products = res.getData();
        //if response is successful add products to products list
        return products;
    }

    public void addToCart(ProductDTO product, int quantity) {
        userService.addProductToShoppingCart(product.getStoreID(), product.getProductID(), quantity, CookiesHandler.getUsernameFromCookies(request), CookiesHandler.getTokenFromCookies(request));
    }

    public String getStoreName(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.getStoreNameByID(storeId, username, token).getData();
    }

    public boolean isLoggedIn() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
