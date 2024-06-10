package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.PaymentAdapter;

public class RandomPay extends PurchaseType {
    @Override
    public boolean pay(PaymentAdapter paymentAdapter) {
        return false;
    }

}
