package Presentation.application.View.Store;

import Presentation.application.Presenter.Store.RolesManagementPresenter;
import Presentation.application.View.LoginView;
import Presentation.application.View.MainLayoutView;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@PageTitle("Roles Management")
@Route(value = "roles-management/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://styles.css")
public class RolesManagementView extends VerticalLayout implements BeforeEnterObserver {

    private final RolesManagementPresenter presenter;
    private final Grid<Map.Entry<String, String>> grid;
    private final MultiSelectComboBox<String> roleFilter;
    private final TextField usernameFilter;
    private HorizontalLayout filterAndButtonLayout;  // Declare filterAndButtonLayout as a class member
    private Integer storeId;

    public RolesManagementView(RolesManagementPresenter presenter) {
        this.presenter = presenter;
        this.presenter.setView(this);

        addClassName("page-view");
        setSizeFull();
        setMargin(true);
        setPadding(true);
        setSpacing(true);  // Set spacing to true for better visual appearance

        // Initialize UI components
        roleFilter = new MultiSelectComboBox<>();
        roleFilter.setLabel("Filter by Role");
        roleFilter.setItems("OWNER", "MANAGER", "CREATOR", "SUBSCRIBER");
        roleFilter.setPlaceholder("Select roles...");
        roleFilter.setClearButtonVisible(true);
        roleFilter.addValueChangeListener(event -> updateRoles());

        usernameFilter = new TextField("Search by Username");
        usernameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        usernameFilter.addValueChangeListener(event -> updateRoles());

        grid = new Grid<>();
        grid.addClassName("custom-grid");
        grid.addColumn(Map.Entry::getKey).setHeader("Username");
        grid.addColumn(Map.Entry::getValue).setHeader("Role");
        grid.addItemClickListener(event -> handleGridItemClick(event.getItem()));

        HorizontalLayout filterLayout = new HorizontalLayout(roleFilter, usernameFilter);
        filterLayout.setAlignItems(FlexComponent.Alignment.END);
        filterLayout.setSpacing(true);
        filterLayout.setWidthFull();

        filterAndButtonLayout = new HorizontalLayout();
        filterAndButtonLayout.setWidthFull();
        filterAndButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        filterAndButtonLayout.setAlignItems(FlexComponent.Alignment.END);
        filterAndButtonLayout.add(filterLayout);  // Add filterLayout to the filterAndButtonLayout

        add(filterAndButtonLayout);
        // The buttonLayout will be initialized and added in the beforeEnter method
        add(grid);
        expand(grid);

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
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
        String id = event.getRouteParameters().get("storeId").orElse("");
        if (id.isEmpty()) {
            event.rerouteTo("");
        }
        storeId = Integer.parseInt(id);
        initButtonLayout();
        updateRoles();
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

    private void initButtonLayout() {
        HorizontalLayout buttonLayout = createButtonLayout();
        filterAndButtonLayout.add(buttonLayout);  // Add buttonLayout to the filterAndButtonLayout
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);

        if ((presenter.hasRole(storeId, "Owner") || presenter.hasRole(storeId, "Creator")) && presenter.isActiveStore(storeId)) {
            Button addButton = new Button("+ Nominate", e -> showNominationDialog());
            addButton.addClassName("yes_button");  // Adding class for custom styling
            buttonLayout.add(addButton);
        }
        if (presenter.hasRole(storeId, "Owner") && presenter.isActiveStore(storeId)) {
            Button waiveOwnershipButton = new Button("Waive Ownership", e -> navigateToOwnershipWaiving());
            waiveOwnershipButton.addClassName("no_button");  // Adding class for custom styling
            buttonLayout.add(waiveOwnershipButton);
        }
        // Back to Store Management button
        Button backButton = new Button("Back to Store Management", event -> {
            RouteParameters routeParameters = new RouteParameters("storeId", storeId.toString());
            UI.getCurrent().navigate(StoreManagementView.class, routeParameters);
        });
        backButton.addClassName("button");
        buttonLayout.add(backButton);

        return buttonLayout;
    }

