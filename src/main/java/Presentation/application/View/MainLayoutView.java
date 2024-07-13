package Presentation.application.View;

import Presentation.application.CookiesHandler;
import Presentation.application.Presenter.MainLayoutPresenter;

import Presentation.application.View.Store.StoreManagementView;
import Presentation.application.View.Store.StorePageView;


import Presentation.application.View.UtilitiesView.Broadcaster;
import Presentation.application.View.UtilitiesView.WSClient;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import Domain.Store.Inventory.ProductDTO;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

/**
 * The main view is a top-level placeholder for other views.
 */
//@CssImport(value= "./styles/navbarStyles.css", themeFor = "vaadin-app-layout")
@Route("")
@PageTitle("Main")
@StyleSheet("context://styles.css")
public class MainLayoutView extends AppLayout implements BeforeEnterObserver {

    private final MainLayoutPresenter presenter;

    private H1 viewTitle;
    private Queue<Message> sub;
    private VerticalLayout mainContent;
    private Span notificationCountSpan;


    public MainLayoutView(MainLayoutPresenter presenter) {
        addClassName("page-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
        mainContent = new VerticalLayout();
        setContent(mainContent);
        sub = new LinkedBlockingQueue<>();
//        setPrimarySection(Section.DRAWER);
        addHeaderContent();
//         addDrawerContent();
        addHomeButton();
        addUserButton();
        welcomeText();
        addSearchBar();
        searchByCategory();
        addNotificationButton();
        shoppingCart();
        addStoresToMain();
        UI currentUI = UI.getCurrent();
        int unreadCount = presenter.getUnreadMessagesCount(); // Fetch this from the server or database
        if (unreadCount > 0) {
            notificationCountSpan.setText(String.valueOf(unreadCount));
            notificationCountSpan.setVisible(true);
        }

        String user=CookiesHandler.getUsernameFromCookies(getRequest());
        Broadcaster.register(message -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                Notification.show(message);

            }));
        }, user);

        UI.getCurrent().getPage().executeJs(
                "document.body.addEventListener('click', function() {" +
                        "    $0.$server.handleUserAction();" +
                        "});",
                getElement()
        );

        getElement().executeJs(
                "this.shadowRoot.querySelector('[part=\"navbar\"]').style.backgroundColor = '#E6DCD3';"
        );

    }

    @ClientCallable
    public void handleUserAction() {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            Notification.show("Token has timed out! Navigating you to login page...");
            UI.getCurrent().navigate(LoginView.class);
        }
    }


    public ArrayList<String> getAllCategories() {
        // Fetch all categories from the server or database
        return presenter.getAllCategories();
    }


    public void searchByCategory() {
        //create a button that opens a list of all existing categories that are clickable and will display the products of that category
        //add a button to the navbar
        //when clicked open a dialog with the categories

        //use context menu to display the categories
        Button categoryButton = new Button("search by category");
//        //minimize the text size
        categoryButton.getElement().getStyle().set("font-size", "15px");
//        //allow texr to be displayed in multiple lines
        categoryButton.addClassName("multiline-button");

        ContextMenu categoryMenu = new ContextMenu(categoryButton);
        categoryMenu.setOpenOnClick(true);
        //set icon to an arrow facing down
        categoryButton.setIcon(new Icon(VaadinIcon.ANGLE_DOWN));
        categoryButton.getElement().getStyle().set("color", "black");
        categoryButton.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search button
        //fetch all categories from the server
        ArrayList<String> categories = presenter.getAllCategories();
        //add a button for each category

        for (String cat : categories) {
            MenuItem categoryItem = categoryMenu.addItem(cat, e -> {
//                //search for products by category
                ArrayList<ProductDTO> products = presenter.searchProductsByCategory(cat).getData();
//                ArrayList<ProductDTO> products = productsRes.getData();
//                //display the products
                displaySearchResults(products);
            });
            //add the category item to the menu
            //categoryMenu.addItem(categoryItem);
        }
        addToNavbar(categoryButton);
    }




    private void addUserButton() {

        Button userButton = new Button("");
        ContextMenu dropdownMenu = new ContextMenu(userButton);
        dropdownMenu.addClassName("custom-context-menu");
        dropdownMenu.setOpenOnClick(true);
        //icon
        userButton.setIcon(new Icon(VaadinIcon.USER));

        String username = presenter.getUserName();
        if(username.equals("u1") && !presenter.isSuspended(username)) {
            MenuItem admin = dropdownMenu.addItem("Admin actions", e -> {
                UI.getCurrent().navigate(AdminView.class);
            });
        }


        if(!username.contains("Guest")) {
            MenuItem myStores = dropdownMenu.addItem("My Stores", e -> myStoresDialog());
            MenuItem personalSettings = dropdownMenu.addItem("Personal Settings", e -> openSettings());
        }

        //if user is guest add register button
        if(username.contains("Guest")) {
            MenuItem register = dropdownMenu.addItem("Register", e -> navigateToRegister());
        }

        MenuItem logout = dropdownMenu.addItem("Logout", e -> {
            presenter.logout();
        });

        userButton.getElement().getStyle().set("color", "black");
        userButton.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search button
        addToNavbar(userButton);
    }

    private boolean hasRole(String username){
        //check if the user is a store manager / owner/ creator
        return presenter.isManager(username) || presenter.isOwner(username) || presenter.isCreator(username);
    }

    private List<String> getUsersStores(String username){
        return presenter.getUsersStores();
    }

    private List<String> getAllStores(){
        return presenter.getAllStores();
    }



    private void navigateToRegister() {
        getUI().ifPresent(ui -> ui.navigate("register"));
    }



    private void myStoresDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setWidth("500px");
        dialog.setHeight("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Choose a store:"));

        List<String> stores = getUsersStores(presenter.getUserName());

        VerticalLayout activeStoresLayout = new VerticalLayout();
        activeStoresLayout.add(new H4("Active Stores:"));

        VerticalLayout deactivatedStoresLayout = new VerticalLayout();
        deactivatedStoresLayout.add(new H4("Deactivated Stores:"));
        String username = presenter.getUserName();

        for (String store : stores) {
            final Integer storeId = getStoreIdByName(store);  // Get the store ID once per iteration

            Button storeButton = new Button(store, e -> {
                if(!presenter.isSuspended(username)) {
                    // Navigate to the store management page (assuming StoreManagementView is the correct view
                    RouteParameters routeParameters = new RouteParameters("storeId", storeId.toString());
                    UI.getCurrent().navigate(StoreManagementView.class, routeParameters);
                    dialog.close();
                }
            });
            storeButton.addClassName("button");

            if (isStoreActive(storeId)) {
                activeStoresLayout.add(storeButton);
            } else {
                deactivatedStoresLayout.add(storeButton);
            }
        }
        if(!presenter.isSuspended(username)) {
            Button openNewStore = new Button("Open a new store", e -> openNewStoreDialog());
            openNewStore.addClassName("button");
            openNewStore.getElement().getStyle().set("position", "absolute");
            openNewStore.getElement().getStyle().set("bottom", "0");
            openNewStore.getElement().getStyle().set("left", "0");
            openNewStore.getElement().getStyle().set("right", "0");
            dialogLayout.add(openNewStore);
        }

        Button closeDialogButton = addCloseButton(dialog);

        dialogLayout.add(activeStoresLayout, deactivatedStoresLayout, closeDialogButton);
        dialog.add(dialogLayout);
        dialog.open();
    }



    // Assume these methods are part of your presenter or some utility class
    private boolean isStoreActive(Integer storeId) {
        // Implement logic to determine if the store is active
        return presenter.isStoreActive(storeId);
    }

    private Integer getStoreIdByName(String storeName) {
        // Implement this method to fetch the storeId based on the store name
        // This can be a call to your service layer
        return presenter.getStoreIdByName(storeName);
    }

    WSClient wsClient;

    private void openNewStoreDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("300px");
        dialog.setHeight("250px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");

        dialogLayout.add(new Span("Open a new store:"));

        TextField storeName = new TextField("Store Name");
        //place in the center
        storeName.getElement().getStyle().set("margin", "0 auto");
        String user=CookiesHandler.getUsernameFromCookies(getRequest());

        try {
            UI ui=UI.getCurrent();
            wsClient=WSClient.getClient(ui,user);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        TextField storeDescription = new TextField("Store Description");
        Button openStore = new Button("Open Store", e -> {
            presenter.addStore(storeName.getValue(), storeName);

            try {
                wsClient.sendMessage(user+":open store");
            } catch (InterruptedException eX) {
                eX.printStackTrace();
            } catch (ExecutionException eX) {
                eX.printStackTrace();
            }

            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");

            dialog.close();
        });
        openStore.addClassName("button");
        //position save button at the center
        openStore.getElement().getStyle().set("position", "absolute");
        openStore.getElement().getStyle().set("bottom", "0");
        openStore.getElement().getStyle().set("left", "0");
        openStore.getElement().getStyle().set("right", "0");

        //add a close button
        Button closeDialogButton = addCloseButton(dialog);
        dialogLayout.add(closeDialogButton);

        dialogLayout.add(storeName, openStore);
        dialog.add(dialogLayout);
        dialog.open();
    }

    public void addHomeButton() {
        // Navigate to the shopping cart page
        Button homeButton = new Button("", e -> {
            UI.getCurrent().navigate(MainLayoutView.class);
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");
        });
        homeButton.getElement().getStyle().set("color", "black");
        homeButton.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search button
        //icon
        homeButton.setIcon(new Icon(VaadinIcon.HOME));
        homeButton.getElement().getStyle().set("margin-left", "10px"); // Add a margin to the right side of the search button

        addToNavbar(homeButton);
    }

    private void addStoresToMain(){
        //get all stores and for each store add a button to the main content area
        List<String> stores = getAllStores();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true); // Optional: Set spacing between components

        for (int i = 0; i < stores.size(); i += 4) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull(); // Ensure row takes full width
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);


            // Counter to keep track of buttons added
            for (int j = 0; j < 4 && (i + j) < stores.size(); j++) {
                String store = stores.get(i + j);
                final Integer storeId = getStoreIdByName(store);
                Button storeButton = new Button(store, e -> {
                    RouteParameters routeParameters = new RouteParameters("storeId", storeId.toString());
                    UI.getCurrent().navigate(StorePageView.class, routeParameters);
                });
                storeButton.addClassName("button");
               //make button take a relative width from 4 buttons in a row
                storeButton.setWidth("25%");
                //make the height of the button relative to the width
                storeButton.setHeight("100px");

                row.add(storeButton);
            }

            // Add the row to the vertical layout
            verticalLayout.add(row);
        }

        // Add the vertical layout to the main content area (assuming mainContent is a VerticalLayout or similar)
        mainContent.add(verticalLayout);


    }

    private void addCategoriesButton() {
        //add a button for each category

        //add a button for each category
        Button category1 = new Button("Food", e -> {
            // Navigate to the shopping cart page
        });

        //make the button round

//        category1.getElement().getStyle().set("border-radius", "50px");
        Button category2 = new Button("Electronics", e -> {
            // Navigate to the shopping cart page
        });
        Button category3 = new Button("General", e -> {
            // Navigate to the shopping cart page
        });
        Image books = new Image("static/books.jpg", "books");
        books.setWidth("50px");
        books.setHeight("50px");
//        addClassName("books-button");
        Button category4 = new Button(books, e -> {

            // Navigate to the shopping cart page
        });
        Button category5 = new Button("Toys", e -> {
            // Navigate to the shopping cart page
        });

        category1.addClassName("round-button");
        category2.addClassName("round-button");
        category3.addClassName("round-button");
        category4.addClassName("round-button");
        category5.addClassName("round-button");

        category1.getElement().getStyle().set("color","white");
        category2.getElement().getStyle().set("color", "white");
        category3.getElement().getStyle().set("color", "white");
        category4.getElement().getStyle().set("color", "white");
        category5.getElement().getStyle().set("color", "white");


        //the text of the button at the bottom


        HorizontalLayout categoriesLayout = new HorizontalLayout(category1, category2, category3, category4, category5);
        categoriesLayout.addClassName("center-layout");
        categoriesLayout.setSpacing(true);
        categoriesLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        categoriesLayout.setWidthFull();

        VerticalLayout mainLayout = new VerticalLayout(categoriesLayout);
        mainLayout.setSizeFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Add the main layout to the main content area
//        setContent(mainLayout);
        mainContent.add(mainLayout);
        //center the buttons
        categoriesLayout.setAlignItems(FlexComponent.Alignment.CENTER);

//        categoriesLayout.add(category1, category2, category3, category4, category6);

//        addToNavbar(categoriesLayout);
    }

  WSClient webSocketClient;
    private void addMessageButton() {
        Button addMessageButton = new Button("Add Message");
        UI ui = UI.getCurrent();
        String user = CookiesHandler.getUsernameFromCookies(getRequest());

        try {
            webSocketClient = WSClient.getClient(ui, user);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Notification.show("Error initializing WebSocket client");
        }

        addMessageButton.addClickListener(e -> {
            try {
                webSocketClient.sendMessage("user:message");
                webSocketClient.sendMessage(user + ":message2");
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
                Notification.show("Error sending WebSocket message");
            }
        });

        // Listen for WebSocket messages and update the UI
        webSocketClient.setOnMessageListener(message -> {
            String[] m = message.split(":");

            if (m[0].equals(user)) {
                ui.access(() -> {
                    Notification.show("New message: " + message);
                    incrementNotificationCount();
                });
            }
        });



        mainContent.add(addMessageButton);
    }

    private void incrementNotificationCount() {
        int count = Integer.parseInt(notificationCountSpan.getText().isEmpty() ? "0" : notificationCountSpan.getText());
        count++;
        notificationCountSpan.setText(String.valueOf(count));
        notificationCountSpan.setVisible(true);
    }

    public void decrementNotificationCount() {
        int count = Integer.parseInt(notificationCountSpan.getText().isEmpty() ? "0" : notificationCountSpan.getText());
        if (count > 0) {
            count--;
            notificationCountSpan.setText(String.valueOf(count));
            if (count == 0) {
                notificationCountSpan.setVisible(false);
            }
        }
    }

    private void clearNotificationCount() {
        notificationCountSpan.setText("");
        notificationCountSpan.setVisible(false);
    }

    private void addHeaderContent() {
//        DrawerToggle toggle = new DrawerToggle();
//        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        //  viewTitle.addClassNames(Lumo.FontStyle.LARGE, Lumo.Margin.NONE);
        Button b=new Button("check message",e->sub.add(new NormalMessage("Message has been added")));
//        addToNavbar(viewTitle);
    }


    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
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



