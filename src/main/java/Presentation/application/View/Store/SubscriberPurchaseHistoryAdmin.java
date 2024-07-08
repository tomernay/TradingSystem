package Presentation.application.View.Store;

import Domain.OrderDTO;
import Presentation.application.CookiesHandler;
import Presentation.application.View.MainLayoutView;
import Service.AdminService;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.UserService;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@PageTitle("")
@Route(value = "ordersAdminSub/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://styles.css")
public class SubscriberPurchaseHistoryAdmin {
    private SubscriberPurchaseHistoryAdmin subscriberPurchaseHistoryAdmin;
    private final OrderService orderService;
    private final UserService userService;
    private final HttpServletRequest request;
    private final AdminService adminService;

    public SubscriberPurchaseHistoryAdmin(HttpServletRequest request) {
        orderService = ServiceInitializer.getInstance().getOrderService();
        userService = ServiceInitializer.getInstance().getUserService();
        adminService = ServiceInitializer.getInstance().getAdminService();
        this.request = request;

    }





    public boolean isLoggedIn() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
