package Presentaion.application.View.Store;

import Presentaion.application.CookiesHandler;
import Presentaion.application.Presenter.StorePresenter.StorePresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

@Route("remove-product")
@PageTitle("Remove Product")
public class RemoveProductView extends VerticalLayout {

    private StorePresenter storePresenter;

    public RemoveProductView() {
        this.storePresenter = new StorePresenter();
        setSizeFull();
        init();
    }

    private void init() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("1000px", 3)
        );

        TextField productIDField = new TextField("Product ID");
        TextField storeIDField = new TextField("Store ID");

        // Retrieve username and token from cookies
        String username = CookiesHandler.getUsernameFromCookies(getRequest());
        String token = CookiesHandler.getTokenFromCookies(getRequest());



        Button removeButton = new Button("Remove Product");
        removeButton.addClickListener(e -> removeProduct(productIDField.getValue(), storeIDField.getValue(), username, token));

        formLayout.add(productIDField, 2);
        formLayout.add(storeIDField, 2);


        add(formLayout, removeButton);
    }

    private void removeProduct(String productID, String storeID, String username, String token) {
        if (productID.isEmpty() || storeID.isEmpty()) {
            Notification.show("Please fill in all fields");
            return;
        }

        // Call the presenter method to remove the product
        storePresenter.removeProduct(this, Integer.parseInt(productID), storeID, username, token);
    }

    public void showSuccessMessage(String message) {
        Notification.show(message);
    }

    public void showErrorMessage(String errorMessage) {
        Notification.show(errorMessage);
    }

    private HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }
}
