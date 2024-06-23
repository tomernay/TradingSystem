package Presentation.application.View.Store;

import Presentation.application.Presenter.Store.StoreManagementPresenter;
import Presentation.application.View.LoginView;
import Presentation.application.View.MainLayoutView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.List;
import Domain.Store.Inventory.ProductDTO;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@PageTitle("Store Management")
@Route(value = "store-management/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class StoreManagementView extends VerticalLayout implements BeforeEnterObserver {

    private final Div content;
    private Integer storeId;
    private String storeName;
    private final StoreManagementPresenter presenter;

    public StoreManagementView(StoreManagementPresenter presenter) {
        content = new Div();
        content.setSizeFull();
        this.presenter = presenter;
        presenter.attachView(this);
        addClassName("store-management-view");

        // Set full size
        setSizeFull();
    }

    private void navigateToStoreReopening() {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add("Are you sure you want to re-open this store?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.reopenStore(storeId);
            confirmationDialog.close();
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");
            showSuccess("Store re-opened successfully");
        });
        confirmButton.addClassName("add-button");

        Button cancelButton = new Button("No", e -> confirmationDialog.close());
        cancelButton.addClassName("waive-button");

        dialogLayout.add(confirmButton, cancelButton);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    private void navigateToStoreClosing() {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add("Are you sure you want to close this store?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.closeStore(storeId);
            confirmationDialog.close();
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");
            showSuccess("Store closed successfully");
        });
        confirmButton.addClassName("add-button");

        Button cancelButton = new Button("No", e -> confirmationDialog.close());
        cancelButton.addClassName("waive-button");

        dialogLayout.add(confirmButton, cancelButton);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    private void navigateToRolesManagement() {
        RouteParameters routeParameters = new RouteParameters("storeId", String.valueOf(storeId));
        UI.getCurrent().navigate(RolesManagementView.class, routeParameters);
    }

    private void navigateToProductManagement() {
        RouteParameters routeParameters = new RouteParameters("storeId", String.valueOf(storeId));
        UI.getCurrent().navigate(ProductManagementView.class, routeParameters);
    }

    private void openCreateDiscountDialog() {
        List<ProductDTO> products = presenter.getProducts(storeId);
        Dialog createDiscountDialog = new CreateDiscountDialog(presenter, storeId, products);
        createDiscountDialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
        String id = event.getRouteParameters().get("storeId").orElse("");
        storeId = Integer.parseInt(id);
        storeId = Integer.parseInt(id);
        storeName = presenter.getStoreName(storeId);
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H1 storeTitle = new H1("Store Management: " + storeName);
        storeTitle.addClassName("store-title");

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        if (presenter.hasPermission(storeId, "VIEW_PRODUCTS")) {
            Button productManagementButton = new Button("Products Management", e -> navigateToProductManagement());
            productManagementButton.addClassName("custom-button");
            buttonLayout.add(productManagementButton);
        }
        if (presenter.hasPermission(storeId, "VIEW_DISCOUNTS_POLICIES")) {
            Button createDiscountButton = new Button("Discounts / Policies", e -> openCreateDiscountDialog());
            createDiscountButton.addClassName("custom-button");
            buttonLayout.add(createDiscountButton);
        }
        if (presenter.hasPermission(storeId, "VIEW_STORE_STAFF_INFO")) {
            Button rolesManagementButton = new Button("Roles Management", e -> navigateToRolesManagement());
            rolesManagementButton.addClassName("custom-button");
            buttonLayout.add(rolesManagementButton);
        }
        if (presenter.hasPermission(storeId, "VIEW_PURCHASE_HISTORY")) {
            Button viewPurchaseHistoryButton = new Button("View Purchase History");
            viewPurchaseHistoryButton.addClassName("custom-button");
            viewPurchaseHistoryButton.addClickListener(event1 -> {
                Integer storeId = this.storeId;
                getUI().ifPresent(ui -> ui.navigate("orders/" + storeId));
            });
            buttonLayout.add(viewPurchaseHistoryButton);
        }

        if (presenter.isCreator(storeId)) {
            if (presenter.isActiveStore(storeId)) {
                Button storeClosingButton = new Button("Close Store", e -> navigateToStoreClosing());
                storeClosingButton.addClassName("custom-button");
                buttonLayout.add(storeClosingButton);
            } else {
                Button storeReopeningButton = new Button("Reopen Store", e -> navigateToStoreReopening());
                storeReopeningButton.addClassName("custom-button");
                buttonLayout.add(storeReopeningButton);
            }
        }

        buttonLayout.setSpacing(true);

        add(storeTitle);
        add(mainLayout);
        mainLayout.add(buttonLayout, content);
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

    public void showError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }
}
