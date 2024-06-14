package Service;

import Domain.Externals.Security.Security;
import Facades.PaymentFacade;
import Facades.SupplyFacade;
import Utilities.Response;
import Utilities.SystemLogger;

public class PaymentService {
    private final PaymentFacade facade;
    private final SupplyFacade supplyFacade;
    private UserService userService;
    private OrderService orderService;


    public PaymentService(){
        facade=new PaymentFacade();
        supplyFacade=new SupplyFacade();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
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
        if (Security.isValidJWT(token,user)) {
            if (!userService.isInPurchaseProcess(user)) {
                SystemLogger.error("[ERROR] User: " + user + " is trying to pay without being in purchase process");
                return Response.error("user is not in purchase process", null);
            }
            Response<String> response = facade.getPaymentRepository().immediatePay(fee, credit);
            if (response.isSuccess()) {
                userService.ReleaseShoppingCartFromStore(user, token);
                orderService.CreateOrder(user, token);
                userService.ReleaseShoppingCartForUser(user, token);
                userService.purchaseProcessInterrupt(user);
            } else {
                userService.ReleaseShoppingCartAndBacktoInventory(user, token);
                userService.purchaseProcessInterrupt(user);
            }
            return response;
        }
        SystemLogger.error("[ERROR] User: " + user + " is trying to pay with invalid token");
        return Response.error("token is invalid", null);
    }

    public Response<String> addPaymentAdapter(String paymentAdapter, String name, String token, String user){
        if(Security.isValidJWT(token,user)) {
            SystemLogger.info("[START] User: " + user + " is trying to add payment");
            return facade.addPaymentAdapter(paymentAdapter, name);
        }
        SystemLogger.error("[ERROR] User: " + user + " is trying to pay with invalid token");
        return Response.error("token is invalid",null);
    }

    public Response<String> addSupplierAdapter(String supplierAdapter, String value, String token, String user) {
        if(Security.isValidJWT(token,user)) {
            SystemLogger.info("[START] User: " + user + " is trying to add supplier");
            return supplyFacade.addSupplierAdapter(supplierAdapter,value);
        }
        SystemLogger.error("[ERROR] User: " + user + " is trying to pay with invalid token");
        return Response.error("token is invalid",null);
    }
}
