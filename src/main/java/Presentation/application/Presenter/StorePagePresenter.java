package Presentation.application.Presenter;

import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.StorePageView;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


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

    public void search(String search) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        String storeID = "0";
        storeService.viewProductFromStoreByName(storeID, search, username, token);
    }

    public void displayProducts() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        String storeID = "0";
        storeService.getAllProductsFromStore(storeID, username, token);
    }
}
