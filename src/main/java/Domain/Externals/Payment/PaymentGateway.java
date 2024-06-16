package Domain.Externals.Payment;

public interface PaymentGateway {
    int processPayment(double amount, String creditCardNumber, String expirationDate, String cvv, String fullName, String id);
    boolean cancelPayment(int transactionId);
    boolean handshake();
}
