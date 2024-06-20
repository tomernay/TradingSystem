package Domain.Store.Discounts;

import Domain.Store.Conditions.Condition;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public class PlusDiscount implements Discount {

    private Discount discount1;
    private Discount discount2;
    private int discountID;

    public PlusDiscount(Discount discount1, Discount discount2, int discountID) {
        this.discount1 = discount1;
        this.discount2 = discount2;
        this.discountID = discountID;
    }


    @Override
    public Response<Double> CalculatorDiscount(List<ProductDTO> products) {
        Response<Double> response1 = discount1.CalculatorDiscount(products);
        Response<Double> response2 = discount2.CalculatorDiscount(products);
        if(!response1.isSuccess() || !response2.isSuccess()){
            return new Response<>(false, "Failed to calculate discount");
        }
        double discount1 = response1.getData();
        double discount2 = response2.getData();
        return new Response<>(true," Calculator Discount", discount1 + discount2);
    }
    @Override
    public int getDiscountID() {
      return discountID;
     }
    @Override
    public String getStoreID() {
        return String.valueOf(discount1.getStoreID());
    }
    @Override
    public DiscountType getDiscountType() {
        return DiscountType.SIMPLE;
    }
    @Override
    public String getPercent() {
        return null;
    }
    @Override
    public String getProductID() {
        return null;
    }
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public Discount getDiscount1() {
        return discount1;
    }

    @Override
    public Discount getDiscount2() {
        return discount2;
    }

    @Override
    public Condition getCondition() {
        return null;
    }
}
