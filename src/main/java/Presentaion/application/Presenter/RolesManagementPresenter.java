package Presentaion.application.Presenter;

import Presentaion.application.View.RolesManagementView;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import Utilities.SystemLogger;
import jakarta.servlet.http.Cookie;
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
    private String storeID;

    public RolesManagementPresenter(HttpServletRequest request) {
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
        this.roleFilter = new HashSet<>(Arrays.asList("Owner", "Manager", "Creator", "Subscriber"));
    }

    private String getTokenFromCookies() {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getUsernameFromCookies() {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void loadRoles(String storeID, Set<String> roleFilter, String username) {
        this.storeID = storeID;
        String token = getTokenFromCookies();
        String currentUsername = getUsernameFromCookies();
        SystemLogger.info("storeID: " + storeID + ", username: " + currentUsername + ", token: " + token);
        Response<Map<String, String>> response = userService.requestEmployeesStatus(storeID, currentUsername, token);
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
        loadRoles(storeID, roleFilter, username);
    }

    public void waiveOwnership() {
        String token = getTokenFromCookies();
        Response<String> response = userService.waiveOwnership(storeID, getUsernameFromCookies(), token);
        if (response.isSuccess()) {
            view.showSuccess("Ownership waived successfully");
        } else {
            view.showError(response.getMessage());
        }
    }

    public void nominateOwner(String subscriberUsername) {
        String token = getTokenFromCookies();
        userService.SendStoreOwnerNomination(storeID, getUsernameFromCookies(), subscriberUsername, token);
    }

    public void nominateManager(String subscriberUsername, Set<String> selectedPermissions) {
        String token = getTokenFromCookies();
        userService.SendStoreManagerNomination(storeID, getUsernameFromCookies(), subscriberUsername, selectedPermissions.stream().toList(), token);
    }

    public void setView(RolesManagementView view) {
        this.view = view;
    }

    public boolean hasRole(String role) {
        String token = getTokenFromCookies();
        if (role.equals("Owner")) {
            return storeService.isStoreOwner(storeID, token);
        } else if (role.equals("Subscriber")) {
            return storeService.isStoreSubscriber(storeID, token);
        } else if (role.equals("Creator")) {
            return storeService.isStoreCreator(storeID, token);
        } else if (role.equals("Manager")) {
            return storeService.isStoreManager(storeID, token);
        }
        return false;
    }

    public boolean hasPermission(String permission) {
        String token = getTokenFromCookies();
        return storeService.hasPermission(storeID, token, permission);
    }

    public Set<String> getManagerPermissions(String managerUsername) {
        String token = getTokenFromCookies();
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
        String token = getTokenFromCookies();
        String username = getUsernameFromCookies();
        Set<String> currentPermissions = getManagerPermissions(managerUsername);
        Set<String> permissionsToAdd = new HashSet<>(selectedPermissions);
        permissionsToAdd.removeAll(currentPermissions);
        Set<String> permissionsToRemove = new HashSet<>(currentPermissions);
        permissionsToRemove.removeAll(selectedPermissions);

        permissionsToAdd.forEach(permission -> storeService.addManagerPermissions(storeID, username, managerUsername, permission, token));
        permissionsToRemove.forEach(permission -> storeService.removeManagerPermissions(storeID, username, managerUsername, permission, token));
        view.showSuccess("Permissions updated for " + managerUsername);
    }

    public Set<String> getPermissions() {
        return storeService.getPermissionsList();
    }

    public void removeSubscription(String subscriber) {
        String token = getTokenFromCookies();
        String username = getUsernameFromCookies();
        Response<String> response = storeService.removeStoreSubscription(storeID, username, subscriber, token);
        if (response.isSuccess()) {
            view.showSuccess("Removed subscription successfully from " + username);
        } else {
            view.showError(response.getMessage());
        }
    }
}
