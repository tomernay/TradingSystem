package Presentation.application.Presenter;

import Presentation.application.View.PaymentView;
import Service.PaymentService;
import Service.ServiceInitializer;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentPresenter {
    private PaymentView paymentView;
    private final PaymentService paymentService;
    private HttpServletRequest request;

    public PaymentPresenter(HttpServletRequest request) {
        paymentService = ServiceInitializer.getInstance().getPaymentService();
        this.request = request;
    }

    public void attachView(PaymentView view) {
        this.paymentView = view;
    }

    public void pay(String user, double fee, String credit, String token) {
        Response<String> payRes = paymentService.immediatePay(user, fee, credit, token);
        if (!paymentView.hasTimerEnded() || credit == null) {
            paymentView.showNotification(payRes.getMessage());
        }
        else{
            paymentView.showNotification("Payment failed - time is up!");
        }
        if (!payRes.isSuccess()) {
            paymentView.navigateToShoppingCart();
        }
        else {
            paymentView.navigateToHome();
        }
    }
}
