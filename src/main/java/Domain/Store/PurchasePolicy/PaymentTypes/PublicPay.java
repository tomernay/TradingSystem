package src.main.java.Domain.Store.PurchasePolicy.PaymentTypes;

import src.main.java.Domain.Externals.Payment.PaymentAdapter;

public class PublicPay extends PurchaseType {
    @Override
    public boolean pay(PaymentAdapter paymentAdapter) {
        return false;
    }

}
