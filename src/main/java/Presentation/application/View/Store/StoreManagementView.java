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
        addClassName("store-management-view");

        this.presenter = presenter;
        presenter.attachView(this);

        Button productManagementButton = new Button("Products Management", e -> navigateToProductManagement());
        Button createDiscountButton = new Button("Discounts / Policies", e -> openCreateDiscountDialog());
        Button rolesManagementButton = new Button("Roles Management", e -> navigateToRolesManagement());

        VerticalLayout buttonLayout = new VerticalLayout(
                productManagementButton, createDiscountButton, rolesManagementButton
        );
        buttonLayout.setSpacing(true);

        add(buttonLayout, content);
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
    }
}
