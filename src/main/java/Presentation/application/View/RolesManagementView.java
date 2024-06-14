package Presentation.application.View;

import Presentation.application.Presenter.RolesManagementPresenter;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@PageTitle("Roles Management")
@Route(value = "roles-management", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class RolesManagementView extends VerticalLayout {
    private RolesManagementPresenter presenter;
    private Grid<Map.Entry<String, String>> grid;
    private MultiSelectListBox<String> roleFilter;
    private TextField usernameFilter;

    public RolesManagementView(RolesManagementPresenter presenter) {
        addClassName("roles-management-view");
        this.presenter = presenter;
        this.presenter.setView(this);
        initUI();
    }

    private void initUI() {
        grid = new Grid<>();
        grid.addColumn(Map.Entry::getKey).setHeader("Username");
        grid.addColumn(Map.Entry::getValue).setHeader("Role");


        // Initialize UI components in the constructor
        MultiSelectComboBox<String> roleFilter = new MultiSelectComboBox<>();
        roleFilter.setLabel("Filter by Role");
        roleFilter.setItems("Owner", "Manager", "Creator", "Subscriber");
        roleFilter.setPlaceholder("Select roles...");
        roleFilter.setClearButtonVisible(true);
        roleFilter.getElement().setAttribute("multiple", true); // Set multiple attribute to enable multi-select

        Button loadRolesButton = new Button("Load Roles", event -> {
            // Replace "store-id" with the actual store ID
            String storeID = "store-id"; // You may want to replace this with a dynamic value

            // Get the selected roles from the roleFilter MultiSelectListBox
            Set<String> selectedRoles = roleFilter.getValue();

            // Pass the selected roles and an empty string for the username
            presenter.loadRoles(storeID, selectedRoles, "");
        });
        usernameFilter = new TextField("Search by Username");
        usernameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        usernameFilter.addValueChangeListener(event -> presenter.searchByUsername(event.getValue()));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        if (presenter.hasRole("Owner") || presenter.hasRole("Creator")) {
            Button addButton = new Button("+ Nominate", e -> showNominationDialog());
            buttonLayout.add(addButton);
        }
        if (presenter.hasRole("Owner")) {
            Button waiveOwnershipButton = new Button("Waive Ownership", e -> presenter.waiveOwnership());
            buttonLayout.add(waiveOwnershipButton);
        }

        grid.addItemClickListener(event -> {
            Map.Entry<String, String> item = event.getItem();
            String role = item.getValue();
            String username = item.getKey();

            if ("Manager".equals(role) && (presenter.hasRole("Owner") || presenter.hasRole("Creator") || (presenter.hasRole("Manager")) && presenter.hasPermission("EDIT_PERMISSIONS"))) {
                showPermissionManagementDialog(username);
            }

            if ("Subscriber".equals(role) && (presenter.hasRole("Owner") || presenter.hasRole("Creator") || (presenter.hasRole("Manager")) && presenter.hasPermission("REMOVE_STORE_SUBSCRIPTION"))) {
                showSubscriberDialog(username);
            }
        });

        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        // Add components to the layout
        add(buttonLayout, roleFilter, usernameFilter, grid);

        // Add UI components to the layout
        add(roleFilter, usernameFilter);
        add(loadRolesButton, grid);
    }

    public void setPresenter(RolesManagementPresenter presenter) {
        this.presenter = presenter;
    }

    private void showSubscriberDialog(String username) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        Button removeSubscriptionButton = new Button("Remove Subscription", e -> {
            presenter.removeSubscription(username);
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(removeSubscriptionButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        Button nominateOwnerButton = new Button("Nominate Owner", e -> showOwnerNominationDialog());
        Button nominateManagerButton = new Button("Nominate Manager", e -> showManagerNominationDialog());

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(nominateOwnerButton, nominateManagerButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showOwnerNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField usernameField = new TextField("Enter Username");
        Button nominateButton = new Button("Send Owner Nomination Request", e -> {
            presenter.nominateOwner(usernameField.getValue());
            // Logic to send owner nomination request
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(usernameField, nominateButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showManagerNominationDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField usernameField = new TextField("Enter Username");
        MultiSelectListBox<String> permissionSelect = new MultiSelectListBox<>();
        permissionSelect.setItems(presenter.getPermissions()); // Add your list of permissions here

        Button nominateButton = new Button("Send Manager Nomination Request", e -> {
            // Get the nominated username and selected permissions
            String nominatedUsername = usernameField.getValue();
            Set<String> selectedPermissions = permissionSelect.getValue();
            presenter.nominateManager(nominatedUsername, selectedPermissions);
            // Logic to send manager nomination request
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(usernameField, permissionSelect, nominateButton);

        dialog.add(dialogLayout);
        dialog.open();
    }



    private void showPermissionManagementDialog(String managerUsername) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        // Create components for the dialog
        MultiSelectListBox<String> permissionSelect = new MultiSelectListBox<>();
        permissionSelect.setItems("Permission 1", "Permission 2", "Permission 3"); // Add your list of permissions here

        // Fetch current permissions and set them as selected
        Set<String> currentPermissions = presenter.getManagerPermissions(managerUsername);
        permissionSelect.setValue(currentPermissions);

        Button saveButton = new Button("Save", e -> {
            Set<String> selectedPermissions = permissionSelect.getValue();
            presenter.updateManagerPermissions(managerUsername, selectedPermissions);
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Manage Permissions for: " + managerUsername), permissionSelect, saveButton);

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
        // Show error message
    }
    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }
}
