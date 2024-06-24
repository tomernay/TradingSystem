package Presentation.application.View;

import Domain.Store.Inventory.ProductDTO;
import Presentation.application.Presenter.ShoppingCartPresenter;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Shopping Cart")
@Route(value = "shopping-cart", layout = MainLayoutView.class)
@StyleSheet("context://styles.css")
public class ShoppingCartView extends VerticalLayout implements BeforeEnterObserver {

    private final ShoppingCartPresenter presenter;
    private final List<Grid<ProductDTO>> storeGrids; // List to hold grids for each store
    private Span totalPriceSpan;
    private HorizontalLayout buttonsLayout;
    private VerticalLayout gridContainer;

    public ShoppingCartView(ShoppingCartPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("page-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        storeGrids = new ArrayList<>();

        // Initialize buttons layout
        Button checkoutButton = new Button("Checkout", e -> presenter.checkout());
        Button clearCartButton = new Button("Clear Cart", e -> presenter.clearCart());
        checkoutButton.addClassName("button");
        clearCartButton.addClassName("button");

        buttonsLayout = new HorizontalLayout(checkoutButton, clearCartButton);
        buttonsLayout.setAlignItems(Alignment.CENTER);
        buttonsLayout.setSpacing(true);

        // Initialize total price field
        totalPriceSpan = new Span("Total Price: 0.00");
        totalPriceSpan.getElement().getStyle().set("font-size", "1.5em");

        // Create a div for total price with title
        Div totalPriceDiv = new Div();
        totalPriceDiv.addClassName("total-price-div");
        Span totalPriceLabel = new Span("Total Price: ");
        totalPriceLabel.getElement().getStyle().set("font-size", "1.5em");
        totalPriceLabel.getElement().getStyle().set("font-weight", "bold");
        totalPriceDiv.add(totalPriceLabel, totalPriceSpan);

        // Add buttons and total price to header layout
        HorizontalLayout headerLayout = new HorizontalLayout(buttonsLayout, totalPriceDiv);
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


        UI.getCurrent().getPage().executeJs(
                "document.body.addEventListener('click', function() {" +
                        "    $0.$server.handleUserAction();" +
                        "});",
                getElement()
        );
    }

    @ClientCallable
    public void handleUserAction() {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            Notification.show("Token has timed out! Navigating you to login page...");
            UI.getCurrent().navigate(LoginView.class);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if the user is already logged in
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            event.rerouteTo(LoginView.class);
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

    public void updateTotalPrice(double totalPrice, double discountPercentage) {
        double discountedPrice = calculateDiscountedPrice(totalPrice, discountPercentage);
        if (discountPercentage > 0) {
            totalPriceSpan.getElement().setProperty("innerHTML",
                    String.format("<span style='text-decoration: line-through;'>%.2f</span> " +
                                    "<span style='color: red;'>%.2f</span>",
                            totalPrice, discountedPrice));
        } else {
            totalPriceSpan.setText(String.format("%.2f", totalPrice));
            totalPriceSpan.getElement().removeProperty("innerHTML");
        }
    }

    private double calculateDiscountedPrice(double originalPrice, double discountPercentage) {
        return originalPrice - (originalPrice * discountPercentage / 100);
    }

    public void navigateToPayment(double totalPrice) {
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
        grid.addClassName("custom-grid");
        grid.removeColumnByKey("storeID");
        grid.removeColumnByKey("storeName");
        grid.removeColumnByKey("productID");
        grid.removeColumnByKey("quantity");
        grid.removeColumnByKey("productName");
        grid.removeColumnByKey("description");
        grid.removeColumnByKey("price");
        grid.removeColumnByKey("categories");

        grid.setItems(products);
        grid.addColumn(ProductDTO::getProductName).setHeader("Name");
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
            increaseButton.addClassName("button");

            Button decreaseButton = new Button("-", e -> {
                int currentValue = quantityField.getValue() != null ? quantityField.getValue() : 0;
                if (currentValue > 1) { // Ensure the quantity does not go below 1
                    quantityField.setValue(currentValue - 1);
                    updateQuantity(item, currentValue - 1);
                }
            });
            decreaseButton.addClassName("button");

            layout.add(increaseButton, quantityField, decreaseButton);
            layout.setAlignItems(Alignment.CENTER);
            layout.setSpacing(true);

            Binder<ProductDTO> binder = new Binder<>(ProductDTO.class);
            binder.forField(quantityField)
                    .bind(ProductDTO::getQuantity, ProductDTO::setQuantity);

            return layout;
        })).setHeader("Quantity");
        grid.addComponentColumn(item -> {
            Button removeButton = new Button("Remove", e -> navigateToProductRemoving(item.getProductID(), item.getStoreID()));
            removeButton.addClassName("button");
            return removeButton;
        }).setHeader("Actions");

        // Set text in the header row cell instead of component
        grid.prependHeaderRow().getCell(grid.getColumns().get(0)).setText("Store Name: " + storeName);

        return grid;
    }

    private void navigateToProductRemoving(Integer productID, Integer storeID) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialogLayout.add("Are you sure you want to remove this product?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.removeProductFromCart(productID, storeID, presenter.getUsername());
            presenter.getShoppingCartContents(); // Refresh cart after removal
            confirmationDialog.close();
            showSuccess("Product removed successfully");
        });
        confirmButton.addClassName("yes_button");

        Button cancelButton = new Button("No", e -> confirmationDialog.close());
        cancelButton.addClassName("no_button");

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);
        buttonLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END); // Align buttons to the right

        dialogLayout.add(buttonLayout);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    private void updateQuantity(ProductDTO item, int newQuantity) {
        item.setQuantity(newQuantity);
        presenter.updateProductQuantityInCart(
                item.getProductID(), item.getStoreID(), newQuantity, presenter.getUsername());
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
