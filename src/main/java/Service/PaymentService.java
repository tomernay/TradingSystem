package src.main.java.Service;

import src.main.java.Domain.Externals.Payment.DefaultPay;
import src.main.java.Domain.Externals.Security.Security;

import src.main.java.Domain.Market.Market;
import src.main.java.Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import src.main.java.Domain.Store.Store;
import src.main.java.Domain.Users.StateOfSubscriber.SubscriberState;
import src.main.java.Domain.Users.Subscriber.Messages.PaymentMessages.Alternative_Offer;
import src.main.java.Domain.Users.Subscriber.Messages.PaymentMessages.PayByBidOffer;
import src.main.java.Domain.Users.Subscriber.Subscriber;
import src.main.java.Utilities.Response;

import java.util.HashMap;

public class PaymentService {
    private Market market;
    public PaymentService(Market market){
        this.market=market;
    }

    public Market getMarket() {
        return market;
    }

    /**
     * send pay by bid offer to all subscribers of types StoreOwner and StoreCreator
     * @param token
     * @param s

     * @param user
     * @param fee
     * @param products
     */
    public Response<String> sendPayByBid(String token, Store s, Subscriber user, double fee, HashMap<Integer,Integer> products){
      /*  if(Security.isValidJWT(token, user.getUsername()) ){
              if(!products.isEmpty()) {
                  for (SubscriberState subscriberState : s.getSubscribers().values()) {
                      if (s.isStoreCreator(subscriberState.getSubscriberUsername()) || s.isStoreOwner(subscriberState.getSubscriberUsername())) {
                          PayByBidOffer payByBidOffer = new PayByBidOffer(s, subscriberState.getSubscriberUsername(), user, fee, products);
                          market.getMarketFacade().getUserRepository().sendMessageToUser(subscriberState.getSubscriberUsername(), payByBidOffer);
                      }
                  }
                  return true;
              }
              return false;

        }
        else {
            return false;
        }*/
        //TODO: implement
        return null;
    }

    /**
     * immediate payment by user
     * @param user
     * @param fee
     * @param s
     * @param credit
     * @param token
     */
    public void immediatePay(String user,double fee,Store s,String credit,String token){
        if(Security.isValidJWT(user,token) ) {
            ImmediatePay payment=new ImmediatePay(fee,s,credit);
            payment.pay(new DefaultPay(user));
        }

    }

    public void alternativePay(String user,String token,HashMap<Integer,Integer> products, Store store, Subscriber subscriber,double fee){
        if(Security.isValidJWT(user,token) ) {
            Alternative_Offer payment=new Alternative_Offer(fee,products, store,subscriber);
            subscriber.addMessage(payment);
        }

    }



}
