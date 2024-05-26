package Service;

import Facades.OrderFacade;
import Domain.Order;
import Utilities.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        try{
            if (storeService.storeExists(storeID) == false){
                return new Response<>(false,"The Store Does Not Exist" );
            }

            Map<Integer, Order> myOrders = orderFacade.getOrders();
            List<Order> orderList = new ArrayList<>();
            for (Order order: myOrders.values()){
                if (order.getStoreID().equals(storeID)){
                    orderList.add(order);
                }
            }
            return new Response<>(true,orderList.toString());
        }
        catch (Exception exception){
            return new Response<>(false,"Other Exception" );
        }
    }

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberID){
        try{
            if (userService.userExists(subscriberID) == false){
                return new Response<>(false, "The User Does Not Exist");
            }

            Map<Integer, Order> myOrders = orderFacade.getOrders();
            List<Order> orderList = new ArrayList<>();
            for (Order order: myOrders.values()){
                if (order.getUsername().equals(subscriberID)){
                    orderList.add(order);
                }
            }
            return new Response<>(true,orderList.toString());
        }
        catch (Exception exception){
            return new Response<>(false, "Other Exception");
        }


    }

    public Response<String> recieveSystemInfo() {
        return null;
    }

    public OrderFacade getOrderFacade(){
        return orderFacade;
    }
}
