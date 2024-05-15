package Tests;

import Domain.Market.Market;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        store=service.getStoreService().getStore("yairStore");
    }

    @Test
    public void payByBidEmptyProducts(){
      boolean result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
      Assert.assertFalse(result);
    }
    @Test
    public void payByBidProducts(){
        boolean result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
        Assert.assertFalse(result);
    }
}
