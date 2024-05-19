package src.main.java.Domain.Store.PurchasePolicy.PaymentTypes;

import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;
import src.main.java.Domain.Store.Store;

public class ImmediatePay extends PurchaseType {
    String credit;
    double fee;
    Store store;
    public ImmediatePay(double fee,Store s,String credit){
        this.credit=credit;
        this.fee=fee;
        this.store=s;
    }
    @Override
    public boolean pay( PaymentAdapter paymentAdapter) {
        if(fee!=0&&store!=null){
          return   paymentAdapter.pay(credit,store,fee);
        }
        return false;
    }

}
