package Domain.Externals.Payment;

public interface PaymentGateway {
    boolean processPayment(double amount, String creditCardNumber, String expirationDate, String cvv, String fullName);
}
