package Presentaion.application.Presenter;

import Presentaion.application.CookiesHandler;
import Presentaion.application.View.MainLayoutView;
import Service.ServiceInitializer;
import Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainLayoutPresenter {
    private MainLayoutView view;
    private final UserService userService; // Assuming you have a UserService
    private final HttpServletRequest request;
//    private final Subscriber subscriber;


    public MainLayoutPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
//        this.subscriber = (Subscriber) CookiesHandler.getUserFromCookies(request);
    }

    public void attachView(MainLayoutView view) {
        this.view = view;
    }

    public void search(String search) {

//        view.navigateToSearch(search);
    }

    public void logout() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        assert username != null;
        if (username.contains("Guest")) {
            userService.logoutAsGuest(username);
            view.navigateToLogin();
        }
        else {
            userService.logoutAsSubscriber(username);
            view.navigateToLogin();
        }
    }

    public List<String> getStores(){
        return new ArrayList<>();
    }
}