package Facades;

import Domain.Order;
import Domain.Repo.OrderRepository;

import java.util.Map;

public class OrderFacade {
    private OrderRepository orderRepository;
    public OrderFacade() {
        orderRepository = new OrderRepository();
    }

    public Map<Integer, Order> getOrders() {
        return orderRepository.getOrders();
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }
}
