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
@StyleSheet("context://styles.css")
public class RegisterView extends VerticalLayout{

    private final RegisterPresenter presenter;
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");


    public RegisterView(RegisterPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("page-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button registerButton = new Button("Register", e -> presenter.register(username.getValue(), password.getValue(), confirmPassword.getValue()));
        Button backButton = new Button("Back to Login", e -> navigateToLogin());
        registerButton.addClassName("button");
        backButton.addClassName("button");
        registerButton.setWidth("376px");
        backButton.setWidth("180px");

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
        backButtonLayout.setSpacing(true);

        // Set a specific width for the FormLayout
        FormLayout formLayout = new FormLayout(username, password, confirmPassword);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setWidth("90%"); // Set the width of the FormLayout to 90%
        formLayout.getStyle().set("margin", "auto"); // Center align the FormLayout

        H1 registerTitle = new H1("Register");
        registerTitle.addClassName("title");
        registerTitle.getStyle().set("font-size", "7em"); // Match the font size of the login title

        // Create a container for all elements
        VerticalLayout container = new VerticalLayout();
        container.setAlignItems(Alignment.CENTER); // Center align items
        container.getStyle().set("background-color", "#E6DCD3"); // Set background color to white
        container.getStyle().set("padding", "20px"); // Add padding inside the container
        container.getStyle().set("border", "2px solid #B4A79E"); // Add a border
        container.getStyle().set("border-radius", "8px"); // Add border radius for rounded corners
        container.getStyle().set("width", "23%"); // Set a width relative to the viewport
        container.getStyle().set("min-height", "50vh"); // Set a minimum height relative to the viewport
        container.getStyle().set("max-height", "80vh");

        // Add elements to the container
        container.add(registerTitle, formLayout, buttons, backButtonLayout);

        // Add the container to the view
        add(container);
    }

    public void showError(String message) {
        confirmPassword.setErrorMessage(message);
        confirmPassword.setInvalid(true);
    }

    public void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate("login"));
    }
}
