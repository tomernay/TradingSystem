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
@StyleSheet("context://styles.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginPresenter presenter;
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");

    public LoginView(LoginPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("page-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button loginButton = new Button("Login", e -> presenter.loginAsSubscriber(username.getValue(), password.getValue()));
        Button registerButton = new Button("Register", e -> navigateToRegister());
        Button guestButton = new Button("Continue as Guest", e -> presenter.loginAsGuest());
        loginButton.addClassName("button");
        registerButton.addClassName("button");
        guestButton.addClassName("button");
        loginButton.setWidth("90%");
        registerButton.setWidth("45%");
        guestButton.setWidth("45%");

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
        loginTitle.addClassName("title");
        loginTitle.getStyle().set("font-size", "450%");


        VerticalLayout container = new VerticalLayout();
        container.setAlignItems(Alignment.CENTER); // Center align items
        container.getStyle().set("background-color", "#E6DCD3"); // Set background color to white
        container.getStyle().set("padding", "20px"); // Add padding inside the container
        container.getStyle().set("border", "2px solid #B4A79E"); // Add a border
        container.getStyle().set("border-radius", "8px"); // Add border radius for rounded corners
        container.getStyle().set("width", "23%"); // Set a width relative to the viewport
        container.getStyle().set("min-height", "50vh"); // Set a minimum height relative to the viewport
        container.getStyle().set("max-height", "80vh");
        container.add(loginTitle, formLayout, buttons, registerGuestButtons);
        add(container);
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
        getUI().ifPresent(ui -> ui.access(() -> {
            try {
                ui.navigate("");
            } catch (Exception e) {
                e.printStackTrace(); // Log the exception for debugging
            }
        }));
    }

    public void navigateToRegister() {
        getUI().ifPresent(ui -> ui.navigate("register"));
    }

    public void loginSuccessful(String username, String token) {
        getUI().ifPresent(ui -> ui.access(() -> {
            CookiesHandler.setCookie("username", username, 24 * 60 * 60); // 24 hours
            CookiesHandler.setCookie("token", token, 24 * 60 * 60); // 24 hours
            navigateToMain();
        }));
    }

}
