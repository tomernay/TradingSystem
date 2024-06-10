package Presentaion.application.Presenter;

import Presentaion.application.View.LoginView;
import Presentaion.application.View.Payment.PaymentPage;
import Service.PaymentService;
import Service.ServiceInitializer;
import Utilities.Response;
import org.springframework.stereotype.Component;

@Component
public class PaymentPresenter {
    PaymentPage paymentPage;
    PaymentService paymentService;
    public PaymentPresenter() {
            paymentService = ServiceInitializer.getInstance().getPaymentService();
    }
    public void attachView(PaymentPage view) {
        this.paymentPage = view;
    }


    public void pay(String user,double fee,String credit,String token){
        Response<String> payRes=paymentService.immediatePay(user,fee,credit,token);
        paymentPage.showNotification(payRes.getMessage());
    }
}
