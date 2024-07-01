package Domain.Repo;

import Domain.Order;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderRepository {
    private final Map<Integer, Order> orders; // <orderID, Order>
    private final AtomicInteger orderID = new AtomicInteger(0);

    public OrderRepository() {
        orders = new HashMap<>();
    }

    public void addOrder(Order order) {
        orders.put(order.getOrderID(), order);
    }

    public Integer getOrderID() {
        return orderID.getAndIncrement();
    }

    public Map<Integer, Order> getOrders() {
        return orders;
    }
}
