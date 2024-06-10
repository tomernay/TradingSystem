package Service;

import Domain.Externals.Payment.DefaultPay;

import Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import Domain.Store.Store;
import Utilities.Response;

public class PaymentService {
    private UserService userService;

    public PaymentService(){
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
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
