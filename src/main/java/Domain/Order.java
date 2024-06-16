package Domain;

import java.util.Date;
import java.util.Map;

public class Order {
    private final int orderID;
    private final String storeID;
    private final String username;
    private final String deliveryAddress;
    private final Date orderDate;
    private final Map<String, Map<String, String>> products; // <productID, <PARAMETER, VALUE>>
    private String status;

    public Order(int orderID, String storeID, String username, String deliveryAddress, Map<String, Map<String, String>> products) {
        this.orderID = orderID;
        this.storeID = storeID;
        this.username = username;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = new Date(); // Set to current date/time
        this.products = products;
        this.status = "Pending";
    }

    public int getOrderID() {
        return orderID;
    }

    public String getStoreID() {
        return storeID;
    }

    public String getUsername() {
        return username;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Map<String, Map<String, String>> getProducts() {
        return products;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", storeID='" + storeID + '\'' +
                ", username='" + username + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", orderDate=" + orderDate +
                ", products=" + products +
                ", status='" + status + '\'' +
                '}';
    }
}
