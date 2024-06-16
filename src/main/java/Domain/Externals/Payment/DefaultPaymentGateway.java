package Domain.Externals.Payment;

import Domain.Externals.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class DefaultPaymentGateway implements PaymentGateway {

    private static final String EXTERNAL_API_URL = "https://damp-lynna-wsep-1984852e.koyeb.app/";

    @Override
    public int processPayment(double amount, String creditCardNumber, String expirationDate, String cvv, String fullName, String id) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("action_type", "pay");
            params.put("amount", String.valueOf(amount));
            params.put("currency", "USD");
            params.put("card_number", creditCardNumber);
            params.put("month", getMonth(expirationDate));
            params.put("year", getYear(expirationDate));
            params.put("holder", fullName);
            params.put("ccv", cvv);
            params.put("id", id);

            String response = HttpUtils.sendPostRequest(EXTERNAL_API_URL, params);
            int transactionId = Integer.parseInt(response.trim());
            return (transactionId >= 10000 && transactionId <= 100000) ? transactionId : -1;
        } catch (Exception e) {
            System.out.println("Payment failed: " + e.getMessage());
            return -1;
        }
    }

    @Override
    public boolean cancelPayment(int transactionId) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("action_type", "cancel_pay");
            params.put("transaction_id", String.valueOf(transactionId));

            String response = HttpUtils.sendPostRequest(EXTERNAL_API_URL, params);
            int result = Integer.parseInt(response.trim());
            return result == 1;
        } catch (Exception e) {
            System.out.println("Cancel payment failed: " + e.getMessage());
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

    private String getMonth(String expirationDate) {
        return expirationDate.split("/")[0];
    }

    private String getYear(String expirationDate) {
        return expirationDate.split("/")[1];
    }
}
