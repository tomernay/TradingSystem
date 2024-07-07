package Facades;

import Domain.Order;
import Domain.OrderDTO;
import Domain.Repo.OrderRepository;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;
import Utilities.SystemLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFacade {
    private final OrderRepository orderRepository;

    public OrderFacade() {
        orderRepository = new OrderRepository();
    }

    public Response<Map<String, String>> getOrdersHistory(Integer storeID) {
        Map<String, String> orderHistory = new HashMap<>();
        Map<Integer, Order> orders = orderRepository.getOrders();
        for (Order order : orders.values()) {
            if (order.getStoreID().equals(storeID)) {
                orderHistory.put(String.valueOf(order.getOrderID()), order.toString());
            }
        }
        if (orderHistory.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for store with ID: " + storeID);
            return Response.error("No orders found for store with ID: " + storeID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success("Successfully fetched order history", orderHistory);
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public Response<List<String>> getPurchaseHistoryByStore(Integer storeID) {
        List<String> orderList = new ArrayList<>();
        Map<Integer, Order> orders = orderRepository.getOrders();
        for (Order order : orders.values()) {
            if (order.getStoreID().equals(storeID)) {
                orderList.add(order.toString());
            }
        }
        if (orderList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for store with ID: " + storeID);
            return Response.error("No orders found for store with ID: " + storeID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success("Successfully fetched order history", orderList);
    }

    public Response<List<String>> getPurchaseHistoryBySubscriber(String subscriberUsername) {
        List<String> orderList = new ArrayList<>();
        Map<Integer, Order> orders = orderRepository.getOrders();
        for (Order order : orders.values()) {
            if (order.getUsername().equals(subscriberUsername)) {
                orderList.add(order.toString());
            }
        }
        if (orderList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for subscriber with ID: " + subscriberUsername);
            return Response.error("No orders found for subscriber with ID: " + subscriberUsername, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success("successfuly fetched order history", orderList);
    }

    public Response<String> CreateOrder(String username, String deliveryAddress, List<ProductDTO> shoppingCartContents) {
        if (shoppingCartContents.isEmpty()) {
            SystemLogger.error("[ERROR] User " + username + " tried to purchase an empty shopping cart");
            return Response.error("Error - can't purchase an empty shopping cart", null);
        }
        Map<Integer, List<ProductDTO>> productsPerStore = new HashMap<>();
        for (ProductDTO product : shoppingCartContents) {
            if (!productsPerStore.containsKey(product.getStoreID())) {
                productsPerStore.put(product.getStoreID(), new ArrayList<>());
            }
            productsPerStore.get(product.getStoreID()).add(product);
        }
        for (Map.Entry<Integer, List<ProductDTO>> entry : productsPerStore.entrySet()) {
            Integer storeID = entry.getKey();
            Order order = new Order(orderRepository.getOrderID(), storeID, username, deliveryAddress, entry.getValue());
            orderRepository.addOrder(order);
        }
        return Response.success("Order created successfully", null);
    }

    public Response<List<OrderDTO>> getOrdersHistoryDTO(Integer storeID) {
        List<OrderDTO> orderDTOList = new ArrayList<>();
        Map<Integer, Order> orders = orderRepository.getOrders();
        for (Order order : orders.values()) {
            if (order.getStoreID().equals(storeID)) {
                orderDTOList.add(new OrderDTO(
                        order.getOrderID(),
                        order.getStoreID(),
                        order.getUsername(),
                        order.getDeliveryAddress(),
                        order.getOrderDate(),
                        order.getProducts(),
                        order.getStatus()
                ));
            }
        }
        if (orderDTOList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for store with ID: " + storeID);
            return Response.error("No orders found for store with ID: " + storeID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success("Successfully fetched order history", orderDTOList);
    }


}
