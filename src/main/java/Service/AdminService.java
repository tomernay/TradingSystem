package Service;

import Facades.AdminFacade;
import Utilities.Response;
import Utilities.SystemLogger;

import java.sql.Date;
import java.util.Map;

public class AdminService {
    private UserService userService;
    private StoreService storeService;
    private OrderService orderService;
    private final AdminFacade adminFacade;

    public AdminService(){
        adminFacade = new AdminFacade();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
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
            return orderService.getPurchaseHistoryByStore(storeID);
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
            return orderService.getPurchaseHistoryBySubscriber(subscriberID);
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }
    }

    public Response<String> recieveSystemInfo() {
        return null;
    }

    public Response<String> suspendUser(String subscriberID, Date endOfSuspensionDate){
        try {
            if (userService.userExists(subscriberID) == false) {
                SystemLogger.error("[ERROR] The User Does Not Exist");
                return Response.error("The User Does Not Exist", null);
            }

            //TO DO - implementation of SUSPENDING a user
            return adminFacade.getAdminRepository().suspendUser(subscriberID,endOfSuspensionDate);
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }
    }

    public Response<String> reactivateUser(String subscriberID){
        try {
            if (userService.userExists(subscriberID) == false) {
                SystemLogger.error("[ERROR] The User Does Not Exist");
                return Response.error("The User Does Not Exist", null);
            }

            return adminFacade.getAdminRepository().reactivateUser(subscriberID);
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }
    }

    public Response<Map<String,Date>> getSuspensionList(){
        Map<String,Date> list = adminFacade.getAdminRepository().getSuspensionList();
        return Response.success("suspension list", list);
    }

    public boolean isSuspended(String subscriberID){
        return adminFacade.getAdminRepository().isSuspended(subscriberID);
    }

}
