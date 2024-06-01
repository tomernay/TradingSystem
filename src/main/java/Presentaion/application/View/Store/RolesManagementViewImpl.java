package Presentaion.application.View.Store;

import Presentaion.application.Presenter.RolesManagementPresenter;
import Presentaion.application.View.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@PageTitle("Roles Management")
@Route(value = "roles-management", layout = MainLayout.class)
@Component
@Qualifier("rolesManagementViewImpl")
public class RolesManagementViewImpl extends VerticalLayout implements RolesManagementView {
    private final Grid<Map.Entry<String, String>> grid;
    private final RolesManagementPresenter presenter;
    private MultiSelectListBox<String> roleFilter;
    private TextField usernameFilter;
    private Button addButton;

    public RolesManagementViewImpl() {
        this.presenter = new RolesManagementPresenter(this);
        grid = new Grid<>();
        grid.addColumn(Map.Entry::getKey).setHeader("Username");
        grid.addColumn(Map.Entry::getValue).setHeader("Role");

        Button loadRolesButton = new Button("Load Roles", event -> {
            // Replace "store-id" with the actual store ID
            String storeID = "store-id"; // You may want to replace this with a dynamic value

            // Get the selected roles from the roleFilter MultiSelectListBox
            Set<String> selectedRoles = roleFilter.getValue();

            // Pass the selected roles and an empty string for the username
            presenter.loadRoles(storeID, selectedRoles, "");
        });
        // Initialize UI components in the constructor
        MultiSelectComboBox<String> roleFilter = new MultiSelectComboBox<>();
        roleFilter.setLabel("Filter by Role");
        roleFilter.setItems("Owner", "Manager", "Creator", "Subscriber");
        roleFilter.setPlaceholder("Select roles...");
        roleFilter.setClearButtonVisible(true);
        roleFilter.getElement().setAttribute("multiple", true); // Set multiple attribute to enable multi-select

        usernameFilter = new TextField("Search by Username");
        usernameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        usernameFilter.addValueChangeListener(event -> presenter.searchByUsername(event.getValue()));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        if (hasRole("Owner") || hasRole("Creator")) {
            Button addButton = new Button("+ Nominate", e -> showNominationDialog(MainLayout.getSubscriberDetails().getUsername()));
            buttonLayout.add(addButton);
        }
        if (hasRole("Owner")) {
            Button waiveOwnershipButton = new Button("Waive Ownership", e -> presenter.waiveOwnership(MainLayout.getSubscriberDetails().getUsername()));
            buttonLayout.add(waiveOwnershipButton);
        }

        grid.addItemClickListener(event -> {
            Map.Entry<String, String> item = event.getItem();
            String role = item.getValue();
            String username = item.getKey();

            if ("Manager".equals(role) && (hasRole("Owner") || hasRole("Creator") || (hasRole("Manager")) && hasPermission("EDIT_PERMISSIONS"))) {
                showPermissionManagementDialog(username);
            }

            if ("Subscriber".equals(role) && (hasRole("Owner") || hasRole("Creator") || (hasRole("Manager")) && hasPermission("REMOVE_STORE_SUBSCRIPTION"))) {
                showSubscriberDialog(username);
            }
        });

        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        // Add components to the layout
        add(buttonLayout, roleFilter, usernameFilter, grid);

        // Add UI components to the layout
        add(roleFilter, usernameFilter);
        add(loadRolesButton, grid);
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

    private boolean hasPermission(String permission) {
        return presenter.hasPermission(permission);
    }

    private boolean hasRole(String role) {
        return presenter.hasRole(role);
    }

    private void showNominationDialog(String username) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        Button nominateOwnerButton = new Button("Nominate Owner", e -> showOwnerNominationDialog(username));
        Button nominateManagerButton = new Button("Nominate Manager", e -> showManagerNominationDialog(username));

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(nominateOwnerButton, nominateManagerButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showOwnerNominationDialog(String username) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField usernameField = new TextField("Enter Username");
        Button nominateButton = new Button("Send Owner Nomination Request", e -> {
            presenter.nominateOwner(username, usernameField.getValue());
            // Logic to send owner nomination request
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(usernameField, nominateButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void showManagerNominationDialog(String username) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField usernameField = new TextField("Enter Username");
        MultiSelectListBox<String> permissionSelect = new MultiSelectListBox<>();
        permissionSelect.setItems(presenter.getPermissions()); // Add your list of permissions here

        Button nominateButton = new Button("Send Manager Nomination Request", e -> {
            // Get the nominated username and selected permissions
            String nominatedUsername = usernameField.getValue();
            Set<String> selectedPermissions = permissionSelect.getValue();
            presenter.nominateManager(username, nominatedUsername, selectedPermissions);
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


    public void showWaiveOwnershipConfirmation() {
        // Implement logic to show a confirmation dialog for waiving ownership
    }

    public void notify(String message) {
        Notification.show(message);
    }

    @Override
    public void showRoles(Collection<Map.Entry<String, String>> roles) {
        grid.setItems(roles);
    }

    @Override
    public void showError(String message) {
        // Show error message
    }
    @Override
    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
        // Show error message
    }
}
