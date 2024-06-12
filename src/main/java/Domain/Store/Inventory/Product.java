package Domain.Store.Inventory;
import Utilities.Response;
import Utilities.SystemLogger;


/**
 * Represents a product in the store inventory.
 * Each product has a unique product ID and is associated with a store.
 */
public class Product {
    private String storeID;     // The ID of the store to which the product belongs,
    private String storeName;   // The name of the store
    private int productID;  // The unique ID of the product
    private String name;        // The name of the product
    private String desc;        // The description of the product
    private double price;          // The price of the product
    private int quantity;       // The quantity of the product available in the inventory
    //private ArrayList<String> categories; // The category that a product is related to

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
        private String storeID;
        private String storeName;
        private Integer productID;
        private String name;
        private String desc;
        private double price;
        private int quantity;
    //    public ArrayList<String> categories;

        public Builder(String storeID,String name, int productID) {
            this.name = name;
            this.storeID = storeID;
            this.productID = productID;
        }

        public Builder storeID(String _storeID) {
            if (_storeID == null || _storeID.isEmpty()) {
                throw new IllegalArgumentException("Store ID cannot be null or empty");
            }
            this.storeID = _storeID;
            return this;
        }

        public Builder storeName(String _storeName){
            this.storeName = _storeName;
            return this;
        }

        public Builder productID(Integer _productID) {
            if (_productID == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            this.productID = _productID;
            return this;
        }

        public Builder name(String _name) {
            this.name = _name;
            return this;
        }

        public Builder desc(String _desc) {
            this.desc = _desc;
            return this;
        }

        public Builder price(double _price) {
            if (_price <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0");
            }
            this.price = _price;
            return this;
        }

        public Builder quantity(int _quantity) {
            if (_quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            this.quantity = _quantity;
            return this;
        }

//        public Builder categories(ArrayList<String> _categories){
//            if(_categories.size() <= 0){
//                throw new IllegalArgumentException("A product must be associated with at least one category");
//            }
//            this.categories = _categories;
//            return this;
//        }

        // call the product constructor //
        public Product build() {
            return new Product(this);
        }
    }

    // Getters //
    public String getStoreID() {
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

//    public ArrayList<String> getCategories() {
//        return categories;
//    }

    // Setters
    public void setStoreID(String _storeID) {
        this.storeID = _storeID;
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

    public boolean setPrice(double _price) {
        if(_price > 0){
            this.price = _price;
            return true;
        }
        else {
            return false;
        }
    }

//    /**
//     * Sets the categories for the product.
//     * @param _categories The list of categories to be set for the product.
//     */
//    public void setCategories(ArrayList<String> _categories) {
//        this.categories = _categories;
//    }


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
