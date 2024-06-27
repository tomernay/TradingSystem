package Domain.Store.Discounts;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Conditions.Condition;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public class DiscountCondition implements Discount{
    private Discount discount;
    private Condition condition;
    private Integer discountID;
    private DiscountType discountType;




    public DiscountCondition(Discount discount, Condition condition, Integer discountID) {
        this.discount = discount;
        this.condition = condition;
        this.discountID = discountID;
        this.discountType = DiscountType.CONDITION;
    }
    public Response<Double> CalculatorDiscount(Map<ProductDTO, Integer> products) {
        if(condition.isValid(products)){
            return discount.CalculatorDiscount(products);
        }
        return new Response<>(false, "Failed to calculate discount");
    }

    @Override
    public Integer getDiscountID() {
        return discountID;
    }


    @Override
    public Integer getStoreID() {
        return discount.getStoreID();
    }

    @Override
    public DiscountType getDiscountType() {
        return null;
    }

    @Override
    public Double getPercent() {
        return discount.getPercent();
    }

    @Override
    public Integer getProductID() {
        return discount.getProductID();
    }

    @Override
    public String getCategory() {
        return "";
    }

    @Override
    public Discount getDiscount1() {
        return discount;
    }

    @Override
    public Discount getDiscount2() {
        return null;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "(" + discount.toString() + ") if (" + condition.toString() + ")";
    }


}
