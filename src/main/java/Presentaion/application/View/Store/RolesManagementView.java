package Presentaion.application.View.Store;

import java.util.Collection;
import java.util.Map;

public interface RolesManagementView {
    void showRoles(Collection<Map.Entry<String, String>> roles);
    void showError(String message);
    void showSuccess(String message);
}
