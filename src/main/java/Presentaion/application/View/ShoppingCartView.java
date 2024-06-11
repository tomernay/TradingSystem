package Presentaion.application.View;

import Presentaion.application.Presenter.ShoppingCartPresenter;
import Domain.Users.Subscriber.Cart.Basket;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@PageTitle("Shopping Cart")
@Route(value = "shopping-cart", layout = MainLayoutView.class)
@StyleSheet("context://shopping-cart-view-styles.css")
public class ShoppingCartView extends VerticalLayout {

    private final ShoppingCartPresenter presenter;
    private final Grid<Basket> cartGrid;

    public ShoppingCartView(ShoppingCartPresenter presenter) {
        this.presenter = presenter;
        this.presenter.attachView(this);
        addClassName("shopping-cart-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Shopping Cart");
        title.getStyle().set("font-size", "3em");

        cartGrid = new Grid<>(Basket.class);
        // Adjust the columns to match the Basket class properties
        cartGrid.setColumns("storeID", "productsQuantityMap");
        cartGrid.getColumnByKey("storeID").setHeader("Store ID");
        cartGrid.getColumnByKey("productsQuantityMap").setHeader("Products Quantity Map");

        Button checkoutButton = new Button("Checkout", e -> presenter.checkout());
        Button clearCartButton = new Button("Clear Cart", e -> presenter.clearCart());

        HorizontalLayout buttonsLayout = new HorizontalLayout(checkoutButton, clearCartButton);
        buttonsLayout.setAlignItems(Alignment.CENTER);

        add(title, cartGrid, buttonsLayout);
        loadCartItems();
    }

    public void loadCartItems() {
        presenter.getShoppingCartContents();
    }

    public void showCartItems(Map<String, Map<String, Integer>> products) {
        List<Basket> baskets = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : products.entrySet()) {
            Basket basket = new Basket(entry.getKey());
            basket.setProductsQuantityMap(entry.getValue());
            baskets.add(basket);
        }
        cartGrid.setItems(baskets);
    }

    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void showError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }
}