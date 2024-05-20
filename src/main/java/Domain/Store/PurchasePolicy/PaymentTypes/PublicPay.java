package src.main.java.Domain.Store.PurchasePolicy.PaymentTypes;

import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Domain.Store.Store;

import java.util.Date;
import java.util.HashMap;

public class PublicPay extends PurchaseType {
    public <V, K> PublicPay(double maxFee, Date date, Store store, String card, HashMap<K,V> kvHashMap) {
        super();
    }

    @Override
    public boolean pay(PaymentAdapter paymentAdapter) {
        return false;
    }

}
