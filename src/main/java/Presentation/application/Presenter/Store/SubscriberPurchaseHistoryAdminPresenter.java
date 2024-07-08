package Presentation.application.Presenter.Store;

import Domain.OrderDTO;
import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.StorePurchaseHistoryAdmin;
import Service.AdminService;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.UserService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import Presentation.application.View.Store.SubscriberPurchaseHistoryAdmin;



import java.util.List;

@Component
public class SubscriberPurchaseHistoryAdminPresenter {
    private SubscriberPurchaseHistoryAdmin subscriberPurchaseHistoryAdmin;
    private final OrderService orderService;
    private final UserService userService;
    private final HttpServletRequest request;
    private final AdminService adminService;

    public SubscriberPurchaseHistoryAdminPresenter(HttpServletRequest request) {
        orderService = ServiceInitializer.getInstance().getOrderService();
        userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
        adminService = ServiceInitializer.getInstance().getAdminService();
    }

    public void attachView(SubscriberPurchaseHistoryAdmin view) {
        this.subscriberPurchaseHistoryAdmin = view;
    }



    public void fetchSubscriberHistory(String subscriberName, Grid<OrderDTO> ordersGrid) {
        List<OrderDTO> subscriberOrders = adminService.getPurchaseHistoryBySubscriber(subscriberName).getData();
        if (subscriberOrders == null || subscriberOrders.isEmpty()) {
            Notification.show("No orders found for Subscriber: " + subscriberName);
        } else {
            ordersGrid.setItems(subscriberOrders);
        }
    }

    public boolean isLoggedIn() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
