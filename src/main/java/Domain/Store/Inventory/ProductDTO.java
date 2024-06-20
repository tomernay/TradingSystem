package Domain.Store.Inventory;

import java.util.ArrayList;

public class ProductDTO {
    private Integer storeID;     // The ID of the store to which the product belongs
    private Integer productID;  // The unique ID of the product
    private String productName;        // The name of the product
    private String desc;        // The description of the product
    private Double price;       // The price of the product
    private Integer quantity;       // The quantity of the product available in the inventory
    private String storeName;
    public ArrayList<String> categories; // The category that a product is related to

    // Existing full constructor
    public ProductDTO(Integer storeID, Integer productID, String productName, String desc, Double price, Integer quantity, String storeName, ArrayList<String> categories) {
        this.storeID = storeID;
        this.productID = productID;
        this.productName = productName;
        this.desc = desc;
        this.price = price;
        this.quantity = quantity;
        this.storeName = storeName;
        this.categories = categories != null ? categories : new ArrayList<>();
    }

    // New minimal constructor
    public ProductDTO(Integer storeID, Integer productID, int quantity) {
        this.storeID = storeID;
        this.productID = productID;
        this.quantity = quantity;
        this.categories = new ArrayList<>();
    }

    public ProductDTO(Product product) {
        this.storeID = product.getStoreID();
        this.productID = product.getProductID();
        this.productName = product.getName();
        this.desc = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.storeName = product.getStoreName();
        this.categories = new ArrayList<>();
    }

    public ProductDTO(Product product, ArrayList<String> categories) {
        this.storeID = product.getStoreID();
        this.productID = product.getProductID();
        this.productName = product.getName();
        this.desc = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.categories = categories;
        this.storeName = product.getStoreName();
    }

    public Integer getProductID() {
        return productID;
    }

    public Integer getStoreID() {
        return storeID;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return desc;
    }

    public String getStoreName() {
        return storeName;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "storeID='" + storeID + '\'' +
                ", productID=" + productID +
                ", name='" + (productName != null ? productName : "N/A") + '\'' +
                ", desc='" + (desc != null ? desc : "N/A") + '\'' +
                ", price=" + (price != 0 ? price : "N/A") +
                ", quantity=" + quantity +
                ", storeName='" + (storeName != null ? storeName : "N/A") + '\'' +
                ", categories=" + categories +
                '}';
    }
}
