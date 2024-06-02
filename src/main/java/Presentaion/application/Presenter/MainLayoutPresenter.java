package Presentaion.application.Presenter;

import Presentaion.application.View.MainLayoutView;
import Service.ServiceInitializer;
import Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class MainLayoutPresenter {
    private MainLayoutView view;
    private final UserService userService; // Assuming you have a UserService
    private final HttpServletRequest request;


    public MainLayoutPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
    }

    public void attachView(MainLayoutView view) {
        this.view = view;
    }

    private String getTokenFromCookies() {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getUsernameFromCookies() {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void logout() {
        String token = getTokenFromCookies();
        String username = getUsernameFromCookies();
        if (username.contains("Guest")) {
            userService.logoutAsGuest(username);
            view.navigateToLogin();
        }
        else {
            userService.logoutAsSubscriber(username);
            view.navigateToLogin();
        }
    }
}