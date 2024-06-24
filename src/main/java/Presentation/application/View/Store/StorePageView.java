package Presentation.application.View.Store;

import Domain.Store.Inventory.ProductDTO;
import Presentation.application.Presenter.Store.StorePagePresenter;
import Presentation.application.View.LoginView;
import Presentation.application.View.MainLayoutView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;

@PageTitle("")
@Route(value = "store-page/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class StorePageView extends AppLayout implements BeforeEnterObserver {

    private StorePagePresenter presenter;
    private VerticalLayout mainContent;
    private Integer storeId;
    private String storeName;
    private ArrayList<ProductDTO> products;

    public StorePageView(StorePagePresenter presenter) {
        addClassName("store-page-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
//        UI.getCurrent().getPage().executeJs(
//                "document.body.addEventListener('click', function() {" +
//                        "    $0.$server.handleUserAction();" +
//                        "});",
//                getElement()
//        );

        //mainContent.add(new Span("Store Page"));
    }

    //change the search method to search in the store
    public void search(String searchTerm, int storeId) {
        presenter.search(searchTerm, storeId);
    }

    public void viewAllProducts(){
        //display all products from the store

    }



    //display stores products
    public void displayStoresProducts() {
        //display products
        ArrayList<ProductDTO> products = presenter.getStoresProducts(storeId);
//        VerticalLayout dialogLayout = new VerticalLayout();
        VerticalLayout nameLayout = new VerticalLayout();
        VerticalLayout priceLayout = new VerticalLayout();
        VerticalLayout quantityLayout = new VerticalLayout();
        VerticalLayout buttonLayout = new VerticalLayout();

        HorizontalLayout productDetailsLayout = new HorizontalLayout();
        productDetailsLayout.setWidthFull();
        productDetailsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        productDetailsLayout.add(nameLayout, priceLayout, quantityLayout, buttonLayout);
        productDetailsLayout.setSpacing(true);


        for (ProductDTO product : products) {
//            Div productDiv = new Div();

            // Create a horizontal layout for product details (name, price, quantity, buttons)


            //add 4 vertical layouts for each column

            Span productNameSpan = new Span(product.getProductName());
            productNameSpan.getElement().getStyle().set("margin-right", "1em"); // Add margin between name and price

            Span productPriceSpan = new Span("$" + product.getPrice()); // Assuming price is stored in ProductDTO
            productPriceSpan.getElement().getStyle().set("color", "gray");

            // Quantity input field
            IntegerField quantityField = new IntegerField();
            quantityField.setMin(1); // Minimum quantity allowed
            quantityField.setWidth("2em"); // Set a fixed width for better alignment
            quantityField.setValue(1); // Default quantity


            // Create buttons for increasing and decreasing quantity
            Button increaseButton = new Button("+");
            increaseButton.getElement().getStyle().set("color", "black");
            increaseButton.getElement().getStyle().set("background-color", "transparent");
            increaseButton.addClickListener(e -> {
                int currentValue = quantityField.getValue();
                quantityField.setValue(currentValue + 1);
            });

            Button decreaseButton = new Button("-");
            decreaseButton.getElement().getStyle().set("color", "black");
            decreaseButton.getElement().getStyle().set("background-color", "transparent");
            decreaseButton.addClickListener(e -> {
                int currentValue = quantityField.getValue();
                if (currentValue > 1) {
                    quantityField.setValue(currentValue - 1);
                }
            });

            // Create the add to cart button with a + icon and transparent background
            Button addToCartButton = new Button("Add to Cart");
            addToCartButton.getElement().getStyle().set("background-color", "lightgray");
            addToCartButton.addClickListener(e -> addToCart(product, quantityField.getValue()));
            addToCartButton.getElement().getStyle().set("color", "black");
            addToCartButton.getElement().getStyle().set("margin-left", "auto"); // Align the button to the right

            HorizontalLayout quantityIncDec = new HorizontalLayout();
            quantityIncDec.add(increaseButton, quantityField, decreaseButton);
            // Add components to productDetailsLayout
            nameLayout.add(productNameSpan);
            priceLayout.add(productPriceSpan);
            quantityLayout.add(quantityIncDec);
            buttonLayout.add(addToCartButton);
            // Add productDetailsLayout to productDiv
//            productDiv.add(productDetailsLayout);

            // Add productDiv to dialogLayout
//            dialogLayout.add(productDiv);
        }
        // Create a container for the product details layout
        Div container = new Div();
        container.getStyle().set("display", "flex");
        container.getStyle().set("flex-direction", "column");
        container.getStyle().set("align-items", "center");
        container.getStyle().set("justify-content", "center");
        container.getStyle().set("padding", "20px");
        container.getStyle().set("background-color", "#E6DCD3"); // Optional: set a background color
        container.getStyle().set("border", "2px solid #B4A79E"); // Optional: set a border
        container.getStyle().set("border-radius", "8px"); // Optional: set border radius for rounded corners
        container.getStyle().set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)"); // Optional: add a subtle box shadow
        container.setWidth("90%"); // Adjust width to content
        container.setHeight("auto"); // Adjust height to content
        //margins left and right auto to center the container
        container.getStyle().set("margin-left", "auto");
        container.getStyle().set("margin-right", "auto");


        // Add the product details layout to the container
        container.add(productDetailsLayout);

        // Add the container to the main content
        mainContent.add(container);

    }

    private void addToCart(ProductDTO product, Integer quantity) {
        presenter.addToCart(product, quantity);
    }




    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
        String id = event.getRouteParameters().get("storeId").orElse("");
        storeId = Integer.parseInt(id);
        storeName = presenter.getStoreName(storeId);
        //add store name to the page
        Span storeNameSpan = new Span(storeName);
        storeNameSpan.getElement().getStyle().set("font-size", "2em");
        storeNameSpan.getElement().getStyle().set("font-weight", "bold");
        storeNameSpan.getElement().getStyle().set("color", "#3F352C");
//        mainContent.add(storeNameSpan);
        //display store products
        mainContent = new VerticalLayout();
        setContent(mainContent);
        displayStoresProducts();

//        presenter.setStoreId(Integer.parseInt(storeId));
//        presenter.getStoresProducts(Integer.parseInt(storeId));
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
        // Implement your token validation logic here
        // This could involve checking the token against a database or decoding a JWT token
        return token != null && !token.isEmpty();
    }
}



