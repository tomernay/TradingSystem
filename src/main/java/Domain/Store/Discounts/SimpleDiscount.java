package Domain.Store.Discounts;

import Domain.Store.Conditions.Condition;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.Map;

public class SimpleDiscount implements Discount{

    private final Double percent;
    private final Integer productID;
    private final String category;
    private final Integer discountID;
    private final String productName;


    public SimpleDiscount(Double percent, Integer productID, String category, Integer discountID, String productName) {
        this.percent = percent;
        this.productID = productID;
        this.category = category;
        this.discountID = discountID;
        this.productName = productName;
    }



    public Response<Double> CalculatorDiscount(Map<ProductDTO, Integer> products) {
        double discount = 0;
        for (ProductDTO product : products.keySet()) {
            if (productID == null || product.getProductID().equals(productID)){
                if (category == null || product.getCategories().contains(category)){
                    discount += product.getPrice() * percent * products.get(product) / 100;
                }
            }
        }
        return new Response<Double>(true,"Calculator Discount",discount);
    }
    @Override
    public Integer getDiscountID() {
        return discountID;
    }
    @Override
    public Integer getStoreID() {
        return discountID;
    }
    @Override
    public DiscountType getDiscountType() {
        return DiscountType.SIMPLE;
    }
    @Override
    public Double getPercent() {
        return percent;
    }
    @Override
    public Integer getProductID() {
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
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(percent + "% off");
        if (productID != null) {
            result.append(" on ").append(productName);
        }
        if (category != null && !category.isEmpty()) {
            result.append(" on category ").append(category);
        }
        return result.toString();
    }

}
