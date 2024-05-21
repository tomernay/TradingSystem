package src.main.java.Domain.Repo;

import src.main.java.Domain.Order;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

}
