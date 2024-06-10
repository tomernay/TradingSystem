package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.CreditCard;
import Domain.Externals.Payment.PaymentAdapter;

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
