package src.main.java.Domain.Store.Inventory;
import Utilities.Response;
import cn.hutool.json.ObjectMapper;
import java.util.ArrayList;

/**
 * Represents a product in the store inventory.
 * Each product has a unique product ID and is associated with a store.
 */
public class Product {
    private String storeID;     // The ID of the store to which the product belongs,
    private String storeName;   // The name of the store
    private Integer productID;  // The unique ID of the product
    private String name;        // The name of the product
    private String desc;        // The description of the product
    private int price;          // The price of the product
    private int quantity;       // The quantity of the product available in the inventory

    private ArrayList<String> categories;  // The category that a product is related to
    private ArrayList<Review> reviews;


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
        this.categories = builder.categories;
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
        private int price;
        private int quantity;
        public ArrayList<String> categories;

        public Builder(String storeID, int productID) {
            if (storeID == null || storeID.isEmpty()) {
                throw new IllegalArgumentException("Store ID cannot be null or empty");
            }
            if (productID <= 0) {
                throw new IllegalArgumentException("Product ID must be greater than 0");
            }
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

        public Builder price(int _price) {
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

        public Builder categories(ArrayList<String> _categories){
            if(_categories.size() <= 0){
                throw new IllegalArgumentException("A product must be associated with at least one category");
            }
            this.categories = _categories;
            return this;
        }

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

    public Integer getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

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


    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPrice(int _price) {
        this.price = _price;
    }

    /**
     * Sets the categories for the product.
     * @param _categories The list of categories to be set for the product.
     */
    public void setCategories(ArrayList<String> _categories) {
        this.categories = _categories;
    }

    /**
     * Removes a category from the product's category list.
     * @param category The category to be removed.
     * @return Response true if the category was successfully removed,Response false otherwise.
     */
    public Response<String> removeCategory(String category) {
        if(categories.contains(category)){
            categories.remove(category);
            return new Response<>(true, "Category has been successfully removed", category);
        }
        else {
            return new Response<>(false, "Category doesn't exist", category);
        }
    }

    /**
     * Sets the quantity to the specified value.
     *
     * @param newQuantity the new quantity to set
     */
    public synchronized void setQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }


    /**
     * Adds the specified amount to the current quantity.
     *
     * @param amountToAdd the amount to add to the current quantity*/
    public synchronized void addQuantity(int amountToAdd) {
        setQuantity(this.quantity + amountToAdd);
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
