package Service;

import Domain.Externals.Payment.DefaultPay;
import Domain.Externals.Security.Security;
import Domain.Market.Market;
import Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import Domain.Store.Store;
import Domain.Users.StateOfSubscriber.StoreCreator;
import Domain.Users.StateOfSubscriber.StoreOwner;
import Domain.Users.StateOfSubscriber.SubscriberState;
import Domain.Users.Subscriber.Messages.PaymentMessages.Alternative_Offer;
import Domain.Users.Subscriber.Messages.PaymentMessages.PayByBidOffer;
import Domain.Users.Subscriber.Subscriber;

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
    public boolean sendPayByBid(String token,Store s,  Subscriber user, double fee, HashMap<Integer,Integer> products){
        if(Security.isValidJWT(token, user.getUsername()) ){
              if(!products.isEmpty()) {
                  for (SubscriberState subscriberState : s.getSubscribers().values()) {
                      if (s.isStoreCreator(subscriberState.getSubscriberID()) || s.isStoreOwner(subscriberState.getSubscriberID())) {
                          PayByBidOffer payByBidOffer = new PayByBidOffer(s, subscriberState.getSubscriberID(), user, fee, products);
                          market.getMarketFacade().getUserRepository().sendMessageToUser(subscriberState.getSubscriberID(), payByBidOffer);
                      }
                  }
                  return true;
              }
              return false;

        }
        else {
            return false;
        }


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
