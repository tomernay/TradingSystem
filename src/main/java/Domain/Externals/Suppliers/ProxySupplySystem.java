package Domain.Externals.Suppliers;

import java.util.Map;

public class ProxySupplySystem implements SupplySystem{

    private boolean isValidAddress(String address) {
        // Simulate address validation (replace with real implementation)
        return address != null && !address.trim().isEmpty();
    }

    @Override
    public int orderSupply(Map<String, Integer> items, String deliveryAddress, String name) {
        if (items == null || items.isEmpty()) {
            System.out.println("No items to supply.");
            return -1;
        }

        if (!isValidAddress(deliveryAddress)) {
            System.out.println("Invalid delivery address: " + deliveryAddress);
            return -1;
        }

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String item = entry.getKey();
            int quantity = entry.getValue();
            if (quantity <= 0) {
                System.out.println("Invalid quantity for item: " + item);
                return -1;
            }
            // Simulate order processing
            System.out.println("Ordering " + quantity + " units of " + item + " to be delivered at " + deliveryAddress);
        }

        return 1;
    }

    @Override
    public boolean cancelSupply(int transactionId) {
        return true;
    }

    @Override
    public boolean handshake() {
        return true;
    }
}
