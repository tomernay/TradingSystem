package Facades;

import Domain.Order;
import Domain.Repo.OrderRepository;
import Utilities.Response;

import java.util.Map;

public class OrderFacade {
    private OrderRepository orderRepository;
    public OrderFacade() {
        orderRepository = new OrderRepository();
    }

    public Map<Integer, Order> getOrders() {
        return orderRepository.getOrders();
    }
    public Response<Map<String,String>> getOrdersHistory(String storeID) {
        return orderRepository.getOrdersHistory(storeID);
    }
    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public Response<String> getPurchaseHistoryByStore(String storeID) {
        return orderRepository.getPurchaseHistoryByStore(storeID);
    }

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberID) {
        return orderRepository.getPurchaseHistoryBySubscriber(subscriberID);
    }
}
