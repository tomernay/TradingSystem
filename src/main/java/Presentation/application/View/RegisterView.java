package Presentation.application.View;

import Presentation.application.Presenter.RegisterPresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register")
@PageTitle("Register | Vaadin CRM")
@AnonymousAllowed
@StyleSheet("context://login-view-styles.css")
public class RegisterView extends VerticalLayout{

    private final RegisterPresenter presenter;
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");


    public RegisterView(RegisterPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("register-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button registerButton = new Button("Register", e -> presenter.register(username.getValue(), password.getValue(), confirmPassword.getValue()));
        Button backButton = new Button("Back to Login", e -> navigateToLogin());

        registerButton.getElement().getStyle().set("background-color", "black");
        registerButton.getElement().getStyle().set("color", "white");
        backButton.getElement().getStyle().set("background-color", "black");
        backButton.getElement().getStyle().set("color", "white");

        // Set a specific width for the buttons
        registerButton.setWidth("350px");
        backButton.setWidth("150px");

        HorizontalLayout buttons = new HorizontalLayout(registerButton);
        buttons.setAlignItems(Alignment.CENTER);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout backButtonLayout = new HorizontalLayout(backButton);
        backButtonLayout.setAlignItems(Alignment.CENTER);
        backButtonLayout.setWidthFull();
        backButtonLayout.setJustifyContentMode(JustifyContentMode.CENTER);


        // Set margins between buttons
        buttons.setSpacing(true);

        // Set a specific width for the FormLayout
        FormLayout formLayout = new FormLayout(username, password, confirmPassword);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setWidth("60%"); // Set the width of the FormLayout to 50%
        formLayout.getStyle().set("margin", "auto"); // Center align the FormLayout

        // Wrap the FormLayout in a FlexLayout
        FlexLayout flexLayout = new FlexLayout(formLayout);
        flexLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the components in the FlexLayout

        H1 registerTitle = new H1("Register");
        registerTitle.getStyle().set("font-size", "5.5em"); // Set the font size to 2.5em
        registerTitle.getStyle().set("font-weight", "bold");

        add(registerTitle, flexLayout, buttons, backButtonLayout);
    }

    public void showError(String message) {
        confirmPassword.setErrorMessage(message);
        confirmPassword.setInvalid(true);
    }

    public void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate("login"));
    }
}
