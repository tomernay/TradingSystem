package Domain.Store.Discounts;

import Domain.Store.Conditions.Condition;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;
import java.util.Map;

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


    public Response<String> CalculatorDiscount(Map<ProductDTO,Integer> products) {
        double discount = 0;
        for (ProductDTO product : products.keySet()) {
            if (productID == null || String.valueOf(product.getProductID()).equals(productID)){
                if (category == null || product.getCategories().contains(category)){
                        discount += product.getPrice() * Double.parseDouble(percent) * products.get(product);
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
        if (productID == null)
            return null;
        return productID;
    }
    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public Discount getDiscount1() {
        return null;
    }

    @Override
    public Discount getDiscount2() {
        return null;
    }

    @Override
    public Condition getCondition() {
        return null;
    }

}
