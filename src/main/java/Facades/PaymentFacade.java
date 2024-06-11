package Facades;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Repo.PaymentRepository;
import Service.PaymentService;
import Utilities.Response;

public class PaymentFacade {
    private PaymentRepository paymentRepository;

    public PaymentFacade() {
        paymentRepository = new PaymentRepository();
    }

    public Response<String> addPaymentAdapter(PaymentAdapter paymentAdapter, String name){
        return paymentRepository.addPaymentAdapter(paymentAdapter, name);
    }

    public PaymentRepository getPaymentRepository() {
        return paymentRepository;
    }
}
