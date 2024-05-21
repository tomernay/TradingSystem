package src.main.java.Domain.Externals.Payment.UnitTests;

import org.junit.Assert;
import org.junit.Test;
import src.main.java.DataBase.FireBaseConstants;
import src.main.java.Domain.Externals.Payment.CreditCard;
import src.main.java.Domain.Externals.Payment.DefaultPay;
import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Utilities.Response;

public class ProxyPaymentTest {

    @Test
    public void legalPayment(){
        PaymentAdapter proxyPayment = new DefaultPay("yair");
       Response<String> res= proxyPayment.pay(new CreditCard(FireBaseConstants.visa), new CreditCard("222222222"), 100);
        Assert.assertTrue(res.isSuccess());
    }

    @Test
    public void illegalVisa(){
        PaymentAdapter proxyPayment = new DefaultPay("yair");
       Response<String> res= proxyPayment.pay(new CreditCard("11111"), new CreditCard("222222222"), 100);
        Assert.assertFalse(res.isSuccess());
    }

    @Test
    public void illegalFee(){
        PaymentAdapter proxyPayment = new DefaultPay("yair");
        Response<String> res= proxyPayment.pay(new CreditCard(FireBaseConstants.visa), new CreditCard("222222222"), 0);
        Assert.assertFalse(res.isSuccess());
    }

    @Test
    public void payToYourself(){
        PaymentAdapter proxyPayment = new DefaultPay("yair");
        Response<String> res= proxyPayment.pay(new CreditCard(FireBaseConstants.visa), new CreditCard(FireBaseConstants.visa), 100);
        Assert.assertFalse(res.isSuccess());
    }


}
