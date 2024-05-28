package Domain.Repo;

import Domain.Order;
import Utilities.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
            return Response.error("No orders found for store with ID: " + storeID, null);
        }
        return Response.success("Successfully fetched order history", orderHistory);
    }

    public Map<Integer, Order> getOrders() {
        return orders;
    }
}
