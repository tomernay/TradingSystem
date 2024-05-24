package Domain;

import java.util.Map;

public class Order {
    private int orderID;
    private String storeID;
    private String username;
    private Map<String,Map<String,String>> products; /// <productID, <PARMETER, VALUE>>
    ///LIST OF PARAMETERS: quantity, price


    public Order (int orderID, String storeID, String username, Map<String,Map<String,String>> products) {
        this.orderID = orderID;
        this.storeID = storeID;
        this.username = username;
        this.products = products;
    }

    public int getOrderID() {
        return orderID;
    }

    public Map<String, Map<String, String>> getProducts() {
        return products;
    }

    public String getStoreID() {
        return storeID;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", storeID='" + storeID + '\'' +
                ", username='" + username + '\'' +
                ", products=" + products +
                '}';
    }
}
