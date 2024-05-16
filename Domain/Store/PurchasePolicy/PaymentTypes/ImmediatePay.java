package Domain.Store.PurchasePolicy.PaymentTypes;

<<<<<<< Updated upstream
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;
import Domain.Store.Store;

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
=======
import Domain.Store.PurchasePolicy.PaymentTypes.PurchaseType;

public class ImmediatePay extends PurchaseType {
>>>>>>> Stashed changes
}
