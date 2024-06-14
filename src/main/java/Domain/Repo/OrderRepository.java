package Domain.Repo;

import Domain.Order;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.*;

public class OrderRepository {
    private final Map<Integer, Order> orders;// <orderID, Order>
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
        return Response.success(orderList.toString(), orderList.toString());
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
        return Response.success(orderList.toString(), orderList.toString());
    }

    public Response<String> CreatOrder(String username, Map<String, Map<String, Integer>> shoppingCartContents) {
        if (shoppingCartContents.isEmpty()) {
            SystemLogger.error("[ERROR] User " + username + " tried to purchase an empty shopping cart");
            return Response.error("Error - can't purchase an empty shopping cart", null);
        }
        for (Map.Entry<String, Map<String, Integer>> entry : shoppingCartContents.entrySet()) {
            String storeID = entry.getKey();
            Map<String,Map<String,String>> product = new HashMap<>();
            for (Map.Entry<String,Integer> entry1 : shoppingCartContents.get(storeID).entrySet()) {
                Map<String,String> parameter = new HashMap<>();
                parameter.put("quantity", String.valueOf(entry1.getValue()));
                product.put(entry1.getKey(), parameter);
            }
            addOrder(entry.getKey(), username, product);
        }
        return Response.success("Order created successfully", null);
    }
}
