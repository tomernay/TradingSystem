package Domain.Repo;

import Domain.Order;
import Domain.OrderDTO;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;
import Utilities.SystemLogger;
import java.util.*;

public class OrderRepository {
    private final Map<Integer, Order> orders; // <orderID, Order>
    private int counterId = 0;

    public OrderRepository() {
        orders = new HashMap<>();
    }

    public void addOrder(Integer storeID, String username, String deliveryAddress, List<ProductDTO> products) {
        Order order = new Order(counterId, storeID, username, deliveryAddress, products);
        orders.put(counterId, order);
        counterId++;
    }

    public Response<Map<String, String>> getOrdersHistory(Integer storeID) {
        Map<String, String> orderHistory = new HashMap<>();

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

    public Map<Integer, Order> getOrders() {
        return orders;
    }

    public Response<String> getPurchaseHistoryByStore(Integer storeID) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getStoreID().equals(storeID)) {
                orderList.add(order);
            }
        }
        if (orderList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for store with ID: " + storeID);
            return Response.error("No orders found for store with ID: " + storeID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success(orderList.toString(), orderList.toString());
    }

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberUsername) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getUsername().equals(subscriberUsername)) {
                orderList.add(order);
            }
        }
        if (orderList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for subscriber with ID: " + subscriberUsername);
            return Response.error("No orders found for subscriber with ID: " + subscriberUsername, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success(orderList.toString(), orderList.toString());
    }

    public Response<String> createOrder(String username, String deliveryAddress, List<ProductDTO> shoppingCartContents) {
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
            addOrder(storeID, username, deliveryAddress, entry.getValue());
        }
        return Response.success("Order created successfully", null);
    }

    public Response<List<OrderDTO>> getOrdersHistoryDTO(Integer storeID) {
        List<OrderDTO> orderDTOList = new ArrayList<>();
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
