package Presentaion.application.View.Store;

import Presentaion.application.CookiesHandler;
import Presentaion.application.Presenter.StorePresenter.StorePresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

public class AddProductView extends Div {
    static StorePresenter presenter;
    private TextField storeID;
    private TextField name;
    private TextField desc;
    private TextField price;
    private TextField quantity;
    private TextField username;
    private TextField token;

    private Button addButton;



    public AddProductView() {
        createForm();
    }

    private void createForm() {
        FormLayout formLayout = new FormLayout();
      presenter=new StorePresenter();
        storeID = new TextField("Store ID");
        name = new TextField("Product Name");
        desc = new TextField("Product Description");
        price = new TextField("Product Price");
        quantity = new TextField("Product Quantity");


        addButton = new Button("Add Product", event -> addProduct());

        formLayout.add(storeID, name, desc, price, quantity, addButton);

        add(formLayout);


    }

    public void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    private void addProduct() {
        try {
            double p=Double.valueOf(price.getValue());
            System.out.println("pass1");
            int q=Integer.valueOf(quantity.getValue());
            System.out.println("pass2");
            String user= CookiesHandler.getUsernameFromCookies(getRequest());
            String token= CookiesHandler.getTokenFromCookies(getRequest());
            presenter.addProduct(this, storeID.getValue(), name.getValue(), desc.getValue(), p,q,user,token);
        }catch (Exception e){
              e.printStackTrace();
        }
    }

    /**
     * connect to Cookies
     * @return HTTP request to retrive data from Cookies
     */
    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }




}
