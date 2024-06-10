package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.CreditCard;
import Domain.Externals.Payment.PaymentAdapter;
import Utilities.Response;

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
    public Response<String> pay(PaymentAdapter paymentAdapter) {
        if(fee!=0){
          return   paymentAdapter.pay(credit,store,fee);
        }
        return new Response<>(false,"fee cant be zero");
    }

}
