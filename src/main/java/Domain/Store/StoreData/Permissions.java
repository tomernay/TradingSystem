package Domain.Store.StoreData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum Permissions {
    // Permissions examples for now. Will be updated later.
    ADD_PRODUCT,
    REMOVE_PRODUCT,
    VIEW_STORE_DETAILS,
    EDIT_PRODUCT,
    REMOVE_CATEGORY,
    VIEW_PRODUCT,
    ADD_DISCOUNT,
    REMOVE_DISCOUNT,
    EDIT_DISCOUNT,
    EDIT_PERMISSIONS,
    VIEW_STORE_PURCHASE_HISTORY,
    VIEW_STORE_MANAGERS,
    VIEW_STORE_OWNERS,
    VIEW_STORE_DISCOUNTS,
    VIEW_STORE_PURCHASES,
    VIEW_STORE_MESSAGES,
    REMOVE_STORE_SUBSCRIPTION;

    public static List<Permissions> convertStringList(List<String> permissions) {
        List<Permissions> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            permissionList.add(Permissions.valueOf(permission));
        }
        return permissionList;
    }

    public static List<String> convertPermissionList(List<Permissions> permissions) {
        List<String> permissionList = new ArrayList<>();
        for (Permissions permission : permissions) {
            permissionList.add(permission.toString());
        }
        return permissionList;
    }

    public static boolean exists(String permission) {
        try {
            Permissions.valueOf(permission);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public static Set<String> getPermissions() {
        return EnumSet.allOf(Permissions.class).stream().map(Enum::name).collect(Collectors.toSet());
    }
}
