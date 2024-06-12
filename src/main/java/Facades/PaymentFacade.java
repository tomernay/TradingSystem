package Facades;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Repo.PaymentRepository;
import Utilities.Response;

public class PaymentFacade {
    private final PaymentRepository paymentRepository;

    public PaymentFacade() {
        paymentRepository = new PaymentRepository();
    }

    public Response<String> addPaymentAdapter(String paymentAdapter, String name){
        return paymentRepository.addPaymentAdapter(paymentAdapter, name);
    }

    public PaymentRepository getPaymentRepository() {
        return paymentRepository;
    }
}
