package Presentation.application.Presenter.Store;

import Domain.OrderDTO;
import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.StorePurchaseHistory;
import Presentation.application.View.Store.StorePurchaseHistoryAdmin;
import Service.AdminService;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.UserService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class StorePurchaseHistoryAdminPresenter {
    private StorePurchaseHistoryAdmin StorePurchaseHistoryAdmin;
    private final OrderService orderService;
    private final UserService userService;
    private final HttpServletRequest request;
    private final AdminService adminService;

    public StorePurchaseHistoryAdminPresenter(HttpServletRequest request) {
        orderService = ServiceInitializer.getInstance().getOrderService();
        userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
        adminService = ServiceInitializer.getInstance().getAdminService();
    }

    public void attachView(StorePurchaseHistoryAdmin view) {
        this.StorePurchaseHistoryAdmin = view;
    }

    public void getStoreHistory(Integer storeID, Grid<OrderDTO> ordersGrid) {
        if (storeID == null) {
            Notification.show("Please enter a Store ID");
            return;
        }

        List<OrderDTO> storeOrders = adminService.getPurchaseHistoryByStore(storeID).getData();
        if (storeOrders == null || storeOrders.isEmpty()) {
            Notification.show("No orders found for Store ID: " + storeID);
        } else {
            ordersGrid.setItems(storeOrders);
        }
    }

    public void fetchStoreHistory(Integer storeID, Grid<OrderDTO> ordersGrid) {
        getStoreHistory(storeID, ordersGrid);
    }

    public boolean isLoggedIn() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
