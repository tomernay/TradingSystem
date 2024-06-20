package Presentation.application.View.Store;

import Presentation.application.Presenter.Store.StorePurchaseHistoryPresenter;
import Presentation.application.View.MainLayoutView;
import Domain.OrderDTO;
import Domain.Store.Inventory.ProductDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

@PageTitle("Order Management")
@Route(value = "orders/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class StorePurchaseHistory extends VerticalLayout implements BeforeEnterObserver {

    StorePurchaseHistoryPresenter presenter;
    private Integer storeId;
    private Grid<OrderDTO> ordersGrid;

    public StorePurchaseHistory(StorePurchaseHistoryPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);

        Button backButton = new Button("Back to Store Management", event1 -> {
            RouteParameters routeParameters = new RouteParameters("storeId", storeId.toString());
            UI.getCurrent().navigate(StoreManagementView.class, routeParameters);
        });
        add(backButton);

        ordersGrid = new Grid<>(OrderDTO.class);
        ordersGrid.removeColumnByKey("storeID");
        ordersGrid.removeColumnByKey("status");
        ordersGrid.removeColumnByKey("products");

        // Add the "products" column with "click to see products" message
        ordersGrid.addColumn(new ComponentRenderer<>(order -> {
            Button button = new Button("Click to see products");
            button.addClickListener(e -> {
                ordersGrid.setDetailsVisible(order, !ordersGrid.isDetailsVisible(order));
            });
            return button;
        })).setHeader("Products");

        // Set the item details renderer for displaying product details
        ordersGrid.setItemDetailsRenderer(new ComponentRenderer<>(order -> {
            Grid<ProductDTO> productGrid = new Grid<>(ProductDTO.class);
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
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get("storeId").orElse("");
        if (id.isEmpty()) {
            event.rerouteTo("");
        }
        storeId = Integer.parseInt(id);
        presenter.fetchStoreHistory(storeId, ordersGrid);
    }
}
