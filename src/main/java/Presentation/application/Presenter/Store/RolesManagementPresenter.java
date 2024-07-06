package Presentation.application.Presenter.Store;

import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.RolesManagementView;
import Presentation.application.View.UtilitiesView.Broadcaster;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RolesManagementPresenter {

    private RolesManagementView view;
    private final StoreService storeService;
    private final UserService userService;
    private final HttpServletRequest request;
    private Set<String> roleFilter;
    private Integer storeID;

    public RolesManagementPresenter(HttpServletRequest request) {
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
        this.roleFilter = new HashSet<>(Arrays.asList("OWNER", "MANAGER", "CREATOR", "SUBSCRIBER"));
    }

    public void loadRoles(Integer storeID, Set<String> roleFilter, String username) {
        this.storeID = storeID;
        String token = CookiesHandler.getTokenFromCookies(request);
        String currentUsername = CookiesHandler.getUsernameFromCookies(request);
        Response<Map<String, String>> response = userService.requestEmployeesStatus(storeID, currentUsername, token);
        if (response.isSuccess()) {
            Map<String, String> rolesMap = response.getData();
            List<Map.Entry<String, String>> roles = rolesMap.entrySet().stream()
                    .filter(entry -> roleFilter == null || roleFilter.isEmpty() || roleFilter.contains(entry.getValue()))
                    .filter(entry -> username == null || username.isEmpty() || entry.getKey().contains(username))
                    .collect(Collectors.toList());
            view.showRoles(roles);
        } else {
            view.showError("Failed to load roles");
        }
    }

    public void searchByUsername(Integer storeId, String username) {
        loadRoles(storeId, roleFilter, username);
    }

    public void waiveOwnership(Integer storeID) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.waiveOwnership(storeID, CookiesHandler.getUsernameFromCookies(request), token);
        if (response.isSuccess()) {
            view.showSuccess("Ownership waived successfully");
        } else {
            view.showError(response.getMessage());
        }
    }

    public void nominateOwner(Integer storeID, String subscriberUsername) {
        String token = CookiesHandler.getTokenFromCookies(request);
        userService.SendOwnerNominationRequest(storeID, CookiesHandler.getUsernameFromCookies(request), subscriberUsername, token);
        view.showSuccess("Owner nomination request sent");
    }

    public void nominateManager(Integer storeID, String subscriberUsername, Set<String> selectedPermissions) {
        String token = CookiesHandler.getTokenFromCookies(request);
        userService.SendManagerNominationRequest(storeID, CookiesHandler.getUsernameFromCookies(request), subscriberUsername, selectedPermissions.stream().toList(), token);
        view.showSuccess("Manager nomination request sent");
    }

    public void setView(RolesManagementView view) {
        this.view = view;
    }

    public boolean hasRole(Integer storeID, String role) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        if (role.equals("Owner")) {
            return storeService.isStoreOwner(storeID, username);
        } else if (role.equals("Subscriber")) {
            return storeService.isStoreSubscriber(storeID, username);
        } else if (role.equals("Creator")) {
            return storeService.isStoreCreator(storeID, username);
        } else if (role.equals("Manager")) {
            return storeService.isStoreManager(storeID, username);
        }
        return false;
    }

    public boolean hasPermission(Integer storeID, String permission) {
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.hasPermission(storeID, token, permission);
    }

    public Set<String> getManagerPermissions(Integer storeID, String managerUsername) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<Map<String, List<String>>> response = storeService.requestManagersPermissions(storeID);
        if (response.isSuccess()) {
            Map<String, List<String>> managersPermissions = response.getData();
            return new HashSet<>(managersPermissions.get(managerUsername));
        } else {
            view.showError("Failed to load manager's permissions");
            return new HashSet<>();
        }
    }

    public void updateManagerPermissions(Integer storeID, String managerUsername, Set<String> selectedPermissions) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Set<String> currentPermissions = getManagerPermissions(storeID, managerUsername);
        Set<String> permissionsToAdd = new HashSet<>(selectedPermissions);
        permissionsToAdd.removeAll(currentPermissions);
        Set<String> permissionsToRemove = new HashSet<>(currentPermissions);
        permissionsToRemove.removeAll(selectedPermissions);

        permissionsToAdd.forEach(permission -> storeService.addManagerPermissions(storeID, username, managerUsername, permission, token));
        permissionsToRemove.forEach(permission -> storeService.removeManagerPermissions(storeID, username, managerUsername, permission, token));
        Broadcaster.broadcast("your permission has been updated",managerUsername);
        view.showSuccess("Permissions updated for " + managerUsername);
    }

    public Set<String> getPermissions() {
        return storeService.getPermissionsList();
    }

    public void removeSubscription(Integer storeID, String subscriber) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<String> response = storeService.removeStoreSubscription(storeID, username, subscriber, token);
        Broadcaster.broadcast("you subscription has been removed from store:"+String.valueOf(storeID),subscriber);
        if (response.isSuccess()) {
            view.showSuccess("Removed subscription successfully from " + subscriber);
        } else {
            view.showError(response.getMessage());
        }
    }

    public boolean isNominatorOf(Integer storeId, String manager) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        return storeService.isNominatorOf(storeId, username, manager);
    }

    public boolean isActiveStore(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.isStoreActive(storeId, username, token);
    }

    public boolean isLoggedIn() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
