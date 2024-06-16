package Domain.Externals.Suppliers;

import java.util.Map;

public class DefaultSupplySystem implements SupplySystem {
    @Override
    public boolean orderSupply(Map<String, Integer> items, String deliveryAddress) {
        if (items == null || items.isEmpty()) {
            System.out.println("No items to supply.");
            return false;
        }

        if (!isValidAddress(deliveryAddress)) {
            System.out.println("Invalid delivery address: " + deliveryAddress);
            return false;
        }

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String item = entry.getKey();
            int quantity = entry.getValue();
            if (quantity <= 0) {
                System.out.println("Invalid quantity for item: " + item);
                return false;
            }
            // Simulate order processing
            System.out.println("Ordering " + quantity + " units of " + item + " to be delivered at " + deliveryAddress);
        }

        return true;
    }

    private boolean isValidAddress(String address) {
        // Simulate address validation (replace with real implementation)
        return address != null && !address.trim().isEmpty();
    }
}
