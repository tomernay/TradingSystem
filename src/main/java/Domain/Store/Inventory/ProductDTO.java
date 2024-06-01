package Domain.Store.Inventory;
import cn.hutool.json.ObjectMapper;

import java.util.ArrayList;

public class ProductDTO {
    private final String storeID;     // The ID of the store to which the product belongs
    //private String storeName;   // The name of the store
    private final Integer productID;  // The unique ID of the product
    private final String name;        // The name of the product
    //private String desc;        // The description of the product
    private final int price;          // The price of the product
    private final int quantity;       // The quantity of the product available in the inventory

    public ArrayList<String> categories; // The category that a product is related to


    public ProductDTO(String storeID, Integer productID, String name, int price, int quantity, ArrayList<String> categories) {
        this.storeID = storeID;
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.categories = categories;
    }

    public ProductDTO(Product product) {
        this.storeID = product.getStoreID();
        this.productID = product.getProductID();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        //this.categories = product.getCategories();
    }
}




