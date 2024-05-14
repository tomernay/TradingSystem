package Domain.Externals.Payment;

import Domain.Store.Store;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultPay extends PaymentAdapter {
    private static final String VISA_REGEX = "^4[0-9]{12}(?:[0-9]{3})?$"; // Visa regular expression

    public DefaultPay(String name) {
        super(name);
    }

    @Override
    public boolean pay(String credit, Store s, double fee) {

     return isValidVisa(credit);
    }

    /**
     * check if th card is a valid visa
     * @param creditCardNumber
     * @return bool
     */
    private   boolean isValidVisa(String creditCardNumber) {
        Pattern pattern = Pattern.compile(VISA_REGEX);
        Matcher matcher = pattern.matcher(creditCardNumber);
        return matcher.matches();
    }
}
