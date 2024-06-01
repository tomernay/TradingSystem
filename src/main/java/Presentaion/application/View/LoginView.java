package Presentaion.application.View;

import Presentaion.application.LoginViewContract;
import Presentaion.application.Presenter.LoginPresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Login | Vaadin CRM")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, LoginViewContract.View {

    private final LoginForm login = new LoginForm();
    private final LoginPresenter presenter;

    @Autowired
    public LoginView(LoginPresenter presenter) {
        this.presenter = presenter;
        presenter.attachView(this);

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.addLoginListener(e -> presenter.login(e.getUsername(), e.getPassword()));

        Button registerButton = new Button("Register", e -> navigateToRegister());
        Button guestButton = new Button("Continue as Guest", e -> continueAsGuest());

        HorizontalLayout buttons = new HorizontalLayout(registerButton, guestButton);

        add(new H1("Vaadin CRM"), login, buttons);
    }

    private void continueAsGuest() {
        MainLayout.setSubscriberDetails("guest", null);
        navigateToMain();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }

    @Override
    public void showError(String message) {
        login.setError(true);
    }

    @Override
    public void navigateToMain() {
        getUI().ifPresent(ui -> ui.navigate(""));
    }

    @Override
    public void navigateToRegister() {
        getUI().ifPresent(ui -> ui.navigate("register"));
    }
}
