
package Presentaion.application.View;

import Presentaion.application.CookiesHandler;
import Presentaion.application.Presenter.MainLayoutPresenter;
import Presentaion.application.View.Messages.MessagesList;
import Presentaion.application.View.Payment.PaymentPage;

import Presentaion.application.View.PurchaseHistory.StorePurchaseHistory;
import Presentaion.application.View.Store.StoreManagementView;
import Presentaion.application.View.UtilitiesView.RealTimeNotifications;
import Service.ServiceInitializer;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import Domain.Store.Inventory.ProductDTO;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;

/**
 * The main view is a top-level placeholder for other views.
 */
@Route("")
@PageTitle("Main")
@StyleSheet("context://login-view-styles.css")
public class MainLayoutView extends AppLayout implements BeforeEnterObserver {

    private final MainLayoutPresenter presenter;
    private List<String> items = Stream.of("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape", "Honeydew")
            .collect(Collectors.toList());
    private H1 viewTitle;
    private Queue<Message> sub;


    public MainLayoutView(MainLayoutPresenter presenter) {
        addClassName("main-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
        sub=new LinkedBlockingQueue<>();
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        addLogoutButton();
        addUserButton();
        welcomeText();
        addSearchBar();
        addNotificationButton();
        shoppingCart();
        addCategoriesButton();
        addMessageButton();
        UI currentUI = UI.getCurrent();

        RealTimeNotifications.start(currentUI,sub);
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

        //check if the user is a store manager / owner/ creator
//if so add a button to manage stores
//        if(presenter.isManager() || presenter.isOwner() || presenter.isCreator(){
//            MenuItem manageStores = dropdownMenu.addItem("Manage Stores", e -> {
//                // Navigate to the shopping cart page
//            });
//        }

        //check if a user is a guest or subscribed user
//TODO: check if the user is a guest or a subscribed user
//        String username = presenter.getUserName();
        if(!presenter.getUserName().contains("Guest")) {
        }
        MenuItem myStores = dropdownMenu.addItem("My Stores", e -> myStoresDialog());
        MenuItem personalSettings = dropdownMenu.addItem("Personal Settings", e -> openSettings());

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

    private void navigateToRegister() {
        getUI().ifPresent(ui -> ui.navigate("register"));
    }

    private void myStoresButton() {
        // Navigate to the shopping cart page
        Button myStores = new Button("My Stores", e -> myStoresDialog());
        myStores.getElement().getStyle().setColor("black");
//        myStores.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search button
        addToDrawer(myStores);


    }

//    private void openStoresDialog() {
//        // Navigate to the shopping cart page
//        //when clicked open a dialog with the stores
//        Dialog dialog = new Dialog();
//        dialog.setWidth("500px");
//        dialog.setHeight("400px");
//        VerticalLayout dialogLayout = new VerticalLayout();
//        dialogLayout.add(new Span("choose a store:"));
//        //dropdown menu presenting the stores
//        ContextMenu dropdownMenu = new ContextMenu();
//        //present items based on given data - i dont know how many stores there are
//
//
//
//
//
//        Button closeDialogButton = new Button("Close", g -> dialog.close());
//        dialogLayout.add(closeDialogButton);
//
//        dialog.add(dialogLayout);
//        dialog.open();
//    }

    private void myStoresDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setHeight("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Choose a store:"));

        // Fetch store data

        List<String> stores = getUsersStores(presenter.getUserName());
        ContextMenu dropdownMenu = new ContextMenu();
        // Dropdown menu presenting the stores
        //show stores only if the user is a manager/owner/creator
        if(hasRole(presenter.getUserName())) {
            for (String store : stores) {
                dialogLayout.add(dropdownMenu.addItem(store, e -> {
                    // Navigate to the selected store
                }));
            }
        }


        //add a button for adding a store called "open a new store"
        Button openNewStore = new Button("Open a new store", e -> openNewStoreDialog());
        dialogLayout.add(openNewStore);
        openNewStore.getElement().getStyle().set("color", "black");
        //put the button at the bottom whole width
        openNewStore.getElement().getStyle().set("position", "absolute");
        openNewStore.getElement().getStyle().set("bottom", "0");
        openNewStore.getElement().getStyle().set("left", "0");
        openNewStore.getElement().getStyle().set("right", "0");
//        openNewStore.getElement().getStyle().set("background-color", "transparent");


        //add a close button
        Button closeDialogButton = addCloseButton(dialog);
        dialogLayout.add(closeDialogButton);



        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openNewStoreDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("300px");
        dialog.setHeight("250px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Open a new store:"));

