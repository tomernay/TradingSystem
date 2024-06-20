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

    public Response<Map<String, String>> getOrdersHistory(Integer storeID) {
        return orderRepository.getOrdersHistory(storeID);
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public Response<String> getPurchaseHistoryByStore(Integer storeID) {
        return orderRepository.getPurchaseHistoryByStore(storeID);
    }

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberUsername) {
        return orderRepository.getPurchaseHistoryBySubscriber(subscriberUsername);
    }

    public Response<String> CreateOrder(String username, String deliveryAddress, List<ProductDTO> shoppingCartContents) {
        return orderRepository.createOrder(username, deliveryAddress, shoppingCartContents);
    }

    public Response<List<OrderDTO>> getOrdersHistoryDTO(Integer storeID) {
        return orderRepository.getOrdersHistoryDTO(storeID);
    }
}
