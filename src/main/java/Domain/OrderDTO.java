package Domain;

import Domain.Store.Inventory.ProductDTO;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private final int orderID;
    private final String storeID;
    private final String username;
    private final String deliveryAddress;
    private final Date orderDate;
    private final List<ProductDTO> products;
    private final String status;

    public OrderDTO(int orderID, String storeID, String username, String deliveryAddress, Date orderDate, List<ProductDTO> products, String status) {
        this.orderID = orderID;
        this.storeID = storeID;
        this.username = username;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = orderDate;
        this.products = products;
        this.status = status;
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

    public List<ProductDTO> getProducts() {
        return products;
    }

    public String getStatus() {
        return status;
    }
    @Override
    public String toString() {
        return "OrderDTO{" +
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
