package Domain.Store.PurchasePolicy.PaymentTypes;


import DataBase.PublicPay.PublicPayDTO;
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PublicPay extends PurchaseType {
    double maxFee;
    Store store;
    Map<String,Integer> products;

    String card;
    Date date;

    Subscriber subscriber;
    //constructor for initiate timer
    public PublicPay(double maxFee,int time,Store store,String card,HashMap<String,Integer> products){
        this.maxFee=maxFee;
        date=new Date(System.currentTimeMillis()+time*1000);
        this.store=store;
        this.card=card;
        this.products=products;
        try {
            PublicPayDTO.addPublicPayment(this);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //Constructor without initiate timer
    public PublicPay(double maxFee,Date time,Store store,String card,HashMap<String,Integer> products){
        this.maxFee=maxFee;
        date=time;
        this.store=store;
        this.card=card;
        this.products=products;

    }

    public PublicPay(){

    }
    @Override
    public boolean pay(PaymentAdapter paymentAdapter) {
        ImmediatePay    immediatePay=new ImmediatePay(maxFee,store,card);
        return immediatePay.pay(paymentAdapter);
    }




    public Store getStore() {
        return store;
    }

    public Date getDate() {
        return date;
    }

    public double getMaxFee() {
        return maxFee;
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public String getCard() {
        return card;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setProducts(HashMap<String, Integer> products) {
        this.products = products;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setMaxFee(double maxFee) {
        this.maxFee = maxFee;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

}
