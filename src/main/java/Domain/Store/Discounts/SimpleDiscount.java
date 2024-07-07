package Domain.Store.Discounts;

import Domain.Store.Conditions.Condition;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.Map;

public class SimpleDiscount implements Discount{

    private final Double percent;
    private final Integer discountID;
    private final String productName;
    private final TYPE type;
    private final String value;


    public SimpleDiscount(Double percent, Integer discountID, String productName, TYPE type, String value) {
        this.percent = percent;

        this.discountID = discountID;
        this.productName = productName;
        this.type = type;
        this.value = value;
    }



    public Response<Double> CalculatorDiscount(Map<ProductDTO, Integer> products) {
        double discount = 0;
        for (ProductDTO product : products.keySet()) {
            if((type.equals(TYPE.PRODUCT) && product.getProductID().toString().equals(value)) || (type.equals(TYPE.CATEGORY) && product.getCategories().contains(value)) || type.equals(TYPE.PRICE)){
                discount += product.getPrice() * percent * products.get(product) / 100;
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
        return Integer.valueOf(value);
    }

    @Override
    public String getCategory() {
        return value;
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
        if (type == TYPE.PRODUCT) {
            result.append(" on ").append(productName);
        }
        if (TYPE.CATEGORY == type) {
            result.append(" on category ").append(value);
        }
        return result.toString();
    }

}
