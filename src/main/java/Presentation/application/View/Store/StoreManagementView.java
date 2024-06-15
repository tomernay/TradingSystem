package Presentation.application.View.Store;

import Presentation.application.View.MainLayoutView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@PageTitle("Store Management")
@Route(value = "store-management/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class StoreManagementView extends VerticalLayout implements BeforeEnterObserver {

    private Div content;
    private String storeId;

    public StoreManagementView() {
        content = new Div();
        content.setSizeFull();
        addClassName("store-management-view");


        Button productManagementButton = new Button("Products Management", e -> navigateToProductManagement());
//        Button discountsButton = new Button("Discounts", e -> setContent(new DiscountsView()));
//        Button policiesButton = new Button("Policies", e -> setContent(new PoliciesView()));
//        Button paymentButton = new Button("Payment Methods", e -> setContent(new PaymentMethodsView()));
//        Button suppliersButton = new Button("Suppliers", e -> setContent(new SuppliersView()));
//        Button rolesManagementButton = new Button("Roles Management", e -> setContent(new RolesManagementView()));

//        VerticalLayout buttonLayout = new VerticalLayout(
//                productManagementButton, discountsButton, policiesButton,
//                paymentButton, suppliersButton, rolesManagementButton
//        );
        VerticalLayout buttonLayout = new VerticalLayout(
                productManagementButton
        );
        buttonLayout.setSpacing(true);

        add(buttonLayout, content);
    }

    private void navigateToProductManagement() {
        RouteParameters routeParameters = new RouteParameters("storeId", storeId);
        UI.getCurrent().navigate(ProductManagementView.class, routeParameters);
    }

    private void setContent(Component component) {
        content.removeAll();
        content.add(component);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        storeId = event.getRouteParameters().get("storeId").orElse("");
    }
}