//    private void displaySearchResults(ArrayList<ProductDTO> results) {
//        Dialog dialog = new Dialog();
//        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
//        dialog.setWidth("800px");
//        dialog.setHeight("700px");
//
//        // Create and configure the filter button
//        Button filterButton = new Button("");
//        filterButton.setIcon(new Icon(VaadinIcon.FILTER));
//        filterButton.getElement().getStyle().set("color", "black");
//        filterButton.getElement().getStyle().set("background-color", "transparent");
//        filterButton.addClickListener(event -> openFilterDialog(results));
//        filterButton.getElement().getStyle().set("margin-left", "auto"); // Align the filter button to the right
//
//        // Create a horizontal layout for the title and filter button
//        HorizontalLayout titleAndFilterLayout = new HorizontalLayout();
//        titleAndFilterLayout.setWidthFull();
//        titleAndFilterLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//
////        HorizontalLayout productDetailsLayout = new HorizontalLayout();
//        VerticalLayout nameLayout = new VerticalLayout();
//        VerticalLayout priceLayout = new VerticalLayout();
//        VerticalLayout quantityLayout = new VerticalLayout();
//        VerticalLayout buttonLayout = new VerticalLayout();
//        nameLayout.setSpacing(false);
//        priceLayout.setSpacing(false);
//        quantityLayout.setSpacing(false);
//        buttonLayout.setSpacing(false);
//
//
////        productDetailsLayout.add(nameLayout, priceLayout, quantityLayout, buttonLayout);
//
//
//        // Create the "Search Results" span title
//        Span searchResultsSpan = new Span("Search Results");
//        searchResultsSpan.getElement().getStyle().set("font-size", "20px"); // Set the font size of the title
//
//        // Add the title and filter button to the titleAndFilterLayout
//        titleAndFilterLayout.add(searchResultsSpan, filterButton);
//
//        VerticalLayout dialogLayout = new VerticalLayout();
//        dialogLayout.add(titleAndFilterLayout);
//
//        if(results.isEmpty()){
//            dialogLayout.add(new Span("No results found"));
//        }
//
//        for (ProductDTO product : results) {
////            Div productDiv = new Div();
//            // Create a horizontal layout for each product's details
//            HorizontalLayout productDetailsLayout = new HorizontalLayout();
//            productDetailsLayout.setWidthFull();
//            productDetailsLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center align items vertically
//
//
//
//            // Product name span
//            Span productNameSpan = new Span(product.getProductName());
////            productNameSpan.getElement().getStyle().set("margin-right", "1em"); // Add margin between name and price
//            //set the font size of the product name
//            productNameSpan.getElement().getStyle().set("font-size", "17px");
//            //set height of the product name
//            //top margin
//            productNameSpan.getElement().getStyle().set("margin-top", "10px");
//            productNameSpan.getElement().getStyle().set("margin-bottom", "10px");
//
//            // Product price span
//            Span productPriceSpan = new Span("$" + product.getPrice()); // Assuming price is stored in ProductDTO
//            productPriceSpan.getElement().getStyle().set("color", "gray");
//            productPriceSpan.getElement().getStyle().set("font-size", "17px");
//            productPriceSpan.getElement().getStyle().set("margin-top", "10px");
//            productPriceSpan.getElement().getStyle().set("margin-bottom", "10px");
////            productPriceSpan.getElement().getStyle().set("height", "43px");
//
//            // Quantity input field
//            IntegerField quantityField = new IntegerField();
//            quantityField.setMin(1); // Minimum quantity allowed
//            quantityField.setWidth("2em"); // Set a fixed width for better alignment
//            quantityField.setValue(1); // Default quantity
//
//            // Create buttons for increasing and decreasing quantity
//            Button increaseButton = new Button("+");
//            increaseButton.getElement().getStyle().set("color", "black");
//            increaseButton.getElement().getStyle().set("background-color", "transparent");
//            increaseButton.addClickListener(e -> {
//                int currentValue = quantityField.getValue();
//                quantityField.setValue(currentValue + 1);
//            });
//
//            Button decreaseButton = new Button("-");
//            decreaseButton.getElement().getStyle().set("color", "black");
//            decreaseButton.getElement().getStyle().set("background-color", "transparent");
//            decreaseButton.addClickListener(e -> {
//                int currentValue = quantityField.getValue();
//                if (currentValue > 1) {
//                    quantityField.setValue(currentValue - 1);
//                }
//            });
//
//            // Create the add to cart button with a + icon and transparent background
//            Button addToCartButton = new Button("Add to Cart");
//            addToCartButton.addClassName("button");
//            addToCartButton.addClickListener(e -> addToCart(product, quantityField.getValue()));
//            addToCartButton.getElement().getStyle().set("margin-left", "auto"); // Align the button to the right
//
//            HorizontalLayout quantityIncDec = new HorizontalLayout();
//            quantityIncDec.add(increaseButton, quantityField, decreaseButton);
//
//            nameLayout.add(productNameSpan);
//            priceLayout.add(productPriceSpan);
//            quantityLayout.add(quantityIncDec);
//            buttonLayout.add(addToCartButton);
//
//            productDetailsLayout.add(nameLayout, priceLayout, quantityLayout, buttonLayout);
//
//            // Add components to productDetailsLayout
////            productDetailsLayout.add(productNameSpan, productPriceSpan, createQuantityLayout(quantityField, decreaseButton, increaseButton, addToCartButton));
//
//            // Add productDetailsLayout to productDiv
////            productDiv.add(productDetailsLayout);
//
//            // Add productDiv to dialogLayout
//            dialogLayout.add(productDetailsLayout);
//        }
//
//        // Add close button to dialogLayout
//        Button closeDialogButton = addCloseButton(dialog);
//        dialogLayout.add(closeDialogButton);
//
//        // Add dialogLayout to dialog
//        dialog.add(dialogLayout);
//
//        // Open the dialog
//        dialog.open();
//
//    }


    private void displaySearchResults(ArrayList<ProductDTO> results) {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setWidth("800px");
        dialog.setHeight("700px");

        // Create and configure the filter button
        Button filterButton = new Button("");
        filterButton.setIcon(new Icon(VaadinIcon.FILTER));
        filterButton.getElement().getStyle().set("color", "black");
        filterButton.getElement().getStyle().set("background-color", "transparent");
        filterButton.addClickListener(event -> openFilterDialog(results));
        filterButton.getElement().getStyle().set("margin-left", "auto"); // Align the filter button to the right

        // Create a horizontal layout for the title and filter button
        HorizontalLayout titleAndFilterLayout = new HorizontalLayout();
        titleAndFilterLayout.setWidthFull();
        titleAndFilterLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Create the "Search Results" span title
        Span searchResultsSpan = new Span("Search Results");
        searchResultsSpan.getElement().getStyle().set("font-size", "20px"); // Set the font size of the title

        // Add the title and filter button to the titleAndFilterLayout
        titleAndFilterLayout.add(searchResultsSpan, filterButton);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(titleAndFilterLayout);

        if(results.isEmpty()){
            dialogLayout.add(new Span("No results found"));
        } else {
            Grid<ProductDTO> productGrid = new Grid<>(ProductDTO.class, false);

            productGrid.addClassName("custom-grid");
            productGrid.addColumn(ProductDTO::getProductName).setHeader("Product Name").setAutoWidth(true);

            productGrid.addColumn(product -> "$" + product.getPrice()).setHeader("Price").setAutoWidth(true);

            if(!presenter.isSuspended(presenter.getUserName())) {
            productGrid.addComponentColumn(product -> {
                IntegerField quantityField = new IntegerField();
                quantityField.setMin(1); // Minimum quantity allowed
                quantityField.setWidth("2em"); // Set a fixed width for better alignment
                quantityField.setValue(1); // Default quantity

                Button increaseButton = new Button("+");
                increaseButton.getElement().getStyle().set("color", "black");
                increaseButton.getElement().getStyle().set("background-color", "transparent");
                increaseButton.setWidth("10px");
                increaseButton.addClickListener(e -> {
                    int currentValue = quantityField.getValue();
                    quantityField.setValue(currentValue + 1);
                });

                Button decreaseButton = new Button("-");
                decreaseButton.getElement().getStyle().set("color", "black");
                decreaseButton.getElement().getStyle().set("background-color", "transparent");
                //narrow the button
                decreaseButton.setWidth("10px");
                decreaseButton.addClickListener(e -> {
                    int currentValue = quantityField.getValue();
                    if (currentValue > 1) {
                        quantityField.setValue(currentValue - 1);
                    }
                });

                HorizontalLayout quantityLayout = new HorizontalLayout(decreaseButton, quantityField, increaseButton);
                return quantityLayout;
            }).setHeader("Quantity").setAutoWidth(true);


                productGrid.addComponentColumn(product -> {
                    Button addToCartButton = new Button("Add to Cart");
                    addToCartButton.addClassName("button");
                    addToCartButton.addClickListener(e -> addToCart(product, 1)); // default quantity to 1
                    return addToCartButton;
                }).setHeader("").setAutoWidth(true);
            }

            productGrid.setItems(results);
            dialogLayout.add(productGrid);
        }

        // Add close button to dialogLayout
        Button closeDialogButton = addCloseButton(dialog);
        dialogLayout.add(closeDialogButton);

        // Add dialogLayout to dialog
        dialog.add(dialogLayout);

        // Open the dialog
        dialog.open();
    }


    // Method to create a horizontal layout for quantity controls and add to cart button
    private HorizontalLayout createQuantityLayout(IntegerField quantityField, Button decreaseButton, Button increaseButton, Button addToCartButton) {
        HorizontalLayout quantityLayout = new HorizontalLayout();
        quantityLayout.setWidthFull();
        quantityLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        quantityLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        quantityLayout.add(decreaseButton, quantityField, increaseButton, addToCartButton);
        return quantityLayout;
    }



    public void addSearchBar() {
        // Add search bar to the header
        ComboBox<String> searchBar = new ComboBox<>();
        searchBar.setPlaceholder("Search for anything");
        searchBar.getElement().getStyle().setColor("black");
        searchBar.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search bar
        searchBar.setWidthFull(); // Set the width of the search bar to 100%

        searchBar.setItemLabelGenerator(item -> item);
        searchBar.setClearButtonVisible(true);
        searchBar.setAllowCustomValue(true);

        // Fetch and display search results as user types in a list opening beneath the search bar

        searchBar.addCustomValueSetListener(event -> {
            String searchTerm = event.getDetail();
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                ArrayList<ProductDTO> results = search(searchTerm);
                displaySearchResults(results);
            }
        });



        addToNavbar(searchBar);
    }

    public ArrayList<ProductDTO> search(String search) {
        return presenter.searchProducts(search);
    }




    private void openFilterDialog(ArrayList<ProductDTO> results) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeight("450px");
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Filter Options"));
        dialogLayout.getElement().getStyle().set("font-size", "20px"); // Set the font size of the title

        // add filter for prices range - min and max in an interactive line to drag min and max from 2 edges
        TextField minPrice = new TextField("Min Price");
        TextField maxPrice = new TextField("Max Price");

        Button applyFilterButton = new Button("Apply Filters", e -> {

            // Apply the selected filters and update the search results accordingly
           ArrayList<ProductDTO> filteredResults = filterPriceRange(results, Double.parseDouble(minPrice.getValue()), Double.parseDouble(maxPrice.getValue()));
            displaySearchResults(filteredResults);
            dialog.close();
        });
        applyFilterButton.getElement().getStyle().set("color", "black");
        applyFilterButton.getElement().getStyle().set("background-color", "transparent");
        dialogLayout.add(minPrice,maxPrice, applyFilterButton);
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, applyFilterButton);

        Button closeDialogButton = addCloseButton(dialog);
        dialogLayout.add(closeDialogButton);

        dialog.add(dialogLayout);
        dialog.open();
    }



    /**
     * Filters and sorts products based on the given price range.
     *
     //     * @param productsResponse the response containing a list of ProductDTO
     * @param minPrice the minimum price for the range filter (exclusive)
     * @param maxPrice the maximum price for the range filter (exclusive)
     * @return a Response containing the filtered and sorted list of ProductDTO
     */
       public static ArrayList<ProductDTO> filterPriceRange(ArrayList<ProductDTO> results, double minPrice, double maxPrice) {
        ArrayList<ProductDTO> filteredResults = new ArrayList<>();
        for (ProductDTO product : results) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                filteredResults.add(product);
            }
        }
    return filteredResults;
