package Presentaion.application.Presenter.StorePresenter;

import Presentaion.application.View.MainLayoutView;
import Presentaion.application.View.Store.StorePageView;
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
}
