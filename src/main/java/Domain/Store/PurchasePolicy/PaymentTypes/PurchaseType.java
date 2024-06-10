package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.PaymentAdapter;
import Utilities.Response;

public abstract class PurchaseType {
    public abstract Response<String> pay(PaymentAdapter paymentAdapter);

}