//}
    }




    private void addToCart(ProductDTO product, Integer quantity) {
        try {
            presenter.addToCart(product, quantity);
            Notification.show("Product added to cart successfully!");
        }
        catch (Exception e) {
            Notification.show("Error adding product to cart: " + e.getMessage());
        }
    }


    private void openSettings(){
        //open a dialog with two buttons - change username and change password
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setWidth("400px");
        dialog.setHeight("250px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Personal Settings"));
        //enlarge the text
        dialogLayout.getElement().getStyle().set("font-size", "20px");
        Button changeUsername = new Button("Change Username", e -> openChangeUsernameDialog());
        //round button edges
        changeUsername.getElement().getStyle().set("border-radius", "auto");
        //make it wide as the dialog box
        changeUsername.setWidth("90%");
        //center
        changeUsername.getElement().getStyle().set("margin", "0 auto");

        changeUsername.addClassName("button");
        Button changePassword = new Button("Change Password", e -> openChangePasswordDialog());
        //black
        changePassword.addClassName("button");
        changePassword.getElement().getStyle().set("border-radius", "5px");
        //make it wide as the dialog box
        changePassword.setWidth("90%");
        //center
        changePassword.getElement().getStyle().set("margin", "0 auto");

        Button closeButton = addCloseButton(dialog);
