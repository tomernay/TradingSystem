package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;

public class RandomPay extends PurchaseType {
    @Override
    public boolean pay(PaymentAdapter paymentAdapter) {
        return false;
    }

}
