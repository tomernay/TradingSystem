package Presentaion.application.Presenter;

import Service.UserService;
import Utilities.Response;
import org.springframework.beans.factory.annotation.Autowired;
import Presentaion.application.View.Store.RolesManagementView;
import Service.ServiceInitializer;
import Service.StoreService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RolesManagementPresenter {
    private RolesManagementView view;
    private StoreService storeService;
    private UserService userService;
    private Set<String> roleFilter;
    private String storeID;
    private final String token = ""; //NOT IMPLEMENTED
    @Autowired
    public RolesManagementPresenter(@Qualifier("rolesManagementViewImpl") RolesManagementView view) {
        this.view = view;
        ServiceInitializer serviceInitializer = ServiceInitializer.getInstance();
        this.storeService = serviceInitializer.getStoreService();
        this.userService = serviceInitializer.getUserService();
        this.roleFilter = new HashSet<>(Arrays.asList("Owner", "Manager", "Creator", "Subscriber"));
    }

    public void loadRoles(String storeID, Set<String> roleFilter, String username) {
        this.storeID = storeID;
        Response<Map<String, String>> response = userService.requestEmployeesStatus(storeID, username, token);
        if (response.isSuccess()) {
            Map<String, String> rolesMap = response.getData();
            List<Map.Entry<String, String>> roles = rolesMap.entrySet().stream()
                    .filter(entry -> roleFilter.contains(entry.getValue()))
                    .filter(entry -> username.isEmpty() || entry.getKey().contains(username))
                    .collect(Collectors.toList());
            view.showRoles(roles);
        } else {
            view.showError("Failed to load roles");
        }
    }

    public void searchByUsername(String username) {
        // Logic to filter roles based on the entered username
        loadRoles(storeID, roleFilter, username);
    }

    public void waiveOwnership(String username) {
        Response<String> response = userService.waiveOwnership(storeID, username, token);
        if (response.isSuccess()) {
            view.showSuccess("Ownership waived successfully");
        } else {
            view.showError(response.getMessage());
        }
        // Logic to waive ownership for the specified username
        // Only owners should have access to this functionality
    }

// Implement logic to handle permissions and subscriber management

    public void managePermissions(String username) {
        // Logic to manage permissions for the specified username
    }

    // Modify the filterRoles method to accept a Set<String> of roles
    public void filterRoles(Set<String> roles) {
        // Update the roleFilter with the provided roles
        this.roleFilter = roles;

        // Call loadRoles with the updated roleFilter
        loadRoles(storeID, roleFilter, "");
    }


    public void nominateOwner(String currentUsername, String subscriberUsername) {
        userService.SendStoreOwnerNomination(storeID, currentUsername, subscriberUsername, token);
        // Logic to add a new owner
    }

    public void nominateManager(String currentUsername, String subscriberUsername, Set<String> selectedPermissions) {
        userService.SendStoreManagerNomination(storeID, currentUsername, subscriberUsername, selectedPermissions.stream().toList(),token);
        // Logic to add a new manager
    }

    public void onRoleClicked(Map.Entry<String, String> role) {
        if ("Manager".equals(role.getValue())) {
            // Logic to show manager's permissions
        } else if ("Subscriber".equals(role.getValue())) {
            // Logic to handle subscriber
        }
    }

    public void setView(RolesManagementView view) {
        this.view = view;
    }

    public boolean hasRole(String role) {
        if (role.equals("Owner")) {
            return storeService.isStoreOwner(storeID, "");
        }
        else if (role.equals("Subscriber")) {
            return storeService.isStoreSubscriber(storeID, "");
        }
        else if (role.equals("Creator")) {
            return storeService.isStoreCreator(storeID, "");
        }
        else if (role.equals("Manager")) {
            return storeService.isStoreManager(storeID, "");
        }
        return false;
    }

    public boolean hasPermission(String permission) {
        return storeService.hasPermission(storeID, "", permission);
    }

    public Set<String> getManagerPermissions(String managerUsername) {
        Response<Map<String, List<String>>> response = storeService.requestManagersPermissions(storeID);
        if (response.isSuccess()) {
            Map<String, List<String>> managersPermissions = response.getData();
            return new HashSet<>(managersPermissions.get(managerUsername));
        } else {
            view.showError("Failed to load manager's permissions");
            return new HashSet<>();
        }
    }

    public void updateManagerPermissions(String managerUsername, Set<String> selectedPermissions) {
        Set<String> currentPermissions = getManagerPermissions(managerUsername);
        Set<String> permissionsToAdd = new HashSet<>(selectedPermissions);
        permissionsToAdd.removeAll(currentPermissions);
        Set<String> permissionsToRemove = new HashSet<>(currentPermissions);
        permissionsToRemove.removeAll(selectedPermissions);

        permissionsToAdd.forEach(permission -> storeService.addManagerPermissions(storeID, "", managerUsername, permission, token));
        permissionsToRemove.forEach(permission -> storeService.removeManagerPermissions(storeID, "", managerUsername, permission, token));
        view.showSuccess("Permissions updated for " + managerUsername);
    }

    public Set<String> getPermissions() {
        return storeService.getPermissionsList();
    }

    public void removeSubscription(String username) {
        Response<String> response = storeService.removeStoreSubscription(storeID, username);
        if (response.isSuccess()) {
            view.showSuccess("Removed subscription successfully from " + username);
        } else {
            view.showError(response.getMessage());
        }
    }
}
