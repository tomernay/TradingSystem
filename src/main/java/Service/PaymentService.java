package Service;

import Domain.Externals.Payment.DefaultPay;

import Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import Domain.Store.Store;
import Facades.PaymentFacade;
import Utilities.Response;
import Utilities.SystemLogger;

public class PaymentService {
    private UserService userService;

    private PaymentFacade facade;

    public PaymentService(){
        facade=new PaymentFacade();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * immediate payment by user
     * @param user
     * @param fee

     * @param credit
     * @param token
     */
    public Response<String> immediatePay(String user,double fee,String credit,String token){

            SystemLogger.info("[START] User: " + user + " is trying to pay");
            return facade.getPaymentRepository().immediatePay(fee,credit);

       // SystemLogger.error("[ERROR] User: " + user + " tried to pay but the token was invalid");
        //return new Response<>(false,"token is invalid",null);


    }

   /* public void alternativePay(String user,String token,HashMap<Integer,Integer> products, Store store, Subscriber subscriber,double fee){
        if(Security.isValidJWT(user,token) ) {
            Alternative_Offer payment=new Alternative_Offer(fee,products, store,subscriber);
            subscriber.addMessage(payment);
        }

    }*/



}
