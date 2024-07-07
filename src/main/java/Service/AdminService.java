package Service;

import Facades.AdminFacade;
import Utilities.Response;
import Utilities.SystemLogger;
import java.util.Date;
import java.util.List;
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

    public Response<List<String>> getPurchaseHistoryByStore(Integer storeID) {
        SystemLogger.info("[START] Admin is trying to get purchase history by store");
        try{
            if (!storeService.storeExists(storeID)){
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

    public Response<List<String>> getPurchaseHistoryBySubscriber(String subscriberUsername){
        try{
            if (!userService.userExists(subscriberUsername)){
                SystemLogger.error("[ERROR] The User Does Not Exist");
                return Response.error("The User Does Not Exist", null);
            }
            return orderService.getPurchaseHistoryBySubscriber(subscriberUsername);
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }
    }

    public Response<String> recieveSystemInfo() {
        return null;
    }

    public Response<String> suspendUser(String subscriberUsername, Date endOfSuspensionDate){
        try {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] The User Does Not Exist");
                return Response.error("The User Does Not Exist", null);
            }
            Response<String> res = adminFacade.suspendUser(subscriberUsername,endOfSuspensionDate);
            if (res.isSuccess()){
                userService.sendMessage(subscriberUsername, "You have been suspended until " + endOfSuspensionDate);
            }
            return res;
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }
    }

    public Response<String> reactivateUser(String subscriberUsername){
        try {
            if (!userService.userExists(subscriberUsername)) {
                SystemLogger.error("[ERROR] The User Does Not Exist");
                return Response.error("The User Does Not Exist", null);
            }
            Response<String> res = adminFacade.reactivateUser(subscriberUsername);
            if (res.isSuccess()){
                userService.sendMessage(subscriberUsername, "Your account have been reactivated");
            }
            return res;
        }
        catch (Exception exception){
            SystemLogger.error("[ERROR] Other Exception");
            return Response.error("Other Exception", null);
        }
    }

    public Response<Map<String,Date>> getSuspensionList(){
        Map<String,Date> list = adminFacade.getSuspensionList();
        return Response.success("suspension list", list);
    }

    public boolean isSuspended(String subscriberID){
        return adminFacade.isSuspended(subscriberID);
    }

}
