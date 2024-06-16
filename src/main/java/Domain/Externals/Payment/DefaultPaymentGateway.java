package Domain.Externals.Payment;

public class DefaultPaymentGateway implements PaymentGateway{

    @Override
    public boolean processPayment(double amount, String creditCardNumber, String expirationDate, String cvv, String fullName) {
        if (amount <= 0) {
            System.out.println("Invalid amount: " + amount);
            return false;
        }

        if (!isValidCreditCardNumber(creditCardNumber)) {
            System.out.println("Invalid credit card number: " + creditCardNumber);
            return false;
        }

        if (!isValidExpirationDate(expirationDate)) {
            System.out.println("Invalid expiration date: " + expirationDate);
            return false;
        }

        if (!isValidCVV(cvv)) {
            System.out.println("Invalid CVV: " + cvv);
            return false;
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            System.out.println("Invalid full name: " + fullName);
            return false;
        }

        // Simulate successful payment
        System.out.println("Payment of $" + amount + " processed successfully for " + fullName);
        return true;
    }

    private boolean isValidCreditCardNumber(String creditCardNumber) {
        // Basic validation for credit card number length
        return creditCardNumber != null && creditCardNumber.length() == 16 && creditCardNumber.matches("\\d+");
    }

    private boolean isValidExpirationDate(String expirationDate) {
        // Basic validation for expiration date format MM/YY
        return expirationDate != null && expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    private boolean isValidCVV(String cvv) {
        // Basic validation for CVV length and digits
        return cvv != null && cvv.length() == 3 && cvv.matches("\\d+");
    }

}
