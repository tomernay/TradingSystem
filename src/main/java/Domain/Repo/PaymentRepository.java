package Domain.Repo;

import DataBase.FireBaseConstants;
import Domain.Externals.Payment.DefaultPay;
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.HashMap;

public class PaymentRepository {
    private final HashMap<String, PaymentAdapter> payments;
    private final HashMap<String, PaymentAdapter> paymentAdapterHashMap;

    public PaymentRepository() {
        this.payments = new HashMap<>();
        paymentAdapterHashMap=new HashMap<>();
        paymentAdapterHashMap.put("DefaultPay",new DefaultPay(""));
    }

    public Response<String> addPaymentAdapter(String paymentAdapter, String name){
        payments.put(name, paymentAdapterHashMap.get(paymentAdapter));
        return new Response<>(true,"payment added successfully");
    }

    /**
     * קנייה מיידית
     * @param fee
     * @param credit
     * @return
     */
    public Response<String> immediatePay(double fee, String credit){
        if (credit == null) {
            SystemLogger.error("[ERROR] User has cancelled payment");
            return Response.error("Payment was cancelled", null);
        }
        ImmediatePay payment=new ImmediatePay(fee, FireBaseConstants.secoundVisa,credit);
        Response<String> pay=payment.pay(new DefaultPay("str"));
        return pay;


    }

}
