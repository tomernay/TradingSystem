package Domain.Store.Discounts;

import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;

public class SimpleDiscount implements Discount{

    private String percent;
    private String productID;
    private String category;
    private int discountID;


    public SimpleDiscount(String percent, String storeID, String productID, String category, int discountID) {
        this.percent = percent;
        this.productID = productID;
        this.category = category;
        this.discountID = discountID;
    }


    public Response<String> CalculatorDiscount(List<ProductDTO> products) {
        double discount = 0;
        for (ProductDTO product : products) {

            if (productID == null || String.valueOf(product.getProductID()).equals(productID)){
                //if (category == null || in product.getCategories().contains(category)){
                        discount += product.getPrice() * Double.parseDouble(percent);
                   // }
                }
            }
        return new Response<String>(true,String.valueOf(discount));
    }
}
