package Service;

import Facades.OrderFacade;
import Utilities.Response;
import Utilities.SystemLogger;

public class AdminService {
    private UserService userService;
    private StoreService storeService;
    private OrderFacade orderFacade;

    public AdminService(){
        orderFacade = new OrderFacade();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    public Response<String> closeStore() {
        return null;
    }

    public Response<String> removeSubscriber() {
        return null;
    }

    public Response<String> recieveInfoWithResponse() {
        return null;
    }

    public Response<String> getPurchaseHistoryByStore(String storeID) {
        SystemLogger.info("[START] Admin is trying to get purchase history by store");
        try{
            if (storeService.storeExists(storeID) == false){
                SystemLogger.error("[ERROR] The Store Does Not Exist");
                return Response.error("The Store Does Not Exist", null);
            }
            return orderFacade.getPurchaseHistoryByStore(storeID);
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }
    }

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberID){
        try{
            if (userService.userExists(subscriberID) == false){
                SystemLogger.error("[ERROR] The User Does Not Exist");
                return Response.error("The User Does Not Exist", null);
            }
            return orderFacade.getPurchaseHistoryBySubscriber(subscriberID);
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }


    }

    public Response<String> recieveSystemInfo() {
        return null;
    }

    public OrderFacade getOrderFacade(){
        return orderFacade;
    }
}
