package Presentaion.application.View.PurchaseHistory;


import Presentaion.application.Presenter.PurchaseHistoryPresenter.StorePurchaseHistoryPresenter;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Div;

import java.util.List;

@Route("orders")
@PageTitle("Order Management")


public class StorePurchaseHistory extends VerticalLayout {

    StorePurchaseHistoryPresenter storePurchaseHistoryPresenter;

    public StorePurchaseHistory() {

        storePurchaseHistoryPresenter = new StorePurchaseHistoryPresenter();
        storePurchaseHistoryPresenter.attachView(this);
        TextField userIdField = new TextField("Store ID");
        Button fetchOrdersButton = new Button("Fetch Orders");
        Div ordersContainer = new Div();
        ordersContainer.setId("Orders-Container");

        fetchOrdersButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                storePurchaseHistoryPresenter.getStoreHistory(userIdField.getValue(), ordersContainer);
            }
        });

        add(userIdField, fetchOrdersButton, ordersContainer);
    }
}
