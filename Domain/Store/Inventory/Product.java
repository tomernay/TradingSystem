package Domain.Store.Inventory;

public class Product {
    private String storeName;
    private int price;
    private int id;

    // Constructor with store name parameter
    public Product(String storeName,int price,int id) {
        this.storeName = storeName;
        this.price = price;
        this.id = id;
    }

    // Empty constructor
    public Product() {
        this.storeName = "";
        this.price = 0; // default value for price
        this.id = 0; // default value for id
    }

    // Getter and setter for storeName
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    // Getter and setter for price
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Getter and setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
