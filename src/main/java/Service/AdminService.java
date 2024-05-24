package Service;

import Domain.Market.Market;
import Domain.Order;
import Utilities.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminService {

    private Market market;

    public AdminService(){
        this.market = Market.getInstance();
    }

    public Market getMarket() {
        return market;
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
            if (market.getMarketFacade().getStoreRepository().getStore(storeID) == null){
                return new Response<>(false,"The Store Does Not Exist" );
            }

            Map<Integer, Order> myOrders = market.getMarketFacade().getOrderRepository().getOrders();
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
            if (market.getMarketFacade().getUserRepository().isUserExist(subscriberID) == false){
                return new Response<>(false, "The User Does Not Exist");
            }

            Map<Integer, Order> myOrders = market.getMarketFacade().getOrderRepository().getOrders();
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
}
