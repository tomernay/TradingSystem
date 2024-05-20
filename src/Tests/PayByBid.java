package src.Tests;

import src.main.java.Domain.Market.Market;
import src.main.java.Domain.Store.Store;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import src.main.java.Service.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import src.main.java.Utilities.Response;

import java.util.HashMap;

public class PayByBid {
    Service service;
    Subscriber subscriber,buyer;
    Store store;
    @Before
    public void init(){
        service=new Service();
        service.getUserService().register("yair","by");
        subscriber=service.getUserService().getUser("yair");

        service.getUserService().register("yair2","by2");
        buyer=service.getUserService().getUser("yair2");
        service.getStoreService().addStore("yairStore","yair",subscriber.getToken());
        store=service.getStoreService().getStore("0");
    }

    @Test
    public void payByBidEmptyProducts(){
     // Response<String> result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
     // Assert.assertFalse(result.isSuccess());
    }
    @Test
    public void payByBidProducts(){
      //  Response<String> result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
      //  Assert.assertFalse(result.isSuccess());
    }
}
