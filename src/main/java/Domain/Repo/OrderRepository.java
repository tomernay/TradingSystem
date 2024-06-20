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

    public void addOrder(String storeID, String username, String deliveryAddress, List<ProductDTO> products) {
        Order order = new Order(counterId, storeID, username, deliveryAddress, products);
        orders.put(counterId, order);
        counterId++;
    }

    public Response<Map<String, String>> getOrdersHistory(String storeID) {
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

    public Response<String> getPurchaseHistoryByStore(String storeID) {
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

    public Response<String> getPurchaseHistoryBySubscriber(String subscriberID) {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getUsername().equals(subscriberID)) {
                orderList.add(order);
            }
        }
        if (orderList.isEmpty()) {
            SystemLogger.error("[ERROR] No orders found for subscriber with ID: " + subscriberID);
            return Response.error("No orders found for subscriber with ID: " + subscriberID, null);
        }
        SystemLogger.info("[SUCCESS] Successfully fetched order history");
        return Response.success(orderList.toString(), orderList.toString());
    }

    public Response<String> createOrder(String username, String deliveryAddress, Map<String, List<ProductDTO>> shoppingCartContents) {
        if (shoppingCartContents.isEmpty()) {
            SystemLogger.error("[ERROR] User " + username + " tried to purchase an empty shopping cart");
            return Response.error("Error - can't purchase an empty shopping cart", null);
        }
        for (Map.Entry<String, List<ProductDTO>> entry : shoppingCartContents.entrySet()) {
            String storeID = entry.getKey();
            addOrder(storeID, username, deliveryAddress, entry.getValue());
        }
//            String storeID = entry.getKey();
//            Map<String, Map<String, String>> product = new HashMap<>();
//            for (Map.Entry<String, Integer> entry1 : shoppingCartContents.get(storeID).entrySet()) {
//                Map<String, String> parameter = new HashMap<>();
//                parameter.put("quantity", String.valueOf(entry1.getValue()));
//                product.put(entry1.getKey(), parameter);
//            }
//            addOrder(storeID, username, deliveryAddress, product);
//        }
        return Response.success("Order created successfully", null);
    }

    public Response<List<OrderDTO>> getOrdersHistoryDTO(String storeID) {
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

    private List<ProductDTO> convertToProductDTOList(Map<String, Map<String, String>> products) {
        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> productEntry : products.entrySet()) {
            String productID = productEntry.getKey();
            String quantityStr = productEntry.getValue().get("quantity");
            int quantity = quantityStr != null ? Integer.parseInt(quantityStr) : 0;
            ProductDTO productDTO = new ProductDTO(null, Integer.parseInt(productID), quantity);
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }
}
