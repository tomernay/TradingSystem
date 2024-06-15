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
                if (category == null || product.getCategories().contains(category)){
                        discount += product.getPrice() * Double.parseDouble(percent);
                    }
                }
            }
        return new Response<String>(true,String.valueOf(discount));
    }
    @Override
    public int getDiscountID() {
        return discountID;
    }
    @Override
    public String getStoreID() {
        return String.valueOf(discountID);
    }
    @Override
    public DiscountType getDiscountType() {
        return DiscountType.SIMPLE;
    }
    @Override
    public String getPercent() {
        return String.valueOf(percent);
    }
    @Override
    public String getProductID() {
        return String.valueOf(productID);
    }
    @Override
    public String getCategory() {
        return String.valueOf(category);
    }

}
