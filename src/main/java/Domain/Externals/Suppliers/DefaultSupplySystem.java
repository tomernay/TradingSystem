package Domain.Externals.Suppliers;

import Domain.Externals.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class DefaultSupplySystem implements SupplySystem {

    private static final String EXTERNAL_API_URL = "https://damp-lynna-wsep-1984852e.koyeb.app/";

    @Override
    public int orderSupply(Map<String, Integer> items, String deliveryAddress, String name) {
        try {
            String[] addressParts = deliveryAddress.split(", ");
            String address = addressParts[0];
            String city = addressParts[1];
            String country = addressParts[2];
            String zip = addressParts[3];

            Map<String, String> params = new HashMap<>();
            params.put("action_type", "supply");
            params.put("name", name);
            params.put("address", address);
            params.put("city", city);
            params.put("country", country);
            params.put("zip", zip);

            String response = HttpUtils.sendPostRequest(EXTERNAL_API_URL, params);
            int transactionId = Integer.parseInt(response.trim());
            return (transactionId >= 10000 && transactionId <= 100000) ? transactionId : -1;
        } catch (Exception e) {
            System.out.println("Supply order failed: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public boolean cancelSupply(int transactionId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("action_type", "cancel_supply");
            params.put("transaction_id", String.valueOf(transactionId));

            String response = HttpUtils.sendPostRequest(EXTERNAL_API_URL, params);
            int result = Integer.parseInt(response.trim());
            return result == 1;
        } catch (Exception e) {
            System.out.println("Cancel supply failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean handshake() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("action_type", "handshake");

            String response = HttpUtils.sendPostRequest(EXTERNAL_API_URL, params);
            return "OK".equals(response.trim());
        } catch (Exception e) {
            System.out.println("Handshake failed: " + e.getMessage());
            return false;
        }
    }
}
