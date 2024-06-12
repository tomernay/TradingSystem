package Presentaion.application.View;

import Domain.Store.Inventory.ProductDTO;
import Presentaion.application.Presenter.ShoppingCartPresenter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        cartGrid.addComponentColumn(item -> new Button("Remove", e -> {
            presenter.removeProductFromCart(item.getProductID().toString(), item.getStoreID(), item.getName());
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