//        dialogLayout.add(changeUsername, changePassword, closeDialogButton);
        //align the layout to the center
        dialogLayout.add(changeUsername, changePassword, closeButton);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);
        dialog.open();

    }

    private void openChangePasswordDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        //make the title font smaller

        dialog.setWidth("500px");
        dialog.setHeight("300px");

        H3 title = new H3("Personal Settings");
        title.getElement().getStyle().set("font-size", "15px"); // Set smaller font size
        dialog.add(title);
        //put title in the center
        title.getElement().getStyle().set("margin", "0 auto");



        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span(""));
//        dialogLayout.add(title);
        PasswordField oldPassword = new PasswordField("Old Password");
        PasswordField password = new PasswordField("New Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");
        Button save = new Button("Save changes", e -> {
            presenter.changePassword(oldPassword.getValue(), password.getValue(), confirmPassword.getValue(), password);
//            dialog.close();
        });
        save.addClassName("button");
        //position save button at the bottom right corner
        save.getElement().getStyle().set("position", "absolute");
        save.getElement().getStyle().set("bottom", "0");
        save.getElement().getStyle().set("right", "0");

        Button cancelButton = cancelButton(dialog);


        Button closeDialogButton = addCloseButton(dialog);
        dialogLayout.add(password, confirmPassword, save, closeDialogButton, cancelButton);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openChangeUsernameDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setWidth("500px");
        dialog.setHeight("200px");

        H3 title = new H3("Change Username");
        title.getElement().getStyle().set("font-size", "15px"); // Set smaller font size
        dialog.add(title);
        //put title in the center
        title.getElement().getStyle().set("margin", "0 auto");



        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span(""));
        TextField username = new TextField("New Username");
        Button save = new Button("Save changes", e -> {
            presenter.changeUsername(presenter.getUserName(), username.getValue(), username);
            //If the username is changed, update the welcome text
//            welcomeText();
            //if there is an error, keep the dialog open
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");
            dialog.close();
        });
        save.addClassName("button");
        //position save button at the bottom right corner
        save.getElement().getStyle().set("position", "absolute");
        save.getElement().getStyle().set("bottom", "0");
        save.getElement().getStyle().set("right", "0");

        Button cancelButton = cancelButton(dialog);

        //transparent background
        cancelButton.getStyle().set("background-color", "transparent");
        Button closeDialogButton = addCloseButton(dialog);
        dialogLayout.add(username, save, closeDialogButton, cancelButton);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);
        dialog.open();
    }

    public Button cancelButton (Dialog dialog){
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        cancelButton.getElement().getStyle().set("color", "black");
        //position save button at the bottom left corner
        cancelButton.getElement().getStyle().set("position", "absolute");
        cancelButton.getElement().getStyle().set("bottom", "0");
        cancelButton.getElement().getStyle().set("left", "0");
        //transparent background
        cancelButton.getStyle().set("background-color", "transparent");
        return cancelButton;
    }


    private void addNotificationButton() {
        // Create a button for notifications
        Button notification = new Button("", e -> {
            getUI().ifPresent(ui -> ui.navigate("MessagesList"));
            // Navigate to the notification page
        });
        notification.setIcon(new Icon(VaadinIcon.BELL));
        notification.getElement().getStyle().setColor("black");
        notification.getElement().getStyle().set("margin-right", "10px");

        // Create a span for the notification count
        notificationCountSpan = new Span();
        notificationCountSpan.addClassName("notification-count");
        notificationCountSpan.setVisible(false);

        // Create a div to hold the notification icon and count
        Div notificationContainer = new Div(notification, notificationCountSpan);
        notificationContainer.addClassName("notification-container");

        // Add the notification container to the navbar
        if(!presenter.getUserName().contains("Guest")) {
            addToNavbar(notificationContainer);
        }
    }



    private void shoppingCart() {
        // Navigate to the shopping cart page
        Button cart = new Button("", e -> {
            // Define your condition here
            String username = presenter.getUserName();
            boolean conditionMet = presenter.isSuspended(username);

            if (conditionMet) {
                getUI().ifPresent(ui -> ui.navigate("shopping-cart"));
            } else {
                // Optionally, handle the case when the condition is not met
                Notification.show("You're suspended, therefor can't access the shopping cart.");
            }
        });
        cart.setIcon(new Icon(VaadinIcon.CART));
        cart.getElement().getStyle().setColor("black");
        cart.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search button
        addToNavbar(cart);

    }

    private void welcomeText() {
//        TextComponent text = new TextComponent();
        String username = presenter.getUserName();



        Text welcome = new Text("Welcome "+username+"!");
        Span welcomeSpan = new Span(welcome);
//        welcomeSpan.addClassName("single-line"); // Add the CSS class
        //dont spread on more than one line
        welcomeSpan.getStyle().set("white-space", "nowrap");
        //margin between the text and the search bar
        welcomeSpan.getStyle().set("margin-right", "10px");

        addToNavbar(welcomeSpan);

    }

    private Button addCloseButton(Dialog dialog){
        Button closeButton = new Button(new Icon(VaadinIcon.CLOSE));
        closeButton.getElement().getStyle().set("color", "black");
        //make the button round
        closeButton.addClassName("close-button");
        closeButton.addClickListener(e -> dialog.close());
        closeButton.getElement().getStyle().set("position", "absolute");
        closeButton.getElement().getStyle().set("top", "0");
        closeButton.getElement().getStyle().set("right", "0");
        closeButton.getElement().getStyle().set("background-color", "transparent");

        return closeButton;
    }

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty();
    }
    public void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate("login"));
    }

    private void logout() {
        presenter.logout();
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
    }

    public void showPwdError(String message, PasswordField field) {
        field.setErrorMessage(message);
        field.setInvalid(true);
    }

    public void showUNError(String message, TextField field) {
        field.setErrorMessage(message);
        field.setInvalid(true);
    }

    public void addStoreError(String message, TextField field) {
        field.setErrorMessage(message);
        field.setInvalid(true);

    }
}
