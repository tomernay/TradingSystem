package Presentation.application.View;

import Presentation.application.CookiesHandler;
import Presentation.application.Presenter.LoginPresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
@StyleSheet("context://login-view-styles.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginPresenter presenter;
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");

    public LoginView(LoginPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("register-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button loginButton = new Button("Login", e -> presenter.loginAsSubscriber(username.getValue(), password.getValue()));
        Button registerButton = new Button("Register", e -> navigateToRegister());
        Button guestButton = new Button("Continue as Guest", e -> presenter.loginAsGuest());

        // Change the color of the buttons
        loginButton.getElement().getStyle().set("background-color", "black");
        loginButton.getElement().getStyle().set("color", "white");
        registerButton.getElement().getStyle().set("background-color", "black");
        registerButton.getElement().getStyle().set("color", "white");
        guestButton.getElement().getStyle().set("background-color", "black");
        guestButton.getElement().getStyle().set("color", "white");

        // Set a specific width for the buttons
        loginButton.setWidth("350px");
        registerButton.setWidth("167px");
        guestButton.setWidth("167px");

        HorizontalLayout buttons = new HorizontalLayout(loginButton);
        buttons.setAlignItems(Alignment.CENTER);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout registerGuestButtons = new HorizontalLayout(registerButton, guestButton);
        registerGuestButtons.setAlignItems(Alignment.CENTER);
        registerGuestButtons.setWidthFull();
        registerGuestButtons.setJustifyContentMode(JustifyContentMode.CENTER);

        // Set margins between buttons
        buttons.setSpacing(true);
        registerGuestButtons.setSpacing(true);

        // Set a specific width for the FormLayout
        FormLayout formLayout = new FormLayout(username, password);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setWidth("90%"); // Set the width of the FormLayout to 50%
        formLayout.getStyle().set("margin", "auto"); // Center align the FormLayout

        // Wrap the FormLayout in a FlexLayout
        FlexLayout flexLayout = new FlexLayout(formLayout);
        flexLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the components in the FlexLayout

        H1 loginTitle = new H1("Login");
        loginTitle.getStyle().set("font-size", "4em"); // Set the font size to 2.5em

        add(loginTitle, flexLayout, buttons, registerGuestButtons);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            showError("Invalid username or password");
        }
    }

    private void continueAsGuest() {
        navigateToMain();
    }

    public void showError(String message) {
        password.setErrorMessage(message);
        password.setInvalid(true);
    }

    public void navigateToMain() {
        getUI().ifPresent(ui -> ui.navigate(""));
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
