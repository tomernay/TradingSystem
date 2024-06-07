//package Presentaion.application.View;
//
//import Presentaion.application.Presenter.OpenStorePresenter;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.formlayout.FormLayout;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.orderedlayout.FlexLayout;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import Presentaion.application.Presenter.RegisterPresenter;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.dependency.StyleSheet;
//import com.vaadin.flow.component.formlayout.FormLayout;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.orderedlayout.FlexLayout;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.PasswordField;
//import com.vaadin.flow.component.textfield.TextArea;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.server.auth.AnonymousAllowed;
//
//@Route("openStore")
//@PageTitle("Open Store")
//@AnonymousAllowed
//@StyleSheet("context://login-view-styles.css")
//public class OpenStoreView {
//    private final OpenStorePresenter presenter;
//
//    public OpenStoreView(OpenStorePresenter presenter) {
////        addCl("open-store-view");
//        this.presenter = presenter;
//        this.presenter.attachView(this);
//
////        addClassName("register-view");
////        setSizeFull();
////        setAlignItems(FlexComponent.Alignment.CENTER);
////        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//        Button createButton = new Button("Create Store", e -> presenter.openStore(username.getValue(), password.getValue(), confirmPassword.getValue()));
////        Button backButton = new Button("Back to Login", e -> navigateToLogin());
//
//        createButton.getElement().getStyle().set("background-color", "black");
//        createButton.getElement().getStyle().set("color", "white");
////        backButton.getElement().getStyle().set("background-color", "black");
////        backButton.getElement().getStyle().set("color", "white");
//
//        // Set a specific width for the buttons
//        createButton.setWidth("350px");
////        backButton.setWidth("150px");
//
//        TextField storeName = new TextField("Store name");
////        TextArea storeDescription = new TextArea("Store description");
//
//        HorizontalLayout buttons = new HorizontalLayout(registerButton);
//        buttons.setAlignItems(FlexComponent.Alignment.CENTER);
//        buttons.setWidthFull();
//        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//        HorizontalLayout backButtonLayout = new HorizontalLayout(backButton);
//        backButtonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
//        backButtonLayout.setWidthFull();
//        backButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//
//        // Set margins between buttons
//        buttons.setSpacing(true);
//
//        // Set a specific width for the FormLayout
//        FormLayout formLayout = new FormLayout(username, password, confirmPassword);
//        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
//        formLayout.setWidth("60%"); // Set the width of the FormLayout to 50%
//        formLayout.getStyle().set("margin", "auto"); // Center align the FormLayout
//
//        // Wrap the FormLayout in a FlexLayout
//        FlexLayout flexLayout = new FlexLayout(formLayout);
//        flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center the components in the FlexLayout
//
//        H1 registerTitle = new H1("Register");
//        registerTitle.getStyle().set("font-size", "4em"); // Set the font size to 2.5em
//
//        add(registerTitle, flexLayout, buttons, backButtonLayout);
//
//    }
//
//    public void showError(String message) {
//        System.out.println(message);
//    }
//
//    //create a form to add all required fields for opening a store
//    public void openStore() {
//        presenter.openStore();
//    }
//
//
//
//
//}
