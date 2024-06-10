package Presentaion.application.View.Store;

import Presentaion.application.Presenter.StorePresenter.StorePresenter;

import Utilities.Response;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


@Route("create-discount")
@PageTitle("Create Discount")
public class CreateDiscountView extends VerticalLayout {

    private final StorePresenter storePresenter;

    public CreateDiscountView() {
        this.storePresenter = new StorePresenter();
        createDiscountForm();
    }

    private void createDiscountForm() {
        // Create form fields
        TextField productIdField = new TextField("Product ID");
        TextField storeIdField = new TextField("Store ID");
        TextField categoryField = new TextField("Category");
        TextField percentField = new TextField("Discount Percent");

        // Create a button to submit the form
        Button submitButton = new Button("Create Discount");

        // Add a click listener to the button
        submitButton.addClickListener(event -> {
            String productID = productIdField.getValue();
            String storeID = storeIdField.getValue();
            String category = categoryField.getValue();
            String percent = percentField.getValue();
            String username = getUsernameFromCookies();
            String token = getTokenFromCookies();

           storePresenter.CreatDiscountSimple(this,username, token, productID, storeID, category, percent);

        });

        // Add form fields and button to the layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(productIdField, storeIdField, categoryField, percentField, submitButton);

        add(formLayout);
    }
    public void showSuccessMessage(String message) {
        Notification.show(message);
    }

    public void showErrorMessage(String errorMessage) {
        Notification.show(errorMessage);
    }
    private String getUsernameFromCookies() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("username".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }

    private String getTokenFromCookies() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }

    private HttpServletRequest getRequest() {
        VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();
        if (vaadinRequest instanceof VaadinServletRequest) {
            return ((VaadinServletRequest) vaadinRequest).getHttpServletRequest();
        }
        return null;
    }
}
