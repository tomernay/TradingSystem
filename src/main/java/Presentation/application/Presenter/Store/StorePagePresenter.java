package Presentation.application.Presenter.Store;

import Domain.Store.Inventory.ProductDTO;
import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.StorePageView;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class StorePagePresenter {
    StorePageView view;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;
    private final HttpServletRequest request;


    public StorePagePresenter(HttpServletRequest request) {
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

    public List<String> getDiscounts(Integer storeId) {
        return storeService.getDiscountsStrings(storeId);
    }

    public List<String> getPolicies(Integer storeId) {
        return storeService.getPoliciesString(storeId);
    }
}
