package Presentaion.application.Presenter;

import Presentaion.application.View.OpenStoreView;
import Service.ServiceInitializer;
import Service.UserService;
import jakarta.servlet.http.HttpServletRequest;

public class OpenStorePresenter {
    private OpenStoreView view;
    private final UserService userService; // Assuming you have a UserService
    private final HttpServletRequest request;

    public OpenStorePresenter(HttpServletRequest request) {
        this.userService =  ServiceInitializer.getInstance().getUserService();
        this.request = request;

    }

    public void attachView(OpenStoreView view) {
        this.view = view;
    }
}
