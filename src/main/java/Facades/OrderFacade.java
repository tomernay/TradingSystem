package Facades;

import Domain.Order;
import Domain.OrderDTO;
import Domain.Repo.OrderRepository;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public class OrderFacade {
    private final OrderRepository orderRepository;

    public OrderFacade() {
        orderRepository = new OrderRepository();
    }

    public Map<Integer, Order> getOrders() {
        return orderRepository.getOrders();
    }

    public Response<Map<String, String>> getOrdersHistory(String storeID) {
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

    public Response<String> CreatOrder(String username, String deliveryAddress, Map<String, List<ProductDTO>> shoppingCartContents) {
        return orderRepository.createOrder(username, deliveryAddress, shoppingCartContents);
    }

    public Response<List<OrderDTO>> getOrdersHistoryDTO(String storeID) {
        return orderRepository.getOrdersHistoryDTO(storeID);
    }
}
