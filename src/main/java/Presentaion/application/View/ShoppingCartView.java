package Presentaion.application.View;

import Domain.Store.Inventory.ProductDTO;
import Presentaion.application.Presenter.ShoppingCartPresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PageTitle("Shopping Cart")
@Route(value = "shopping-cart", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class ShoppingCartView extends VerticalLayout {

    private final ShoppingCartPresenter presenter;
    private final Grid<ProductDTO> cartGrid;


    public ShoppingCartView(ShoppingCartPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("shopping-cart-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        cartGrid = new Grid<>(ProductDTO.class);
        cartGrid.removeColumnByKey("storeID");
        cartGrid.removeColumnByKey("productID");
        cartGrid.removeColumnByKey("quantity"); // Remove the automatic columns

        cartGrid.addColumn(new ComponentRenderer<>(item -> {
            ComboBox<Integer> quantityComboBox = new ComboBox<>();
            quantityComboBox.setItems(IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList()));
            quantityComboBox.setValue(item.getQuantity()); // Assuming ProductDTO has a getQuantity method

            Binder<ProductDTO> binder = new Binder<>(ProductDTO.class);
            binder.forField(quantityComboBox)
                    .bind(ProductDTO::getQuantity, ProductDTO::setQuantity); // Assuming ProductDTO has a setQuantity method

            quantityComboBox.addValueChangeListener(e -> {
                binder.writeBeanIfValid(item);
                presenter.updateProductQuantityInCart(item.getProductID().toString(), item.getStoreID(), item.getQuantity(), presenter.getUsername());
                cartGrid.getDataProvider().refreshItem(item);
            });

            return quantityComboBox;
        })).setHeader("Quantity");

        cartGrid.addComponentColumn(item -> new Button("Remove", e -> {
            presenter.removeProductFromCart(item.getProductID().toString(), item.getStoreID(),presenter.getUsername());
            cartGrid.getDataProvider().refreshAll();
        })).setHeader("Actions");

        Button checkoutButton = new Button("Checkout", e -> presenter.checkout());
        Button clearCartButton = new Button("Clear Cart", e -> presenter.clearCart());

        HorizontalLayout buttonsLayout = new HorizontalLayout(checkoutButton, clearCartButton);
        buttonsLayout.setAlignItems(Alignment.CENTER);



        add(cartGrid, buttonsLayout);
        loadCartItems();
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



    public Grid<ProductDTO> getCartGrid() {
        return cartGrid;
    }
}