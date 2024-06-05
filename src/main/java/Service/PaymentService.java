package Service;

import Domain.Externals.Payment.DefaultPay;
import Domain.Externals.Security.Security;

import Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import Domain.Store.Store;
import Utilities.Response;

import java.util.HashMap;

public class PaymentService {
    private UserService userService;

    public PaymentService(){
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * send pay by bid offer to all subscribers of types StoreOwner and StoreCreator
     * @param token
     * @param

     * @param user
     * @param fee
     * @param products
     */
    public Response<String> sendPayByBid(String token, String store, String user, double fee, HashMap<Integer,Integer> products){
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
    public Response<String> immediatePay(String user,double fee,Store s,String credit,String token){
        if(userService.isValidToken(user,token) ) {
            ImmediatePay payment=new ImmediatePay(fee,"111111115",credit);
            boolean pay=payment.pay(new DefaultPay(user));
            return new Response<>(pay,"payment Status"+String.valueOf(pay),null);
        }
        return new Response<>(false,"token is invalid",null);


    }

   /* public void alternativePay(String user,String token,HashMap<Integer,Integer> products, Store store, Subscriber subscriber,double fee){
        if(Security.isValidJWT(user,token) ) {
            Alternative_Offer payment=new Alternative_Offer(fee,products, store,subscriber);
            subscriber.addMessage(payment);
        }

    }*/



}
