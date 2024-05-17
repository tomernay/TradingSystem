package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.Store;

public abstract class PurchaseType {
    public abstract boolean pay( PaymentAdapter paymentAdapter);

}
