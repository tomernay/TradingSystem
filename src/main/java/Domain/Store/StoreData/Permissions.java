package Domain.Store.StoreData;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum Permissions {
    MANAGE_PRODUCTS, //Add + remove + edit products
    MANAGE_DISCOUNTS_POLICIES, //Add + remove + edit discounts and purchase policies
    MANAGE_MANAGERS_PERMISSIONS, //Add + remove + edit permissions of managers
    VIEW_PURCHASE_HISTORY, //View purchase history of store
    VIEW_DISCOUNTS_POLICIES, //View discounts and purchase policies of store
    VIEW_PRODUCTS, //View products
    VIEW_STORE_STAFF_INFO, //View store staff list
    REMOVE_STORE_SUBSCRIPTION; //Remove store subscription

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
