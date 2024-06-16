package Presentation.application.Presenter;

import Presentation.application.View.PaymentView;
import Service.OrderService;
import Service.ServiceInitializer;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class PaymentPresenter {
    private PaymentView paymentView;
    private final OrderService orderService;
    private HttpServletRequest request;

    public PaymentPresenter(HttpServletRequest request) {
        orderService = ServiceInitializer.getInstance().getOrderService();
        this.request = request;
    }

    public void attachView(PaymentView view) {
        this.paymentView = view;
    }

    public void pay(String user, double totalPrice, String creditCardNumber, String expirationDate, String cvv, String fullName, String address, String token) {
        Response<String> payRes = orderService.payAndSupply(totalPrice, user, token, address, creditCardNumber, expirationDate, cvv, fullName);
        if (!paymentView.hasTimerEnded() || creditCardNumber == null) {
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
