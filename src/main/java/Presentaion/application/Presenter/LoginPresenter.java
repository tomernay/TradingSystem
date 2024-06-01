package Presentaion.application.Presenter;

import Presentaion.application.LoginViewContract;
import Presentaion.application.View.MainLayout;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginPresenter implements LoginViewContract.Presenter {

    private final UserService userService;
    private LoginViewContract.View view;

    public LoginPresenter() {
        ServiceInitializer serviceInitializer = ServiceInitializer.getInstance();
        this.userService = serviceInitializer.getUserService();
    }

    public void attachView(LoginViewContract.View view) {
        this.view = view;
    }

    @Override
    public void login(String username, String password) {
        try {
            Response<String> login = userService.loginAsSubscriber(username, password);
            if (!login.isSuccess()) {
                view.showError(login.getMessage());
                return;
            }
            MainLayout.setSubscriberDetails(username, login.getData());
            view.navigateToMain();
        } catch (Exception ex) {
            view.showError("Invalid credentials");
        }
    }
}
