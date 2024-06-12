package Service;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Externals.Security.Security;
import Domain.Externals.Suppliers.SupplierAdapter;
import Facades.PaymentFacade;
import Facades.SupplyFacade;
import Utilities.Response;
import Utilities.SystemLogger;

public class PaymentService {
    private final PaymentFacade facade;
    private final SupplyFacade supplyFacade;

    public PaymentService(){
        facade=new PaymentFacade();
        supplyFacade=new SupplyFacade();
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
    }

    public Response<String> addPaymentAdapter(String paymentAdapter, String name, String token, String user){
        if(Security.isValidJWT(token,user)) {
            SystemLogger.info("[START] User: " + user + " is trying to add payment");
            return facade.addPaymentAdapter(paymentAdapter, name);
        }
        return new Response<>(false,"token is invalid",null);
    }

    public Response<String> addSupplierAdapter(String supplierAdapter, String value, String token, String user) {
        if(Security.isValidJWT(token,user)) {
            SystemLogger.info("[START] User: " + user + " is trying to add supplier");
            return supplyFacade.addSupplierAdapter(supplierAdapter,value);
        }
        return new Response<>(false,"token is invalid",null);
    }
}
