package Domain.Repo;

import Domain.Order;

import java.util.HashMap;
import java.util.Map;

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

    public Map<Integer, Order> getOrders() {
        return orders;
    }
}
