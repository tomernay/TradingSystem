package Domain.Store.Inventory;
import Utilities.Response;
import Utilities.SystemLogger;
import jakarta.persistence.*;


/**
 * Represents a product in the store inventory.
 * Each product has a unique product ID and is associated with a store.
 */
@Entity
@Table(name = "products")
public class Product {
    @Column(name = "store_id")
    private Integer storeID;     // The ID of the store to which the product belongs

    @Column(name = "store_name")
    private String storeName;   // The name of the store

    @Id
    @Column(name = "product_id")
    private Integer productID;  // The unique ID of the product

    @Column(name = "product_name")
    private String name;        // The name of the product

    @Column(name = "product_desc")
    private String desc;        // The description of the product

    @Column(name = "product_price")
    private Double price;          // The price of the product

    @Column(name = "product_quantity")
    private Integer quantity;       // The quantity of the product available in the inventory

    // Default constructor for JPA
    protected Product() {
        // for JPA / hibernate
    }


    /**
     * Constructs a Product object using a Builder.
     * This constructor is private and should only be accessed through the Builder.
     *
     * @param builder the Builder object containing product attributes
     */

    private Product(Builder builder) {
        this.storeID = builder.storeID;
        this.storeName = builder.storeName;
        this.productID = builder.productID;
        this.name = builder.name;
        this.desc = builder.desc;
        this.price = builder.price;
        this.quantity = builder.quantity;
        //  this.categories = builder.categories;
    }

    /**
     * Builder class for constructing Product objects.
     * Provides methods to set various attributes of the Product.
     */
    public static class Builder {
        private Integer storeID;
        private String storeName;
        private Integer productID;
        private String name;
        private String desc;
        private Double price;
        private Integer quantity;
        //    public ArrayList<String> categories;

        public Builder(Integer storeID, String name, Integer productID) {
            this.name = name;
            this.storeID = storeID;
            this.productID = productID;
        }

        public Builder storeID(Integer storeID) {
            if (storeID == null) {
                throw new IllegalArgumentException("Store ID cannot be null or empty");
            }
            this.storeID = storeID;
            return this;
        }

        public Builder storeName(String storeName){
            this.storeName = storeName;
            return this;
        }

        public Builder productID(Integer productID) {
            if (productID == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            this.productID = productID;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder price(Double price) {
            if (price <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0");
            }
            this.price = price;
            return this;
        }

        public Builder quantity(int _quantity) {
            if (_quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            this.quantity = _quantity;
            return this;
        }

        // call the product constructor //
        public Product build() {
            return new Product(this);
        }
    }

    // Getters //
    public Integer getStoreID() {
        return storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public int getProductID() {
        return productID;
    }

    //
    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }


    // Setters
    public void setStoreID(Integer storeID) {
        this.storeID = storeID;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    //
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public boolean setPrice(double price) {
        if(price > 0){
            this.price = price;
            return true;
        }
        else {
            return false;
        }
    }


    public synchronized Response<String> setQuantity(int newQuantity) {
        if (newQuantity < 0) {
            SystemLogger.error("[ERROR] Invalid Quantity: Quantity cannot be negative");
            return Response.error("Invalid Quantity: Quantity cannot be negative", null);
        }
        this.quantity = newQuantity;
        SystemLogger.info("[SUCCESS] Quantity set successfully for product: " + this.name + " to " + this.quantity);
        return Response.success("Quantity set successfully for product: " + this.name, null);
    }


    public synchronized Response<String> addQuantity(int amountToAdd) {
        return setQuantity(this.quantity + amountToAdd);
    }

    @Override
    public String toString() {
        return "Product info {" +
                "storeId='" + storeID + '\'' +
                "storeName='" + storeName + '\'' +
                ", prodId=" + productID +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}