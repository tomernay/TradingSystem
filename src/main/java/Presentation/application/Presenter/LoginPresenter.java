package Presentation.application.Presenter;

import Presentation.application.View.LoginView;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoginPresenter {
    private LoginView view;
    private final UserService userService; // Assuming you have a UserService

    public LoginPresenter() {
        this.userService = ServiceInitializer.getInstance().getUserService();
    }

    public void attachView(LoginView view) {
        this.view = view;
    }

    public void loginAsSubscriber(String username, String password) {
        try {
            Response<String> response = userService.loginAsSubscriber(username, password);
            if (response.isSuccess()) {
                String token = response.getData();
                if (token != null) {
                    view.loginSuccessful(username, token);
                } else {
                    view.showError("Invalid username or password");
                }
            } else {
                view.showError("Invalid username or password");
            }

        } catch (Exception e) {
            view.showError("An error occurred during login");
        }
    }

    public void loginAsGuest() {
        try {
            Response<List<String>> response = userService.loginAsGuest();
            String username = response.getData().get(0);
            String token = response.getData().get(1);
            if (token != null) {
                view.loginSuccessful(username, token);
            } else {
                view.showError("An error occurred during login");
            }
        } catch (Exception e) {
            view.showError("An error occurred during login");
        }
    }
}
