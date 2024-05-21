package src.main.java.Domain.Externals.Payment;

import src.main.java.Domain.Store.Store;
import src.main.java.Utilities.Response;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultPay extends PaymentAdapter {
    private static final String VISA_REGEX = "^4[0-9]{12}(?:[0-9]{3})?$"; // Visa regular expression

    public DefaultPay(String name) {
        super(name);
    }

    @Override
    public Response<String> pay(CreditCard buyer, CreditCard receiver, double fee) {
        if(buyer==null||receiver==null){
            return new Response<String>(false, "invalid credit card number");
        }
        if(fee<=0){
            return new Response<String>(false, "fee must be positive");
        }
        else if(!isValidVisa(buyer.getCreditCardNumber())){
            return new Response<String>(false, "invalid credit card number");
        }
        else if(Objects.equals(buyer.getCreditCardNumber(), receiver.getCreditCardNumber())){
            return new Response<String>(false, "can't pay to yourself");
        }
        return new Response<>(true, "paid");
      //  return isValidVisa(buyer.getCreditCardNumber())&& !Objects.equals(buyer.getCreditCardNumber(), receiver.getCreditCardNumber())&&fee>0;
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
