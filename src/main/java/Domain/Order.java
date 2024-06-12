package Domain;

import java.util.Map;

public class Order {
    private final int orderID;
    private final String storeID;
    private final String username;
    private final Map<String,Map<String,String>> products; /// <productID, <PARAMETER, VALUE>>
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
