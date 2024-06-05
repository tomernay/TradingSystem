package Domain.Repo;

import Domain.Order;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.*;

public class OrderRepository {
    private Map<Integer, Order> orders;// <orderID, Order>
    private int counterId = 0;
    public OrderRepository() {
        orders = new HashMap<>();
    }

    public void addOrder(String storeID, String username, Map<String,Map<String,String>> products) {
        Order order = new Order(counterId, storeID, username, products);
        orders.put(counterId, order);
        counterId++;
    }

    public Response<Map<String, String>> getOrdersHistory(String storeID) {
        Map<String, String> orderHistory = new HashMap<>();

        for (Order order: orders.values()){
            if (order.getStoreID().equals(storeID)){
                orderHistory.put(String.valueOf(order.getOrderID()), order.toString());
            }
        }
        if(orderHistory.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for store with ID: " + storeID);
            return Response.error("No orders found for store with ID: " + storeID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success("Successfully fetched order history", orderHistory);
    }

    public Map<Integer, Order> getOrders() {
        return orders;
    }

    public Response<String> getPurchaseHistoryByStore(String storeID) {
        List<Order> orderList = new ArrayList<>();
        for (Order order: orders.values()){
            if (order.getStoreID().equals(storeID)){
                orderList.add(order);
            }
        }
        if(orderList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for store with ID: " + storeID);
            return Response.error("No orders found for store with ID: " + storeID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success(orderList.toString(), null);
    }

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberID) {
        List<Order> orderList = new ArrayList<>();
        for (Order order: orders.values()){
            if (order.getUsername().equals(subscriberID)){
                orderList.add(order);
            }
        }
        if(orderList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for subscriber with ID: " + subscriberID);
            return Response.error("No orders found for subscriber with ID: " + subscriberID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success(orderList.toString(), null);
    }
}
