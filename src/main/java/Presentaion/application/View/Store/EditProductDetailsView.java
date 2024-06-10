package Presentaion.application.View.Store;

import Presentaion.application.Presenter.StorePresenter.StorePresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;

public class EditProductDetailsView extends Div {
      StorePresenter presenter;
    public EditProductDetailsView() {
        presenter=new StorePresenter();

        FormLayout formLayout = new FormLayout();

        TextField productName = new TextField("Product Name");
        TextField productPrice = new TextField("Product Price");
        TextField productDescription = new TextField("Product Description");

        Button saveButton = new Button("Save", event -> {

        });

        formLayout.add(productName, productPrice, productDescription, saveButton);
        add(formLayout);
    }

    public void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

}
