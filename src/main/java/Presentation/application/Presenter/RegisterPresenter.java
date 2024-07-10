package Presentation.application.Presenter;


import Presentation.application.View.RegisterView;
import Service.UserService;
import Utilities.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterPresenter {
    @Autowired
    private UserService userService;
    private RegisterView view;

    public RegisterPresenter() {
    }

    public void attachView(RegisterView view) {
        this.view = view;
    }

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
