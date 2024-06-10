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

    private Binder<Product> binder;

    public AddProductView() {
        createForm();
    }

    private void createForm() {
        FormLayout formLayout = new FormLayout();

        storeID = new TextField("Store ID");
        name = new TextField("Product Name");
        desc = new TextField("Product Description");
        price = new TextField("Product Price");
        quantity = new TextField("Product Quantity");


        addButton = new Button("Add Product", event -> addProduct());

        formLayout.add(storeID, name, desc, price, quantity, addButton);

        add(formLayout);

        binder = new Binder<>(Product.class);
        binder.bindInstanceFields(this);
    }

    public void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    private void addProduct() {
        try {
           // double p=Double.valueOf(price.getValue());
            System.out.println("pass1");
          //  int q=Integer.valueOf(quantity.getValue());
            System.out.println("pass2");
            String user= CookiesHandler.getUsernameFromCookies(getRequest());
            String token= CookiesHandler.getUsernameFromCookies(getRequest());
            presenter.addProduct(this, storeID.getValue(), name.getValue(), desc.getValue(), 20.0,10,user,token);
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



    private static class Product {
        private String storeID;
        private String name;
        private String desc;
        private String price;
        private String quantity;
        private String username;
        private String token;

        // Getters and setters for each field
        public String getStoreID() { return storeID; }
        public void setStoreID(String storeID) { this.storeID = storeID; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDesc() { return desc; }
        public void setDesc(String desc) { this.desc = desc; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
        public String getQuantity() { return quantity; }
        public void setQuantity(String quantity) { this.quantity = quantity; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
