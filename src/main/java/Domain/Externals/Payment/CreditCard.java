package Domain.Externals.Payment;

public class CreditCard {
    private String creditCardNumber;
    private  String expirationDate;
    private String cvv;
    public CreditCard(String creditCardNumber){
        this.creditCardNumber=creditCardNumber;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }
}
