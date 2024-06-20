package Presentation.application.Presenter.Store;

import Presentation.application.View.Store.StorePurchaseHistory;
import Service.OrderService;
import Service.ServiceInitializer;
import Domain.OrderDTO;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorePurchaseHistoryPresenter {
    StorePurchaseHistory storePurchaseHistory;
    OrderService orderService;
    private HttpServletRequest request;

    public StorePurchaseHistoryPresenter(HttpServletRequest request) {
        orderService = ServiceInitializer.getInstance().getOrderService();
        this.request = request;
    }

    public void attachView(StorePurchaseHistory view) {
        this.storePurchaseHistory = view;
    }

    public void getStoreHistory(String storeID, Grid<OrderDTO> ordersGrid) {
        if (storeID == null) {
            Notification.show("Please enter a Store ID");
            return;
        }

        List<OrderDTO> storeOrders = orderService.getOrdersHistory(storeID).getData();
        if (storeOrders == null || storeOrders.isEmpty()) {
            Notification.show("No orders found for Store ID: " + storeID);
        } else {
            ordersGrid.setItems(storeOrders);
        }
    }

    public void fetchStoreHistory(String storeID, Grid<OrderDTO> ordersGrid) {
        getStoreHistory(storeID, ordersGrid);
    }
}
