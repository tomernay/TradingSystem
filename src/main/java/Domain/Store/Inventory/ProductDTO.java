package Domain.Store.Inventory;
import cn.hutool.json.ObjectMapper;

import java.util.ArrayList;

public class ProductDTO {
    private String storeID;     // The ID of the store to which the product belongs
    //private String storeName;   // The name of the store
    private Integer productID;  // The unique ID of the product
    private String name;        // The name of the product
    //private String desc;        // The description of the product
    private int price;          // The price of the product
   // private int quantity;       // The quantity of the product available in the inventory

    public ArrayList<String> categories; // The category that a product is related to


    public ProductDTO(String storeID, Integer productID, String name, int price, ArrayList<String> categories) {
        this.storeID = storeID;
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.categories = categories;
    }

}