    private void navigateToOwnershipWaiving() {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialogLayout.add("Are you sure you want to waive your ownership?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.waiveOwnership(storeId);
            confirmationDialog.close();
            showSuccess("Ownership waived successfully");
            UI.getCurrent().navigate("");
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 100);");
        });
        confirmButton.addClassName("yes_button");

        Button cancelButton = new Button("No", e -> confirmationDialog.close());
        cancelButton.addClassName("no_button");

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);
        buttonLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END); // Align buttons to the right

        dialogLayout.add(confirmButton, cancelButton);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    private void handleGridItemClick(Map.Entry<String, String> item) {
        String role = item.getValue();
        String username = item.getKey();

        if (presenter.isActiveStore(storeId) && "MANAGER".equals(role) && ((presenter.hasRole(storeId, "Owner") && presenter.isNominatorOf(storeId, username)) || presenter.hasRole(storeId, "Creator"))) {
            showPermissionManagementDialog(username);
        }
        if (!presenter.isActiveStore(storeId) && "MANAGER".equals(role) && (presenter.hasRole(storeId, "Owner") && !presenter.isNominatorOf(storeId, username))) {
            showError("You're not the nominator of this manager");
        }

        if (presenter.isActiveStore(storeId) && "SUBSCRIBER".equals(role) && (presenter.hasRole(storeId, "Owner") || presenter.hasRole(storeId, "Creator") || (presenter.hasRole(storeId, "Manager") && presenter.hasPermission(storeId, "REMOVE_STORE_SUBSCRIPTION")))) {
            showSubscriberDialog(username);
        }
    }

    private void showSubscriberDialog(String username) {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setCloseOnOutsideClick(false);

        Button removeSubscriptionButton = new Button("Remove Subscription", e -> navigateToSubscriptionRemoving(username));
        removeSubscriptionButton.addClassName("button");

        VerticalLayout dialogLayout = new VerticalLayout(removeSubscriptionButton);
        dialogLayout.setPadding(false);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void navigateToSubscriptionRemoving(String username) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialogLayout.add("Are you sure you want to remove this subscription?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.removeSubscription(storeId, username);
            confirmationDialog.close();
            showSuccess("Subscription removed successfully");
            UI.getCurrent().navigate("");
            UI.getCurrent().getPage().executeJs("setTimeout(function() { window.location.reload(); }, 100);");
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


    private void showNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setCloseOnOutsideClick(false);

        Button nominateOwnerButton = new Button("Nominate Owner", e -> showOwnerNominationDialog());
        Button nominateManagerButton = new Button("Nominate Manager", e -> showManagerNominationDialog());
        nominateOwnerButton.addClassName("button");
        nominateManagerButton.addClassName("button");
        VerticalLayout dialogLayout = new VerticalLayout(nominateOwnerButton, nominateManagerButton);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showOwnerNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setCloseOnOutsideClick(false);

        TextField usernameField = new TextField("Enter Username");
        Button nominateButton = new Button("Send Owner Nomination Request", e -> {
            presenter.nominateOwner(storeId, usernameField.getValue());
            dialog.close();
        });
        nominateButton.addClassName("yes_button");

        VerticalLayout dialogLayout = new VerticalLayout(usernameField, nominateButton);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showManagerNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setCloseOnOutsideClick(false);

        TextField usernameField = new TextField("Enter Username");
        MultiSelectListBox<String> permissionSelect = new MultiSelectListBox<>();
        permissionSelect.setItems(presenter.getPermissions());

        Button nominateButton = new Button("Send Manager Nomination Request", e -> {
            String nominatedUsername = usernameField.getValue();
            Set<String> selectedPermissions = permissionSelect.getValue();
            presenter.nominateManager(storeId, nominatedUsername, selectedPermissions);
            dialog.close();
        });
        nominateButton.addClassName("yes_button");

        VerticalLayout dialogLayout = new VerticalLayout(usernameField, permissionSelect, nominateButton);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showPermissionManagementDialog(String managerUsername) {
        Dialog dialog = new Dialog();
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        dialog.setCloseOnOutsideClick(false);

        MultiSelectListBox<String> permissionSelect = new MultiSelectListBox<>();
        permissionSelect.setItems(presenter.getPermissions());

        Set<String> currentPermissions = presenter.getManagerPermissions(storeId, managerUsername);
        permissionSelect.setValue(currentPermissions);

        // Create a styled title with the manager's username
        Span title = new Span("Manage Permissions for: ");
        Span managerName = new Span(managerUsername);
        title.addClassName("dialog-title");
        managerName.addClassName("manager-name");

        HorizontalLayout titleLayout = new HorizontalLayout(title, managerName);
        titleLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        Button saveButton = new Button("Save", e -> {
            Set<String> selectedPermissions = permissionSelect.getValue();
            presenter.updateManagerPermissions(storeId, managerUsername, selectedPermissions);
            dialog.close();
        });
        saveButton.addClassName("yes_button");

        VerticalLayout dialogLayout = new VerticalLayout(titleLayout, permissionSelect, saveButton);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.open();
    }

    public void notify(String message) {
        Notification.show(message);
    }

    public void showRoles(Collection<Map.Entry<String, String>> roles) {
        grid.setItems(roles);
    }

    public void showError(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    private void updateRoles() {
        presenter.loadRoles(storeId, roleFilter.getValue(), usernameFilter.getValue());
    }
}
