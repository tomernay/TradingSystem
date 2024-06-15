package Presentation.application.View;

import Domain.Store.Inventory.ProductDTO;
import Presentation.application.Presenter.ShoppingCartPresenter;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Shopping Cart")
@Route(value = "shopping-cart", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class ShoppingCartView extends VerticalLayout {

    private final ShoppingCartPresenter presenter;
    private final List<Grid<ProductDTO>> storeGrids; // List to hold grids for each store
    private TextField totalPriceField;
    private HorizontalLayout buttonsLayout;
    private VerticalLayout gridContainer;

    public ShoppingCartView(ShoppingCartPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("shopping-cart-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        storeGrids = new ArrayList<>();

        // Initialize buttons layout
        Button checkoutButton = new Button("Checkout", e -> presenter.checkout());
        Button clearCartButton = new Button("Clear Cart", e -> presenter.clearCart());

        buttonsLayout = new HorizontalLayout(checkoutButton, clearCartButton);
        buttonsLayout.setAlignItems(Alignment.CENTER);
        buttonsLayout.setSpacing(true);

        // Initialize total price field
        totalPriceField = new TextField("Total Price");
        totalPriceField.setReadOnly(true); // Make it read-only
        totalPriceField.setWidth("10em"); // Adjust width as needed
        totalPriceField.setValue("0.00"); // Initial value

        // Add buttons and total price to header layout
        HorizontalLayout headerLayout = new HorizontalLayout(buttonsLayout, totalPriceField);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setWidthFull();
        add(headerLayout);

        // Initialize grid container
        gridContainer = new VerticalLayout();
        gridContainer.setSizeFull();
        gridContainer.setPadding(false);
        gridContainer.setSpacing(false);
        add(gridContainer);

        // Load cart items grouped by store initially
        loadCartItems();
    }

    public void updateTotalPrice(double totalPrice) {
        totalPriceField.setValue(String.format("%.2f", totalPrice));
    }

    public void navigateToPayment(String totalPrice) {
        double Price = Double.parseDouble(totalPrice);
        getUI().ifPresent(ui -> ui.navigate(PaymentView.class, String.valueOf(totalPrice)));
    }

    public void loadCartItems() {
        presenter.getShoppingCartContents();
    }

    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void showError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void updateCartGrid(List<ProductDTO> productList) {
        // Clear existing storeGrids
        removeAllGrids();

        // Create new Grid for each store
        Map<String, List<ProductDTO>> productsByStore = productList.stream()
                .collect(Collectors.groupingBy(ProductDTO::getStoreName));

        if (productsByStore.isEmpty()) {
            showEmptyCartMessage();
        } else {
            hideEmptyCartMessage();
            productsByStore.forEach((storeName, products) -> {
                Grid<ProductDTO> grid = createGridForStore(products, storeName);
                gridContainer.add(grid); // Add each grid to the view
                storeGrids.add(grid);
            });
        }
    }

    private Grid<ProductDTO> createGridForStore(List<ProductDTO> products, String storeName) {
        Grid<ProductDTO> grid = new Grid<>(ProductDTO.class);
        grid.removeColumnByKey("storeID");
        grid.removeColumnByKey("storeName");
        grid.removeColumnByKey("productID");
        grid.removeColumnByKey("quantity");
        grid.removeColumnByKey("name");
        grid.removeColumnByKey("description");
        grid.removeColumnByKey("price");
        grid.removeColumnByKey("categories");

        grid.setItems(products);
        grid.addColumn(ProductDTO::getName).setHeader("Name");
        grid.addColumn(ProductDTO::getDescription).setHeader("Description");
        grid.addColumn(ProductDTO::getPrice).setHeader("Price");

        grid.addColumn(new ComponentRenderer<>(item -> {
            Double totalPrice = item.getPrice() * item.getQuantity();
            return new Text(String.format("%.2f", totalPrice)); // Formatting to 2 decimal places
        })).setHeader("Total Price");

        grid.addColumn(new ComponentRenderer<>(item -> {
            HorizontalLayout layout = new HorizontalLayout();

            IntegerField quantityField = new IntegerField();
            quantityField.setValue(item.getQuantity());
            quantityField.setWidth("4em"); // Adjust width as needed
            quantityField.setMin(1); // Set minimum value allowed

            Button increaseButton = new Button("+", e -> {
                int currentValue = quantityField.getValue() != null ? quantityField.getValue() : 0;
                quantityField.setValue(currentValue + 1);
                updateQuantity(item, currentValue + 1);
            });

            Button decreaseButton = new Button("-", e -> {
                int currentValue = quantityField.getValue() != null ? quantityField.getValue() : 0;
                if (currentValue > 1) { // Ensure the quantity does not go below 1
                    quantityField.setValue(currentValue - 1);
                    updateQuantity(item, currentValue - 1);
                }
            });

            layout.add(increaseButton, quantityField, decreaseButton);
            layout.setAlignItems(Alignment.CENTER);
            layout.setSpacing(true);

            Binder<ProductDTO> binder = new Binder<>(ProductDTO.class);
            binder.forField(quantityField)
                    .bind(ProductDTO::getQuantity, ProductDTO::setQuantity);

            return layout;
        })).setHeader("Quantity");

        grid.addComponentColumn(item -> new Button("Remove", e -> {
            presenter.removeProductFromCart(item.getProductID().toString(), item.getStoreID(), presenter.getUsername());
            presenter.getShoppingCartContents(); // Refresh cart after removal
        })).setHeader("Actions");

        // Set text in the header row cell instead of component
        grid.prependHeaderRow().getCell(grid.getColumns().get(0)).setText("Store Name: " + storeName);

        return grid;
    }

    private void updateQuantity(ProductDTO item, int newQuantity) {
        item.setQuantity(newQuantity);
        presenter.updateProductQuantityInCart(
                item.getProductID().toString(), item.getStoreID(), newQuantity, presenter.getUsername());
    }

    private void showEmptyCartMessage() {
        removeAllGrids(); // Remove all existing grids
        Text message = new Text("Your cart is empty");
        gridContainer.add(message);
    }

    private void hideEmptyCartMessage() {
        gridContainer.removeAll(); // Remove the empty cart message
    }

    private void removeAllGrids() {
        gridContainer.removeAll();
        storeGrids.clear();
    }
}
