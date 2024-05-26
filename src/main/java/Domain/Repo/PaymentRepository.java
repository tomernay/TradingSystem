package Domain.Repo;

import Domain.Externals.Payment.PaymentAdapter;

import java.util.HashMap;

public class PaymentRepository {
    private HashMap<String, PaymentAdapter> payments;

    public PaymentRepository() {
        this.payments = new HashMap<>();
    }

    public void addPaymentAdapter(PaymentAdapter paymentAdapter, String name){
        payments.put(name, paymentAdapter);
    }



}
