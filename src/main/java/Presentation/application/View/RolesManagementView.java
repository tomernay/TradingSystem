package Presentation.application.View;

import Presentation.application.Presenter.RolesManagementPresenter;
import Presentation.application.View.Store.StoreManagementView;
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@PageTitle("Roles Management")
@Route(value = "roles-management/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class RolesManagementView extends VerticalLayout implements BeforeEnterObserver {

    private final RolesManagementPresenter presenter;
    private final Grid<Map.Entry<String, String>> grid;
    private final MultiSelectComboBox<String> roleFilter;
    private final TextField usernameFilter;
    private HorizontalLayout filterAndButtonLayout;  // Declare filterAndButtonLayout as a class member
    private String storeId;

    public RolesManagementView(RolesManagementPresenter presenter) {
        this.presenter = presenter;
        this.presenter.setView(this);

        addClassName("roles-management-view");
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
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        storeId = event.getRouteParameters().get("storeId").orElse("");
        initButtonLayout();
        updateRoles();
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

        if (presenter.hasRole(storeId, "Owner") || presenter.hasRole(storeId, "Creator")) {
            Button addButton = new Button("+ Nominate", e -> showNominationDialog());
            addButton.addClassName("add-button");  // Adding class for custom styling
            buttonLayout.add(addButton);
        }
        if (presenter.hasRole(storeId, "Owner")) {
            Button waiveOwnershipButton = new Button("Waive Ownership", e -> presenter.waiveOwnership(storeId));
            waiveOwnershipButton.addClassName("waive-button");  // Adding class for custom styling
            buttonLayout.add(waiveOwnershipButton);
        }
        // Back to Store Management button
        Button backButton = new Button("Back to Store Management", event -> {
            RouteParameters routeParameters = new RouteParameters("storeId", storeId);
            UI.getCurrent().navigate(StoreManagementView.class, routeParameters);
        });
        backButton.addClassName("back-button");  // Adding class for custom styling
        buttonLayout.add(backButton);

        return buttonLayout;
    }

    private void handleGridItemClick(Map.Entry<String, String> item) {
        String role = item.getValue();
        String username = item.getKey();

        if ("MANAGER".equals(role) && (presenter.hasRole(storeId, "Owner") || presenter.hasRole(storeId, "Creator") || (presenter.hasRole(storeId, "Manager") && presenter.hasPermission(storeId, "EDIT_PERMISSIONS")))) {
            showPermissionManagementDialog(username);
        }

        if ("SUBSCRIBER".equals(role) && (presenter.hasRole(storeId, "Owner") || presenter.hasRole(storeId, "Creator") || (presenter.hasRole(storeId, "Manager") && presenter.hasPermission(storeId, "REMOVE_STORE_SUBSCRIPTION")))) {
            showSubscriberDialog(username);
        }
    }

    private void showSubscriberDialog(String username) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        Button removeSubscriptionButton = new Button("Remove Subscription", e -> {
            presenter.removeSubscription(storeId, username);
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout(removeSubscriptionButton);
        dialogLayout.setPadding(false);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        Button nominateOwnerButton = new Button("Nominate Owner", e -> showOwnerNominationDialog());
        Button nominateManagerButton = new Button("Nominate Manager", e -> showManagerNominationDialog());

        VerticalLayout dialogLayout = new VerticalLayout(nominateOwnerButton, nominateManagerButton);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showOwnerNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField usernameField = new TextField("Enter Username");
        Button nominateButton = new Button("Send Owner Nomination Request", e -> {
            presenter.nominateOwner(storeId, usernameField.getValue());
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout(usernameField, nominateButton);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showManagerNominationDialog() {
        Dialog dialog = new Dialog();
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

        VerticalLayout dialogLayout = new VerticalLayout(usernameField, permissionSelect, nominateButton);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showPermissionManagementDialog(String managerUsername) {
        Dialog dialog = new Dialog();
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
