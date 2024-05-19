package src.main.java.Domain.Store.PurchasePolicy.PaymentTypes;

import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;

public class RandomPay extends PurchaseType {
    @Override
    public boolean pay(PaymentAdapter paymentAdapter) {
        return false;
    }

}
