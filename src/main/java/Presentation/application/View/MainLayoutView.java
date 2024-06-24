
package Presentation.application.View;

import Presentation.application.CookiesHandler;
import Presentation.application.Presenter.MainLayoutPresenter;

import Presentation.application.View.Store.StoreManagementView;
import Presentation.application.View.Store.StorePageView;


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
import com.vaadin.flow.component.dependency.StyleSheet;
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
@Route("")
@PageTitle("Main")
@StyleSheet("context://login-view-styles.css")
public class MainLayoutView extends AppLayout implements BeforeEnterObserver {

    private final MainLayoutPresenter presenter;

    private H1 viewTitle;
    private Queue<Message> sub;
    private VerticalLayout mainContent;
    private Span notificationCountSpan;


    public MainLayoutView(MainLayoutPresenter presenter) {
        addClassName("main-view");
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
        discountsText();
//        addCategoriesButton();
        addStoresToMain();
//        addMessageButton();
        UI currentUI = UI.getCurrent();
//        navigateToStorePage();
//        addHomeButtonToDrawer();

        // Initialize the notification count
        int unreadCount = presenter.getUnreadMessagesCount(); // Fetch this from the server or database
        if (unreadCount > 0) {
            notificationCountSpan.setText(String.valueOf(unreadCount));
            notificationCountSpan.setVisible(true);
        }

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
//    private void createCategorySearchButton() {
//        // Create the button to open the category list
//        Button categorySearchButton = new Button("Search by Category");
//        categorySearchButton.addClickListener(event -> openCategoryDialog());
//
//        // Add the button to your layout, for example, in the navbar
//        addToNavbar(categorySearchButton);
//    }
//
//    private void openCategoryDialog() {
//        Dialog categoryDialog = new Dialog();
//        categoryDialog.setWidth("400px");
//        categoryDialog.setHeight("300px");
//
//        // Create and configure the close button for the dialog
//        Button closeButton = new Button("Close");
//        closeButton.addClickListener(event -> categoryDialog.close());
//
//        // Layout for the dialog content
//        VerticalLayout dialogLayout = new VerticalLayout();
//        dialogLayout.setPadding(false);
//        dialogLayout.setSpacing(false);
//
//        // Add the close button to the dialog layout
//        dialogLayout.add(closeButton);
//
//        // Fetch all categories from the presenter
//        ArrayList<String> categories = presenter.getAllCategories();
//
//        for (String category : categories) {
//            Button categoryButton = new Button(category);
//            categoryButton.getElement().getStyle().set("margin", "5px"); // Add some margin for better spacing
//            categoryButton.getElement().getStyle().set("background-color", "#4CAF50"); // Set button color
//            categoryButton.getElement().getStyle().set("color", "white"); // Set text color
//            categoryButton.addClickListener(event -> {
//                categoryDialog.close();
//                searchByCategory(category);
//            });
//            dialogLayout.add(categoryButton);
//        }
//
//        // Add the layout to the dialog
//        categoryDialog.add(dialogLayout);
//
//        // Open the dialog
//        categoryDialog.open();
//    }

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
//                Response<ArrayList<ProductDTO>> productsRes = presenter.searchProductsByCategory(cat);
//                ArrayList<ProductDTO> products = productsRes.getData();
//                //display the products
//                displaySearchResults(products);
            });
            //add the category item to the menu
            categoryMenu.addItem(categoryItem);
        }

        addToNavbar(categoryButton);


    }

    private void addHomeButtonToDrawer() {
        Button homeButton = new Button("", e -> {
            UI.getCurrent().navigate(MainLayoutView.class);
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");
        });
        homeButton.getElement().getStyle().set("color", "black");
        homeButton.setWidthFull();
        addToDrawer(homeButton);
    }

    private void addDrawerContent() {
 Span appName = new Span("My App");
 // appName.addClassNames(Lumo.FontStyle.BOLD, Lumo.FontStyle.LARGE);
 Header header = new Header(appName);

 Scroller scroller = new Scroller(createNavigation());

 addToDrawer(header, scroller, createFooter());
 }


 private SideNav createNavigation() {
 SideNav nav = new SideNav();
 SideNavItem paymentItem = new SideNavItem("Payment");
 paymentItem.addAttachListener(new ComponentEventListener<AttachEvent>() {
 @Override
 public void onComponentEvent(AttachEvent event) {

 }
 });

// nav.addItem(new SideNavItem("Payment", PaymentView.class));

// nav.addItem(new SideNavItem("Messages", MessagesList.class));
// nav.addItem(new SideNavItem("Roles Management", RolesManagementView.class)); // New navigation item
// nav.addItem(new SideNavItem("My Shopping Cart", ShoppingCartView.class)); // New navigation item


 return nav;
 }

    private void navigateToStorePage() {
//        UI.getCurrent().navigate(StorePageView.class);
        //add the button to the main content area not the navbar
        Button nav = new Button("Store Page", e -> UI.getCurrent().navigate(StorePageView.class));
        nav.getElement().getStyle().set("color", "black");
//        setContent(nav);
        mainContent.add(nav);

    }

    private void openDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setHeight("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Div(new com.vaadin.flow.component.html.Span("My Stores")));
        Button closeDialogButton = new Button("Close", e -> dialog.close());
        closeDialogButton.getElement().getStyle().set("color", "black");
        dialogLayout.add(closeDialogButton);

        dialog.add(dialogLayout);
        dialog.open();
    }


    private void addUserButton() {
        // Navigate to the shopping cart page
        //dropdown menu

        Button userButton = new Button("");
        ContextMenu dropdownMenu = new ContextMenu(userButton);
        dropdownMenu.setOpenOnClick(true);
        //icon
        userButton.setIcon(new Icon(VaadinIcon.USER));



        if(!presenter.getUserName().contains("Guest")) {
            MenuItem myStores = dropdownMenu.addItem("My Stores", e -> myStoresDialog());
            MenuItem personalSettings = dropdownMenu.addItem("Personal Settings", e -> openSettings());
        }

        //if user is guest add register button
        if(presenter.getUserName().contains("Guest")) {
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
        dialog.setWidth("500px");
        dialog.setHeight("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Choose a store:"));

        List<String> stores = getUsersStores(presenter.getUserName());

        VerticalLayout activeStoresLayout = new VerticalLayout();
        activeStoresLayout.add(new H4("Active Stores:"));

        VerticalLayout deactivatedStoresLayout = new VerticalLayout();
        deactivatedStoresLayout.add(new H4("Deactivated Stores:"));

        for (String store : stores) {
            final Integer storeId = getStoreIdByName(store);  // Get the store ID once per iteration

            Button storeButton = new Button(store, e -> {
                RouteParameters routeParameters = new RouteParameters("storeId", storeId.toString());
                UI.getCurrent().navigate(StoreManagementView.class, routeParameters);
                dialog.close();
            });
            storeButton.getElement().getStyle().set("color", "black");

            if (isStoreActive(storeId)) {
                activeStoresLayout.add(storeButton);
            } else {
                deactivatedStoresLayout.add(storeButton);
            }
        }

        Button openNewStore = new Button("Open a new store", e -> openNewStoreDialog());
        openNewStore.getElement().getStyle().set("color", "black");
        openNewStore.getElement().getStyle().set("position", "absolute");
        openNewStore.getElement().getStyle().set("bottom", "0");
        openNewStore.getElement().getStyle().set("left", "0");
        openNewStore.getElement().getStyle().set("right", "0");

        Button closeDialogButton = addCloseButton(dialog);

        dialogLayout.add(activeStoresLayout, deactivatedStoresLayout, openNewStore, closeDialogButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    public void discountsText() {
        Span discounts = new Span("Discounts");
        //center the text
        discounts.getElement().getStyle().set("margin", "0 auto");

        discounts.getElement().getStyle().set("font-size", "20px");
        discounts.getElement().getStyle().set("margin-left", "10px");
        discounts.getElement().getStyle().set("margin-top", "10px");
        discounts.getElement().getStyle().set("margin-bottom", "10px");
        //add the text to the main content area

        mainContent.add(discounts);
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
        dialogLayout.add(new Span("Open a new store:"));

        TextField storeName = new TextField("Store Name");
        //place in the center
        storeName.getElement().getStyle().set("margin", "0 auto");
        String user=CookiesHandler.getUsernameFromCookies(getRequest());

        try {
            UI ui=UI.getCurrent();
            wsClient=new WSClient(ui,user);


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
        openStore.getElement().getStyle().set("color", "black");
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



            // Counter to keep track of buttons added
            for (int j = 0; j < 4 && (i + j) < stores.size(); j++) {
                String store = stores.get(i + j);
                final Integer storeId = getStoreIdByName(store);
                Button storeButton = new Button(store, e -> {
                    RouteParameters routeParameters = new RouteParameters("storeId", storeId.toString());
                    UI.getCurrent().navigate(StorePageView.class, routeParameters);
                });
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
            webSocketClient = new WSClient(ui, user);
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

//        Button purchaseHistoryByStoreButton = new Button("Purchase History By Store");
//        purchaseHistoryByStoreButton.addClickListener(e -> {
//            UI.getCurrent().navigate(StorePurchaseHistory.class);
//        });

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
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        //  viewTitle.addClassNames(Lumo.FontStyle.LARGE, Lumo.Margin.NONE);
        Button b=new Button("check message",e->sub.add(new NormalMessage("Message has benn added")));
        addToNavbar(toggle, viewTitle);
    }

//    private void addDrawerContent() {
//        Span appName = new Span("My App");
//        //  appName.addClassNames(Lumo.FontStyle.BOLD, Lumo.FontStyle.LARGE);
//        Header header = new Header(appName);
//
//        Scroller scroller = new Scroller(createNavigation());
//
//        addToDrawer(header, scroller, createFooter());
//    }
//
//
//    private SideNav createNavigation() {
//        SideNav nav = new SideNav();
//        SideNavItem paymentItem = new SideNavItem("Payment");
//        paymentItem.addAttachListener(new ComponentEventListener<AttachEvent>() {
//            @Override
//            public void onComponentEvent(AttachEvent event) {
//
//            }
//        });
//
////        nav.addItem(new SideNavItem("Payment", PaymentView.class));
//
//        nav.addItem(new SideNavItem("Messages", MessagesList.class));
////        nav.addItem(new SideNavItem("Roles Management", RolesManagementView.class)); // New navigation item
//        nav.addItem(new SideNavItem("My Shopping Cart", ShoppingCartView.class)); // New navigation item
//
//
//        return nav;
//    }

    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if the user is already logged in
//        if (!isLoggedIn()) {
//            // If not logged in, reroute to the login page
//            event.rerouteTo(LoginView.class);
//        }
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



    private void displaySearchResults(ArrayList<ProductDTO> results) {
        Dialog dialog = new Dialog();
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

        HorizontalLayout productDetailsLayout = new HorizontalLayout();
        VerticalLayout nameLayout = new VerticalLayout();
        VerticalLayout priceLayout = new VerticalLayout();
        VerticalLayout quantityLayout = new VerticalLayout();
        VerticalLayout buttonLayout = new VerticalLayout();

        productDetailsLayout.add(nameLayout, priceLayout, quantityLayout, buttonLayout);


        // Create the "Search Results" span title
        Span searchResultsSpan = new Span("Search Results");
        searchResultsSpan.getElement().getStyle().set("font-size", "20px"); // Set the font size of the title

        // Add the title and filter button to the titleAndFilterLayout
        titleAndFilterLayout.add(searchResultsSpan, filterButton);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(titleAndFilterLayout);

        if(results.isEmpty()){
            dialogLayout.add(new Span("No results found"));
        }

        for (ProductDTO product : results) {
//            Div productDiv = new Div();



            // Product name span
            Span productNameSpan = new Span(product.getProductName());
            productNameSpan.getElement().getStyle().set("margin-right", "1em"); // Add margin between name and price
            //set the font size of the product name
            productNameSpan.getElement().getStyle().set("font-size", "18px");
            //set height of the product name
            productNameSpan.getElement().getStyle().set("height", "48px");

            // Product price span
            Span productPriceSpan = new Span("$" + product.getPrice()); // Assuming price is stored in ProductDTO
            productPriceSpan.getElement().getStyle().set("color", "gray");
            productPriceSpan.getElement().getStyle().set("font-size", "18px");
            productPriceSpan.getElement().getStyle().set("height", "48px");

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

            nameLayout.add(productNameSpan);
            priceLayout.add(productPriceSpan);
            quantityLayout.add(quantityIncDec);
            buttonLayout.add(addToCartButton);

            // Add components to productDetailsLayout
//            productDetailsLayout.add(productNameSpan, productPriceSpan, createQuantityLayout(quantityField, decreaseButton, increaseButton, addToCartButton));

            // Add productDetailsLayout to productDiv
//            productDiv.add(productDetailsLayout);

            // Add productDiv to dialogLayout
            dialogLayout.add(productDetailsLayout);
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

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Filter Options"));
        dialogLayout.getElement().getStyle().set("font-size", "20px"); // Set the font size of the title

        // Add filter options such as price range, product review, and store review
//        ComboBox<String> priceRangeFilter = new ComboBox<>();
//        priceRangeFilter.setLabel("Price Range");
//        priceRangeFilter.setItems("Low to High", "High to Low");
//
        // add filter for prices range - min and max in an interactive line to drag min and max from 2 edges
        TextField minPrice = new TextField("Min Price");
        TextField maxPrice = new TextField("Max Price");


//        ComboBox<String> productReviewFilter = new ComboBox<>();
//        productReviewFilter.setLabel("Product Review");
//        productReviewFilter.setItems("Excellent", "Good", "Average", "Poor");
//
//        ComboBox<String> storeReviewFilter = new ComboBox<>();
//        storeReviewFilter.setLabel("Store Review");
//        storeReviewFilter.setItems("Excellent", "Good", "Average", "Poor");



        Button applyFilterButton = new Button("Apply Filters", e -> {

            // Apply the selected filters and update the search results accordingly
           ArrayList<ProductDTO> filteredResults = filterPriceRange(results, Double.parseDouble(minPrice.getValue()), Double.parseDouble(maxPrice.getValue()));
            displaySearchResults(filteredResults);
//            filterProductReview(productReviewFilter.getValue());
//            filterStoreReview(storeReviewFilter.getValue());


            dialog.close();
        });


        //put apply filter button at the bottom
//        applyFilterButton.getElement().getStyle().set("position", "absolute");
//        applyFilterButton.getElement().getStyle().set("bottom", "0");
//        applyFilterButton.getElement().getStyle().set("left", "0");
//        applyFilterButton.getElement().getStyle().set("right", "0");

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
        presenter.addToCart(product, quantity);
    }


    private void openSettings(){
        //open a dialog with two buttons - change username and change password
        Dialog dialog = new Dialog();
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

        changeUsername.getElement().getStyle().set("color", "black");
        Button changePassword = new Button("Change Password", e -> openChangePasswordDialog());
        //black
        changePassword.getElement().getStyle().set("color", "black");
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
        PasswordField password = new PasswordField("New Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");
        Button save = new Button("Save changes", e -> {
            presenter.changePassword(password.getValue(), confirmPassword.getValue(), password);
//            dialog.close();
        });
        save.getElement().getStyle().set("color", "black");
        //position save button at the bottom right corner
        save.getElement().getStyle().set("position", "absolute");
        save.getElement().getStyle().set("bottom", "0");
        save.getElement().getStyle().set("right", "0");
        save.getElement().getStyle().set("background-color", "transparent");
        Button cancelButton = cancelButton(dialog);


        Button closeDialogButton = addCloseButton(dialog);
        dialogLayout.add(password, confirmPassword, save, closeDialogButton, cancelButton);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openChangeUsernameDialog() {
        Dialog dialog = new Dialog();
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
        save.getElement().getStyle().set("color", "black");
        //position save button at the bottom right corner
        save.getElement().getStyle().set("position", "absolute");
        save.getElement().getStyle().set("bottom", "0");
        save.getElement().getStyle().set("right", "0");
        save.getElement().getStyle().set("background-color", "transparent");
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
        addToNavbar(notificationContainer);
    }



    private void shoppingCart() {
        // Navigate to the shopping cart page
        Button cart = new Button("", e -> {
            getUI().ifPresent(ui -> ui.navigate("shopping-cart"));
        });
        cart.setIcon(new Icon(VaadinIcon.CART));
        cart.getElement().getStyle().setColor("black");
        cart.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search button

//        HorizontalLayout cartLayout = new HorizontalLayout(cart);
//        cartLayout.addClassName("right-layout"); // Add the CSS class
        //on the right side of the page
//        cart.getElement().getStyle().setAlignSelf(Style.JustifyContentMode.FLEX_END);
        addToNavbar(cart);
//        addToNavbar(cartLayout);

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
        // Implement your token validation logic here
        // This could involve checking the token against a database or decoding a JWT token
        return token != null && !token.isEmpty();
    }

//    private void addLogoutButton() {
//        Button logoutButton = new Button("Logout", e -> logout());
//        logoutButton.getElement().getStyle().set("margin-top", "auto"); // This will push the button to the bottom
//        addToDrawer(logoutButton);
//    }

    public void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate("login"));
    }

    private void logout() {
        presenter.logout();
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        return footer;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        Component content = getContent();
        if (content == null) {
            return "";
        }
        PageTitle title = content.getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    public void showPwdError(String message, PasswordField field) {
        field.setErrorMessage(message);
        field.setInvalid(true);
    }

    public void showUNError(String message, TextField field) {
        field.setErrorMessage(message);
        field.setInvalid(true);
    }


    public void pwdSuccess() {
//        CookiesHandler.setCookie("password", "password", 5 * 60); // 5 minutes
    }

    public void UnSuccess() {
//        CookiesHandler.setCookie("username", "username", 5 * 60); // 5 minutes
    }

    public void addStoreSuccess() {
//        CookiesHandler.setCookie("store", "store", 5 * 60); // 5 minutes

    }

    public void addStoreError(String message, TextField field) {
        field.setErrorMessage(message);
        field.setInvalid(true);

    }
}
