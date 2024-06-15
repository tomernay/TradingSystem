package Domain.Store.Inventory;

import java.util.ArrayList;

public class ProductDTO {
    private String storeID;     // The ID of the store to which the product belongs
    private Integer productID;  // The unique ID of the product
    private String name;        // The name of the product
    private String desc;        // The description of the product
    private double price;          // The price of the product
    private int quantity;       // The quantity of the product available in the inventory
    private String storeName;

    public ArrayList<String> categories; // The category that a product is related to


    public ProductDTO(String storeID, Integer productID, String name, double price, int quantity) {
        this.storeID = storeID;
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public ProductDTO(Product product) {
        this.storeID = product.getStoreID();
        this.productID = product.getProductID();
        this.name = product.getName();
        this.desc = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.storeName = product.getStoreName();
        //this.categories = product.getCategories();
    }

    public ProductDTO(Product product, ArrayList<String> categories) {
        this.storeID = product.getStoreID();
        this.productID = product.getProductID();
        this.name = product.getName();
        this.desc = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.categories = categories;
        this.storeName = product.getStoreName();
    }

    public String getStoreID() {return storeID;}
    public String getName() {
        return name;
    }
    public Integer getProductID() {
        return productID;
    }
    public double getPrice() {
        return price;
    }
    public String getDescription() {return desc;}
    public int getQuantity() {return quantity;}
    public String getStoreName() {
        return storeName;
    }
    public ArrayList<String> getCategories() {return categories;}

    public void setQuantity(Integer integer) {
        this.quantity = integer;
    }




    public String toString() {
        return "ProductINFO{" +
                "storeID='" + storeID + '\'' +
                ", productID=" + productID +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", categories=" + categories +
                '}';
    }

}




