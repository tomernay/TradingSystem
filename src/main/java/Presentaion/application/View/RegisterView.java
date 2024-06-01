package Presentaion.application.View;

import Presentaion.application.Presenter.RegisterPresenter;
import Presentaion.application.RegisterViewContract;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
@PageTitle("Register | Vaadin CRM")
@AnonymousAllowed
public class RegisterView extends VerticalLayout implements RegisterViewContract.View {

    private final RegisterPresenter presenter;
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");

    @Autowired
    public RegisterView(RegisterPresenter presenter) {
        this.presenter = presenter;
        presenter.attachView(this);

        addClassName("register-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button registerButton = new Button("Register", e -> presenter.register(username.getValue(), password.getValue(), confirmPassword.getValue()));
        Button backButton = new Button("Back to Login", e -> navigateToLogin());

        HorizontalLayout buttons = new HorizontalLayout(registerButton, backButton);

        FormLayout formLayout = new FormLayout(username, password, confirmPassword);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        add(new H1("Register"), formLayout, buttons);
    }

    @Override
    public void showError(String message) {
        confirmPassword.setErrorMessage(message);
        confirmPassword.setInvalid(true);
    }

    @Override
    public void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate("login"));
    }
}
