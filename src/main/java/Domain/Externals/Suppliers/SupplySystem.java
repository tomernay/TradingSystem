package Domain.Externals.Suppliers;

import java.util.Map;

public interface SupplySystem {
    boolean orderSupply(Map<String, Integer> items, String deliveryAddress);
}
