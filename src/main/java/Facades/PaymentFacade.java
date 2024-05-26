package Facades;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Repo.PaymentRepository;

public class PaymentFacade {
    private PaymentRepository paymentRepository;

    public PaymentFacade() {
        paymentRepository = new PaymentRepository();
    }

    public void addPaymentAdapter(PaymentAdapter paymentAdapter, String name){
        paymentRepository.addPaymentAdapter(paymentAdapter, name);
    }
}
