package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.PaymentAdapter;

public abstract class PurchaseType {
    public abstract boolean pay( PaymentAdapter paymentAdapter);

}
