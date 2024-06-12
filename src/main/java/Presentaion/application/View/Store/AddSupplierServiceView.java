package Presentaion.application.View.Store;


import Domain.Externals.Suppliers.DefaultSupplier;
import Domain.Externals.Suppliers.SupplierAdapter;
import Presentaion.application.CookiesHandler;
import Presentaion.application.Presenter.StorePresenter.StorePresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

@Route("add-supplier-service")
@PageTitle("Add Payment Service")
public class AddSupplierServiceView extends VerticalLayout {

     StorePresenter presenter;
     HashMap<String, SupplierAdapter> paymentAdapterHashMap;
    public AddSupplierServiceView() {
        // Create a ComboBox (spinner) to pick an item
        presenter=new StorePresenter();

        ComboBox<String> paymentServiceComboBox = new ComboBox<>("Select Payment Service");
        paymentServiceComboBox.setItems("DefaultSupply"); // Add your payment services here
        paymentAdapterHashMap=new HashMap<>();
        paymentAdapterHashMap.put("DefaultSupply",new DefaultSupplier(""));
        // Create a Button to print the selected item
        Button printButton = new Button("Print Selected Service");

        // Add a click listener to the button
        printButton.addClickListener(event -> {
            String user= CookiesHandler.getUsernameFromCookies(getRequest());
            String token= CookiesHandler.getTokenFromCookies(getRequest());
            presenter.addSupplier(this,paymentServiceComboBox.getValue(),paymentServiceComboBox.getValue(),user,token);

        });

        // Add components to the layout
        add(paymentServiceComboBox, printButton);
    }


    public void showSuccessMessage(String message) {
        Notification.show(message);
    }

    public void showErrorMessage(String errorMessage) {
        Notification.show(errorMessage);
    }

    /**
     * connect to Cookies
     * @return HTTP request to retrive data from Cookies
     */
    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }
}
