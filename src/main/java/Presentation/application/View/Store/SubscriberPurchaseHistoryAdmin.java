package Presentation.application.View.Store;

import Domain.OrderDTO;
import Domain.Store.Inventory.ProductDTO;
import Presentation.application.Presenter.Store.SubscriberPurchaseHistoryAdminPresenter;
import Presentation.application.View.LoginView;
import Presentation.application.View.MainLayoutView;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@PageTitle("")
@Route(value = "ordersAdminSub/:subscriber", layout = MainLayoutView.class)
@StyleSheet("context://styles.css")
public class SubscriberPurchaseHistoryAdmin extends VerticalLayout implements BeforeEnterObserver {
    private SubscriberPurchaseHistoryAdminPresenter presenter;
    private String subscriber;
    private Grid<OrderDTO> ordersGrid;

    public SubscriberPurchaseHistoryAdmin(SubscriberPurchaseHistoryAdminPresenter presenter) {

        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("page-view");

        ordersGrid = new Grid<>(OrderDTO.class);
        ordersGrid.addClassName("custom-grid");
        ordersGrid.removeColumnByKey("storeID");
        ordersGrid.removeColumnByKey("status");
        ordersGrid.removeColumnByKey("products");

        // Add the "products" column with "click to see products" message
        ordersGrid.addColumn(new ComponentRenderer<>(order -> {
            Button button = new Button("Click to see products");
            button.addClickListener(e -> {
                ordersGrid.setDetailsVisible(order, !ordersGrid.isDetailsVisible(order));
            });
            button.addClassName("button");
            return button;
        })).setHeader("Products");

        // Set the item details renderer for displaying product details
        ordersGrid.setItemDetailsRenderer(new ComponentRenderer<>(order -> {
            Grid<ProductDTO> productGrid = new Grid<>(ProductDTO.class);
            productGrid.addClassName("custom-grid");
            productGrid.addColumn(ProductDTO::getProductID).setHeader("Product ID");
            productGrid.addColumn(ProductDTO::getProductName).setHeader("Name");
            productGrid.addColumn(ProductDTO::getQuantity).setHeader("Quantity");
            productGrid.addColumn(ProductDTO::getPrice).setHeader("Price");
            productGrid.addColumn(product -> product.getQuantity() * product.getPrice()).setHeader("Total Price");

            // Hide unwanted columns
            productGrid.removeColumnByKey("productID");
            productGrid.removeColumnByKey("productName");
            productGrid.removeColumnByKey("quantity");
            productGrid.removeColumnByKey("price");
            productGrid.removeColumnByKey("description");
            productGrid.removeColumnByKey("categories");
            productGrid.removeColumnByKey("storeName");
            productGrid.removeColumnByKey("storeID");

            productGrid.setItems(order.getProducts());

            return new VerticalLayout(productGrid);
        }));

        add(ordersGrid);


        UI.getCurrent().getPage().executeJs(
                "document.body.addEventListener('click', function() {" +
                        "    $0.$server.handleUserAction();" +
                        "});",
                getElement()
        );
    }




@Override
public void beforeEnter(BeforeEnterEvent event) {
    if (!presenter.isLoggedIn() || !isLoggedIn()) {
        event.rerouteTo(LoginView.class);
    }
    String subName = event.getRouteParameters().get("subscriber").orElse("");
    if (subName.isEmpty()) {
        event.rerouteTo("");
    }
    subscriber = subName;
    presenter.fetchSubscriberHistory(subscriber, ordersGrid);
}

    @ClientCallable
    public void handleUserAction() {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            Notification.show("Token has timed out! Navigating you to login page...");
            UI.getCurrent().navigate(LoginView.class);
        }
    }


    private boolean isLoggedIn() {
        // Retrieve the current HTTP request
        HttpServletRequest request = (HttpServletRequest) VaadinService.getCurrentRequest();

        if (request != null) {
            // Retrieve cookies from the request
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        // Assuming a valid token indicates a logged-in user
                        return isValidToken(cookie.getValue());
                    }
                }
            }
        }

        // If no valid token is found, the user is not logged in
        return false;
    }

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty();
    }
}
