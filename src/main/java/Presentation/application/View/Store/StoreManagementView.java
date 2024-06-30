package Presentation.application.View.Store;

import Presentation.application.CookiesHandler;
import Presentation.application.Presenter.Store.StoreManagementPresenter;
import Presentation.application.View.LoginView;
import Presentation.application.View.MainLayoutView;
import Presentation.application.View.UtilitiesView.WSClient;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Domain.Store.Inventory.ProductDTO;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@PageTitle("Store Management")
@Route(value = "store-management/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://styles.css")
public class StoreManagementView extends VerticalLayout implements BeforeEnterObserver {

    private final Div content;
    private Integer storeId;
    private String storeName;
    private final StoreManagementPresenter presenter;

    public StoreManagementView(StoreManagementPresenter presenter) {
        content = new Div();
        content.setSizeFull();
        this.presenter = presenter;
        presenter.attachView(this);
        addClassName("page-view");

        // Set full size
        setSizeFull();
    }
 WSClient wsClient;
    private void navigateToStoreReopening() {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialogLayout.add("Are you sure you want to re-open this store?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.reopenStore(storeId);
            confirmationDialog.close();
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");
         //   showSuccess("Store re-opened successfully");
            try {
                UI ui=UI.getCurrent();
                String user= CookiesHandler.getUsernameFromCookies(((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest());

                wsClient=WSClient.getClient(ui,user);

                wsClient.sendMessage(user+":reopen store");
            } catch (InterruptedException eX) {
                eX.printStackTrace();
            } catch (ExecutionException eX) {
                eX.printStackTrace();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
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

    private void navigateToStoreClosing() {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialogLayout.add("Are you sure you want to close this store?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.closeStore(storeId);
            confirmationDialog.close();
         //   UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 1);");
           // showSuccess("Store closed successfully");
            try {
                UI ui=UI.getCurrent();
                String user= CookiesHandler.getUsernameFromCookies(((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest());

                wsClient=WSClient.getClient(ui,user);

                wsClient.sendMessage(user+":close store");
            } catch (InterruptedException eX) {
                eX.printStackTrace();
            } catch (ExecutionException eX) {
                eX.printStackTrace();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }

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

    private void navigateToRolesManagement() {
        RouteParameters routeParameters = new RouteParameters("storeId", String.valueOf(storeId));
        UI.getCurrent().navigate(RolesManagementView.class, routeParameters);
    }

    private void navigateToProductManagement() {
        RouteParameters routeParameters = new RouteParameters("storeId", String.valueOf(storeId));
        UI.getCurrent().navigate(ProductManagementView.class, routeParameters);
    }

    private void openCreateDiscountDialog() {
        List<ProductDTO> products = presenter.getProducts(storeId);
        Dialog createDiscountDialog = new CreateDiscountDialog(presenter, storeId, products);
        createDiscountDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        createDiscountDialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
        String id = event.getRouteParameters().get("storeId").orElse("");
        storeId = Integer.parseInt(id);
        storeName = presenter.getStoreName(storeId);

        H1 storeTitle = new H1("Store Management: " + storeName);
        storeTitle.addClassName("title");
        storeTitle.getStyle().set("font-size", "8em"); // Set the title size here
        storeTitle.getStyle().set("text-align", "center");
        storeTitle.getStyle().set("width", "100%"); // Ensure it spans the width of the container

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.getStyle().set("margin-top", "10vh");

        if (presenter.hasPermission(storeId, "VIEW_PRODUCTS")) {
            Button productManagementButton = new Button("Products", e -> navigateToProductManagement());
            productManagementButton.addClassName("button");
            productManagementButton.getStyle().set("font-size", "1.5em"); // Set button text size here
            productManagementButton.getStyle().set("padding", "1.5em 2.5em"); // Set button padding here
            productManagementButton.getStyle().set("min-width", "250px"); // Set minimum width here
            buttonLayout.add(productManagementButton);
        }
        if (presenter.hasPermission(storeId, "VIEW_DISCOUNTS_POLICIES")) {
            Button createDiscountButton = new Button("Discounts / Policies", e -> openCreateDiscountDialog());
            createDiscountButton.addClassName("button");
            createDiscountButton.getStyle().set("font-size", "1.5em"); // Set button text size here
            createDiscountButton.getStyle().set("padding", "1.5em 2.5em"); // Set button padding here
            createDiscountButton.getStyle().set("min-width", "250px"); // Set minimum width here
            buttonLayout.add(createDiscountButton);
        }
        if (presenter.hasPermission(storeId, "VIEW_STORE_STAFF_INFO")) {
            Button rolesManagementButton = new Button("Roles", e -> navigateToRolesManagement());
            rolesManagementButton.addClassName("button");
            rolesManagementButton.getStyle().set("font-size", "1.5em"); // Set button text size here
            rolesManagementButton.getStyle().set("padding", "1.5em 2.5em"); // Set button padding here
            rolesManagementButton.getStyle().set("min-width", "250px"); // Set minimum width here
            buttonLayout.add(rolesManagementButton);
        }
        if (presenter.hasPermission(storeId, "VIEW_PURCHASE_HISTORY")) {
            Button viewPurchaseHistoryButton = new Button("Purchase History");
            viewPurchaseHistoryButton.addClassName("button");
            viewPurchaseHistoryButton.getStyle().set("font-size", "1.5em"); // Set button text size here
            viewPurchaseHistoryButton.getStyle().set("padding", "1.5em 2.5em"); // Set button padding here
            viewPurchaseHistoryButton.getStyle().set("min-width", "250px"); // Set minimum width here
            viewPurchaseHistoryButton.addClickListener(event1 -> {
                Integer storeId = this.storeId;
                getUI().ifPresent(ui -> ui.navigate("orders/" + storeId));
            });
            buttonLayout.add(viewPurchaseHistoryButton);
        }

        if (presenter.isCreator(storeId)) {
            if (presenter.isActiveStore(storeId)) {
                Button storeClosingButton = new Button("Close Store", e -> navigateToStoreClosing());
                storeClosingButton.addClassName("button");
                storeClosingButton.getStyle().set("font-size", "1.5em"); // Set button text size here
                storeClosingButton.getStyle().set("padding", "1.5em 2.5em"); // Set button padding here
                storeClosingButton.getStyle().set("min-width", "250px"); // Set minimum width here
                buttonLayout.add(storeClosingButton);
            } else {
                Button storeReopeningButton = new Button("Reopen Store", e -> navigateToStoreReopening());
                storeReopeningButton.addClassName("button");
                storeReopeningButton.getStyle().set("font-size", "1.5em"); // Set button text size here
                storeReopeningButton.getStyle().set("padding", "1.5em 2.5em"); // Set button padding here
                storeReopeningButton.getStyle().set("min-width", "250px"); // Set minimum width here
                buttonLayout.add(storeReopeningButton);
            }
        }

        buttonLayout.setSpacing(true);

        add(storeTitle);
        add(buttonLayout);
        add(content);
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

    public void showError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }
}
