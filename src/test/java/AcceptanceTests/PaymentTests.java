package AcceptanceTests;

import DataBase.FireBaseConstants;
import Domain.Users.Subscriber.Subscriber;
import Service.PaymentService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PaymentTests {
    ServiceInitializer serviceInitializer;
    PaymentService paymentService;
    UserService userService;
    Subscriber subscriber;
    @Before
    public void init() {
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        paymentService=serviceInitializer.getPaymentService();
        userService.register("miaa", "Password123!");
        userService.loginAsSubscriber("miaa", "Password123!");
        subscriber = userService.getUserFacade().getUserRepository().getUser("miaa");
    }
//    @Test
//    public void ImmediatePaymentTest(){
//        Response<String> s=paymentService.immediatePay(subscriber.getUsername(),50.0, FireBaseConstants.visa,subscriber.getToken());
//        Assert.assertTrue(s.isSuccess());
//    }

//    @Test
//    public void ImmediatePaymentTestIleegalVisa(){
//        Response<String> s=paymentService.immediatePay(subscriber.getUsername(),50.0, "",subscriber.getToken());
//        Assert.assertFalse(s.isSuccess());
//    }
}
