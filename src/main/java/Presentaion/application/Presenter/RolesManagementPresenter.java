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
        ServiceInitializer serviceInitializer = new ServiceInitializer();
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
        userService.waiveOwnership(storeID, username, token);
        // Logic to waive ownership for the specified username
        // Only owners should have access to this functionality
    }

// Implement logic to handle permissions and subscriber management

    public void managePermissions(String username) {
        // Logic to manage permissions for the specified username
    }

    public void unsubscribe(String username) {
        // Logic to unsubscribe the specified username
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
}
