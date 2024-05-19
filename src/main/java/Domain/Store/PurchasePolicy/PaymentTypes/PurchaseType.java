package src.main.java.Domain.Store.PurchasePolicy.PaymentTypes;

import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Domain.Store.Store;

public abstract class PurchaseType {
    public abstract boolean pay( PaymentAdapter paymentAdapter);

}
