package Presentaion.application.Presenter;

import Presentaion.application.RegisterViewContract;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterPresenter implements RegisterViewContract.Presenter {

    private final UserService userService;
    private RegisterViewContract.View view;

    public RegisterPresenter() {
        ServiceInitializer serviceInitializer = ServiceInitializer.getInstance();
        this.userService = serviceInitializer.getUserService();
    }

    public void attachView(RegisterViewContract.View view) {
        this.view = view;
    }

    @Override
    public void register(String username, String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            Response<String> register = userService.register(username, password);
            if (!register.isSuccess()) {
                view.showError(register.getMessage());
                return;
            }
            view.navigateToLogin();
        } else {
            view.showError("Passwords do not match");
        }
    }
}
