package Tests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Messages.NormalMessage;
import Domain.Users.Subscriber.Messages.PaymentMessages.Alternative_Offer;
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
        System.out.println(service.getUserService().register("yair","password123").getMessage());
        subscriber=service.getUserService().getUser("yair");

        service.getUserService().register("yair2","by2");
        buyer=service.getUserService().getUser("yair2");
        service.getStoreService().addStore("yairStore","yair",subscriber.getToken());
        store=service.getStoreService().getStore("0");
        buyer.setCredit("1234567890123456");
    }

    @Test
    public void payByBidEmptyProducts(){
      boolean result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
      Assert.assertFalse(result);
    }
    @Test
    public void cancelPayment() {

        store.getInventory().addProduct(new Domain.Store.Inventory.Product("yairStore", 50, 1), 30);
        store.getInventory().addProduct(new Domain.Store.Inventory.Product("yairStore", 50, 2), 30);
        HashMap<Integer, Integer> storeProducts = new HashMap<>();
        storeProducts.put(1, 3);
        storeProducts.put(2, 3);
        boolean result = service.getPaymentService().sendPayByBid(buyer.getToken(), store, buyer, 50, storeProducts);
        Assert.assertTrue(result);
        service.getUserService().getUser("yair").getMessages().poll().response(false);
        Assert.assertFalse(service.getUserService().getUser(buyer.getUsername()).getMessages().size() == 0);
        System.out.println(buyer.getMessages().peek());
    }



    @Test
    public void acceptAlternativePayment(){


        HashMap<Integer, Integer> storeProducts = new HashMap<>();
        storeProducts.put(1, 3);
        storeProducts.put(2, 3);
//        Assert.assertTrue(service.getUserService().getUser(buyer.getUsername()).getMessages().size() == 1);
        service.getPaymentService().alternativePay(subscriber.getUsername(),subscriber.getToken(),storeProducts,store,buyer,79);
        //Assert.assertTrue(buyer.getMessages().poll() instanceof NormalMessage);
       // System.out.println(buyer.getMessages().poll().getMessage());
        Assert.assertTrue(((Alternative_Offer)buyer.getMessages().peek() )!=null);
        service.getUserService().getUser(buyer.getUsername()).getMessages().poll().response(true);
        Assert.assertTrue(buyer.getMessages().poll() instanceof NormalMessage);
    }


    Subscriber secondOwner;
    @Test
    public void acceptPayByBid(){
        service.getUserService().register("yair23", "by2");
        secondOwner=  service.getUserService().getUser("yair23");

        HashMap<Integer, Integer> storeProducts = new HashMap<>();
        storeProducts.put(1, 3);
        storeProducts.put(2, 3);
        service.getPaymentService().sendPayByBid(buyer.getToken(), store, buyer, 50, storeProducts);

        //subscriber.setCredit("1111111111");
        // buyer.setCredit("1111111111");
        service.getUserService().getUser("yair").getMessages().poll().response(true);
        Assert.assertTrue(buyer.getMessages().poll() instanceof NormalMessage);



    }
}
