package Domain.Store.Discounts;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Conditions.Condition;
import Utilities.Response;

import java.util.Map;

public class DiscountCondition implements Discount{
    private Discount discount;
    private Condition condition;
    private int discountID;
    private DiscountType discountType;




    public DiscountCondition(Discount discount, Condition condition, int discountID) {
        this.discount = discount;
        this.condition = condition;
        this.discountID = discountID;
        this.discountType = DiscountType.CONDITION;
    }
    public Response<Double> CalculatorDiscount(Map<ProductDTO,Integer> products) {
        if(condition.isValid(products)){
            return discount.CalculatorDiscount(products);
        }
        return new Response<>(false, "Failed to calculate discount");
    }

    @Override
    public int getDiscountID() {
        return discountID;
    }


    @Override
    public String getStoreID() {
        return discount.getStoreID();
    }

    @Override
    public DiscountType getDiscountType() {
        return null;
    }

    @Override
    public String getPercent() {
        return discount.getPercent();
    }

    @Override
    public String getProductID() {
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


}
