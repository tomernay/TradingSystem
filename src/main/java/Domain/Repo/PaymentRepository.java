package Domain.Repo;

import DataBase.FireBaseConstants;
import Domain.Externals.Payment.DefaultPay;
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import Utilities.Response;

import java.util.HashMap;

public class PaymentRepository {
    private HashMap<String, PaymentAdapter> payments;

    public PaymentRepository() {
        this.payments = new HashMap<>();
    }

    public void addPaymentAdapter(PaymentAdapter paymentAdapter, String name){
        payments.put(name, paymentAdapter);
    }

    /**
     * קנייה מיידית
     * @param fee
     * @param credit
     * @return
     */
    public Response<String> immediatePay(double fee, String credit){

        ImmediatePay payment=new ImmediatePay(fee, FireBaseConstants.secoundVisa,credit);
        boolean pay=payment.pay(new DefaultPay("str"));
        return new Response<>(pay,"payment Status"+String.valueOf(pay),null);


    }

}
