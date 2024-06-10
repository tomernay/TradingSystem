package Presentaion.application.Presenter.PurchaseHistoryPresenter;


import Presentaion.application.View.PurchaseHistory.StorePurchaseHistory;
import Service.OrderService;
import Service.ServiceInitializer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class StorePurchaseHistoryPresenter {
    StorePurchaseHistory storePurchaseHistory;
    OrderService orderService;
    public StorePurchaseHistoryPresenter() {
        orderService = ServiceInitializer.getInstance().getOrderService();
    }
    public void attachView(StorePurchaseHistory view) {
        this.storePurchaseHistory = view;
    }

    public void getStoreHistory(String storeID, Div ordersContainer){
        Map<String,String> storeOrders =  ServiceInitializer.getInstance().getAdminService().getOrderFacade().getOrdersHistory(storeID).getData();


            if (storeID == null) {
                Notification.show("Please enter a Store ID");
                return;
            }

            Collection<String> orders = storeOrders.values();
            if (orders.isEmpty()) {
                Notification.show("No orders found for Store ID: " + storeID);
            } else {

                ordersContainer.removeAll(); // Clear previous results
                orders.forEach(order -> ordersContainer.add(new Div(new Div(new com.vaadin.flow.component.html.Span(order)))));
            }
    }
}
