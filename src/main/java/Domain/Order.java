package Domain;

import Domain.Store.Inventory.ProductDTO;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer orderID;
    @Column(nullable = true)
    private Integer storeID;
    @Column(nullable = true)
    private String username;
    @Column(nullable = true)
    private String deliveryAddress;
    @Column(nullable = true)
    private Date orderDate;
    @Transient
    private List<ProductDTO> products; // <productID, <PARAMETER, VALUE>>
    @Column(nullable = true)
    private String status;

    public Order(Integer orderID, Integer storeID, String username, String deliveryAddress, List<ProductDTO> products) {
        this.orderID = orderID;
        this.storeID = storeID;
        this.username = username;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = new Date(); // Set to current date/time
        this.products = products;
        this.status = "Pending";
    }
    public Order(){
        this.products = new ArrayList<>();
    }

    public int getOrderID() {
        return orderID;
    }

    public Integer getStoreID() {
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
