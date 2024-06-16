package Domain.Externals.Suppliers;

import java.util.Map;

public interface SupplySystem {
    int orderSupply(Map<String, Integer> items, String deliveryAddress, String name);
    boolean cancelSupply(int transactionId);
    boolean handshake();
}
