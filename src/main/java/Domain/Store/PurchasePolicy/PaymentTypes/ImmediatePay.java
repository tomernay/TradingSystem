package src.main.java.Domain.Store.PurchasePolicy.PaymentTypes;

import src.main.java.Domain.Externals.Payment.CreditCard;
import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;
import src.main.java.Domain.Store.Store;

public class ImmediatePay extends PurchaseType {
    CreditCard credit;
    double fee;
    CreditCard store;
    public ImmediatePay(double fee,String receiver,String credit){
        this.credit=new CreditCard(credit);
        this.fee=fee;
        this.store=new CreditCard(receiver);
    }
    @Override
    public boolean pay( PaymentAdapter paymentAdapter) {
        if(fee!=0&&store!=null){
          return   paymentAdapter.pay(credit,store,fee).isSuccess();
        }
        return false;
    }

}
