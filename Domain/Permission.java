package Domain;

public class Permission {
    private boolean canViewContent;
    private boolean canEditContent;
    private boolean canManageSubscribers;
    private boolean canManageStoreSettings;
    private boolean canViewStoreAnalytics;
    private boolean canManageOrders;
    private boolean canManageStoreDesign;
    private boolean canManageDiscounts;
    private boolean canManageInventory;
    private boolean canAccessDashboard;

    public Permission(boolean canViewContent, boolean canEditContent, boolean canManageSubscribers,
                       boolean canManageStoreSettings, boolean canViewStoreAnalytics, boolean canManageOrders,
                       boolean canManageStoreDesign, boolean canManageDiscounts, boolean canManageInventory,
                       boolean canAccessDashboard) {
        this.canViewContent = canViewContent;
        this.canEditContent = canEditContent;
        this.canManageSubscribers = canManageSubscribers;
        this.canManageStoreSettings = canManageStoreSettings;
        this.canViewStoreAnalytics = canViewStoreAnalytics;
        this.canManageOrders = canManageOrders;
        this.canManageStoreDesign = canManageStoreDesign;
        this.canManageDiscounts = canManageDiscounts;
        this.canManageInventory = canManageInventory;
        this.canAccessDashboard = canAccessDashboard;
    }

    public boolean canViewContent() {
        return canViewContent;
    }

    public void setCanViewContent(boolean canViewContent) {
        this.canViewContent = canViewContent;
    }

    public boolean canEditContent() {
        return canEditContent;
    }

    public void setCanEditContent(boolean canEditContent) {
        this.canEditContent = canEditContent;
    }

    public boolean canManageSubscribers() {
        return canManageSubscribers;
    }

    public void setCanManageSubscribers(boolean canManageSubscribers) {
        this.canManageSubscribers = canManageSubscribers;
    }

    public boolean canManageStoreSettings() {
        return canManageStoreSettings;
    }

    public void setCanManageStoreSettings(boolean canManageStoreSettings) {
        this.canManageStoreSettings = canManageStoreSettings;
    }

    public boolean canViewStoreAnalytics() {
        return canViewStoreAnalytics;
    }

    public void setCanViewStoreAnalytics(boolean canViewStoreAnalytics) {
        this.canViewStoreAnalytics = canViewStoreAnalytics;
    }

    public boolean canManageOrders() {
        return canManageOrders;
    }

    public void setCanManageOrders(boolean canManageOrders) {
        this.canManageOrders = canManageOrders;
    }

    public boolean canManageStoreDesign() {
        return canManageStoreDesign;
    }

    public void setCanManageStoreDesign(boolean canManageStoreDesign) {
        this.canManageStoreDesign = canManageStoreDesign;
    }

    public boolean canManageDiscounts() {
        return canManageDiscounts;
    }

    public void setCanManageDiscounts(boolean canManageDiscounts) {
        this.canManageDiscounts = canManageDiscounts;
    }

    public boolean canManageInventory() {
        return canManageInventory;
    }

    public void setCanManageInventory(boolean canManageInventory) {
        this.canManageInventory = canManageInventory;
    }

    public boolean canAccessDashboard() {
        return canAccessDashboard;
    }

    public void setCanAccessDashboard(boolean canAccessDashboard) {
        this.canAccessDashboard = canAccessDashboard;
    }
}
