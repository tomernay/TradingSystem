package Domain.Externals.Payment;

import Utilities.Response;
import Utilities.SystemLogger;

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
            SystemLogger.error("[ERROR] Credit card cannot be null");
            return Response.error("Credit card cannot be null", null);
        }
        if(fee<=0){
            SystemLogger.error("[ERROR] Fee must be positive");
            return Response.error("fee must be positive", null);
        }
        else if(!isValidVisa(buyer.getCreditCardNumber())){
            SystemLogger.error("[ERROR] Invalid credit card number");
            return Response.error("invalid credit card number", null);
        }
        else if(Objects.equals(buyer.getCreditCardNumber(), receiver.getCreditCardNumber())){
            SystemLogger.error("[ERROR] Can't pay to yourself");
            return Response.error("can't pay to yourself", null);
        }
        SystemLogger.info("[SUCCESS] Payment successful");
        return Response.success("paid", null);
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
