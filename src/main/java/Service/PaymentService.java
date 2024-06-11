package Service;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Externals.Security.Security;
import Domain.Externals.Suppliers.SupplierAdapter;
import Facades.PaymentFacade;
import Facades.SupplyFacade;
import Utilities.Response;
import Utilities.SystemLogger;

public class PaymentService {
    private UserService userService;

    private PaymentFacade facade;

    private SupplyFacade supplyFacade;

    public PaymentService(){
        facade=new PaymentFacade();
        supplyFacade=new SupplyFacade();
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

    public Response<String> addPaymentAdapter(PaymentAdapter paymentAdapter, String name, String token, String user){
        if(Security.isValidJWT(token,user)) {
            SystemLogger.info("[START] User: " + user + " is trying to add payment");
            return facade.addPaymentAdapter(paymentAdapter, name);
        }
        return new Response<>(false,"token is invalid",null);
    }


    public Response<String> addSupplierAdapter(SupplierAdapter supplierAdapter, String value, String token, String user) {
        if(Security.isValidJWT(token,user)) {
            SystemLogger.info("[START] User: " + user + " is trying to add supplier");
            return supplyFacade.addSupplierAdapter(supplierAdapter,value);
        }
        return new Response<>(false,"token is invalid",null);
    }
}
