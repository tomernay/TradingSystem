package Presentaion.application.View;

import Presentaion.application.CookiesHandler;
import Presentaion.application.Presenter.LoginPresenter;
import Utilities.SystemLogger;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Login | Vaadin CRM")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginPresenter presenter;
    private final LoginForm login = new LoginForm();


    public LoginView(LoginPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setForgotPasswordButtonVisible(false); // Hide the "Forgot Password" button

        login.addLoginListener(e -> presenter.loginAsSubscriber(e.getUsername(), e.getPassword()));

        Button registerButton = new Button("Register", e -> navigateToRegister());
        Button guestButton = new Button("Continue as Guest", e -> presenter.loginAsGuest());

        HorizontalLayout buttons = new HorizontalLayout(registerButton, guestButton);

        add(new H1("Vaadin CRM"), login, buttons);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }

    private void continueAsGuest() {
        navigateToMain();
    }

    public void showError(String message) {
        login.setError(true);
    }

    public void navigateToMain() {
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                ui.navigate("");
            });
        });
    }

    public void navigateToRegister() {
        getUI().ifPresent(ui -> ui.navigate("register"));
    }

    public void loginSuccessful(String username, String token) {
        CookiesHandler.setCookie("username", username, 5 * 60); // 5 minutes
        CookiesHandler.setCookie("token", token, 5 * 60); // 5 minutes
        navigateToMain();
    }

}