        TextField storeName = new TextField("Store Name");
        //place in the center
        storeName.getElement().getStyle().set("margin", "0 auto");

//        TextField storeDescription = new TextField("Store Description");
        Button openStore = new Button("Open Store", e -> {
            presenter.addStore(storeName.getValue(), storeName);
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
        Button category3 = new Button("Clothing", e -> {
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

        category1.getElement().getStyle().set("color", "black");
        category2.getElement().getStyle().set("color", "black");
        category3.getElement().getStyle().set("color", "black");
        category4.getElement().getStyle().set("color", "black");
        category5.getElement().getStyle().set("color", "black");


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
        setContent(mainLayout);


//        categoriesLayout.add(category1, category2, category3, category4, category6);

//        addToNavbar(categoriesLayout);
    }


    private void addMessageButton() {
        Button addMessageButton = new Button("Add Message");
        addMessageButton.addClickListener(e -> sub.add(new NormalMessage("New message!")));
        // Add to the main content area



        Button storeButton = new Button("Manage Store");
        storeButton.addClickListener(e -> {
            UI.getCurrent().navigate(StoreManagementView.class);
        });

        Button purchaseHistoryButton = new Button("Purchase History");
        purchaseHistoryButton.addClickListener(e -> {
            UI.getCurrent().navigate(StorePurchaseHistory.class);
        });


        // Add to the main content area
        setContent(new VerticalLayout(addMessageButton,storeButton,purchaseHistoryButton));

    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        //  viewTitle.addClassNames(Lumo.FontStyle.LARGE, Lumo.Margin.NONE);
        Button b=new Button("check message",e->sub.add(new NormalMessage("Message has benn added")));
        addToNavbar(toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("My App");
        //  appName.addClassNames(Lumo.FontStyle.BOLD, Lumo.FontStyle.LARGE);
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

        nav.addItem(new SideNavItem("Payment", PaymentPage.class));

        nav.addItem(new SideNavItem("Messages", MessagesList.class));
        nav.addItem(new SideNavItem("Roles Management", RolesManagementView.class)); // New navigation item


        return nav;
    }

    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if the user is already logged in
        if (!isLoggedIn()) {
            // If not logged in, reroute to the login page
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

    public void addSearchBar() {
        // Add search bar to the header
        ComboBox<String> searchBar = new ComboBox<>();
        searchBar.setPlaceholder("Search for anything");
        searchBar.getElement().getStyle().setColor("black");
        searchBar.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search bar
        searchBar.getElement().getStyle().set("margin-left", "10px"); // Add a margin to the left side of the search bar
        searchBar.setWidthFull(); // Set the width of the search bar to 100%

        searchBar.setItemLabelGenerator(item -> item);
        searchBar.setClearButtonVisible(true);
        searchBar.setAllowCustomValue(true);

        // Fetch and display search results as user types
        searchBar.addCustomValueSetListener(event -> {
            String searchTerm = event.getDetail();
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                List<String> results = search(searchTerm).stream()
                        .map(ProductDTO::getName)
                        .collect(Collectors.toList());
                searchBar.setItems(results);
            }
        });

        addToNavbar(searchBar);
    }


    public ArrayList<ProductDTO> search(String search) {
        return presenter.searchProducts(search);
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

//            dialog.close();
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
        // Add notification button to the header
        Button notification = new Button("", e -> {
            // Navigate to the notification page
        });
        notification.setIcon(new Icon(VaadinIcon.BELL));
        notification.getElement().getStyle().setColor("black");
        notification.getElement().getStyle().set("margin-right", "10px"); // Add a margin to the right side of the search button
        addToNavbar(notification);
    }

    private void shoppingCart() {
        // Navigate to the shopping cart page
        Button cart = new Button("", e -> {
            // Navigate to the shopping cart page
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
        closeButton.addClassName("close-button");
        closeButton.getElement().getStyle().set("position", "absolute");
        closeButton.getElement().getStyle().set("top", "0");
        closeButton.getElement().getStyle().set("right", "0");

        return closeButton;
    }

    private boolean isValidToken(String token) {
        // Implement your token validation logic here
        // This could involve checking the token against a database or decoding a JWT token
        return token != null && !token.isEmpty();
    }

    private void addLogoutButton() {
        Button logoutButton = new Button("Logout", e -> logout());
        logoutButton.getElement().getStyle().set("margin-top", "auto"); // This will push the button to the bottom
        addToDrawer(logoutButton);
    }

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
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
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
        CookiesHandler.setCookie("password", "password", 5 * 60); // 5 minutes
    }

    public void UnSuccess() {
        CookiesHandler.setCookie("username", "username", 5 * 60); // 5 minutes
    }

    public void addStoreSuccess() {
        CookiesHandler.setCookie("store", "store", 5 * 60); // 5 minutes

    }

    public void addStoreError(String message, TextField field) {
        field.setErrorMessage(message);
        field.setInvalid(true);

    }
}
