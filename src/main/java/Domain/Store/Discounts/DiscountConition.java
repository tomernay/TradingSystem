package Domain.Store.Discounts;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.conditions.Condition;
import Utilities.Response;

import java.util.List;

public class DiscountConition implements Discount{
    private Discount discount;
    private Condition condition;
    private String discountID;
    private DiscountType discountType;




    public DiscountConition(Discount discount, Condition condition, String discountID) {
        this.discount = discount;
        this.condition = condition;
        this.discountID = discountID;
        this.discountType = DiscountType.CONDITION;
    }
    public Response<String> CalculatorDiscount(List<ProductDTO> products) {
        if(condition.isValid(products)){
            return discount.CalculatorDiscount(products);
        }
        return new Response<>(false, "Failed to calculate discount");
    }

    @Override
    public String getDiscountID() {
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


}
