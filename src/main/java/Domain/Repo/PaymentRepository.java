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

    public Response<String> addPaymentAdapter(PaymentAdapter paymentAdapter, String name){
        payments.put(name, paymentAdapter);
        return new Response<>(true,"payment added successfully");
    }

    /**
     * קנייה מיידית
     * @param fee
     * @param credit
     * @return
     */
    public Response<String> immediatePay(double fee, String credit){

        ImmediatePay payment=new ImmediatePay(fee, FireBaseConstants.secoundVisa,credit);
        Response<String> pay=payment.pay(new DefaultPay("str"));
        return pay;


    }

}
