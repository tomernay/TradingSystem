package Presentation.application.View.Store;

import Presentation.application.Presenter.StoreManagementPresenter;
import Presentation.application.View.MainLayoutView;
import Presentation.application.View.RolesManagementView;
import Service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.List;
import Domain.Store.Inventory.ProductDTO;

@PageTitle("Store Management")
@Route(value = "store-management/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class StoreManagementView extends VerticalLayout implements BeforeEnterObserver {

    private Div content;
    private String storeId;
    private StoreManagementPresenter presenter;

    public StoreManagementView(StoreManagementPresenter presenter) {
        content = new Div();
        content.setSizeFull();
        this.presenter = presenter;
        presenter.attachView(this);
        addClassName("store-management-view");
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

        Button cancelButton = new Button("No", e -> confirmationDialog.close());

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

        Button cancelButton = new Button("No", e -> confirmationDialog.close());

        dialogLayout.add(confirmButton, cancelButton);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    private void navigateToRolesManagement() {
        RouteParameters routeParameters = new RouteParameters("storeId", storeId);
        UI.getCurrent().navigate(RolesManagementView.class, routeParameters);
    }

    private void navigateToProductManagement() {
        RouteParameters routeParameters = new RouteParameters("storeId", storeId);
        UI.getCurrent().navigate(ProductManagementView.class, routeParameters);
    }

    private void setContent(Component component) {
        content.removeAll();
        content.add(component);
    }

    private void openCreateDiscountDialog() {
        List<ProductDTO> products = presenter.getProducts(storeId);
        Dialog createDiscountDialog = new CreateDiscountDialog(presenter, storeId, products);
        createDiscountDialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        storeId = event.getRouteParameters().get("storeId").orElse("");
        VerticalLayout buttonLayout = new VerticalLayout();
        if (presenter.hasPermission(storeId, "VIEW_PRODUCTS")) {
            Button productManagementButton = new Button("Products Management", e -> navigateToProductManagement());
            buttonLayout.add(productManagementButton);
        }
        if (presenter.hasPermission(storeId,"VIEW_DISCOUNTS_POLICIES")) {
            Button createDiscountButton = new Button("Discounts / Policies", e -> openCreateDiscountDialog());
            buttonLayout.add(createDiscountButton);
        }
        if (presenter.hasPermission(storeId,"VIEW_STORE_STAFF_INFO")) {
            Button rolesManagementButton = new Button("Roles Management", e -> navigateToRolesManagement());
            buttonLayout.add(rolesManagementButton);
        }
        if (presenter.isCreator(storeId)) {
            if (presenter.isActiveStore(storeId)) {
                Button storeClosingButton = new Button("Close Store", e -> navigateToStoreClosing());
                buttonLayout.add(storeClosingButton);
            }
            else {
                Button storeReopeningButton = new Button("Reopen Store", e -> navigateToStoreReopening());
                buttonLayout.add(storeReopeningButton);
            }
        }
        buttonLayout.setSpacing(true);

        add(buttonLayout, content);
    }

    public void showError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }
}
