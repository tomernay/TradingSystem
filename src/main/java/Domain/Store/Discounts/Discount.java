package Domain.Store.Discounts;

import Domain.Store.Conditions.Condition;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public interface Discount {




    public Response<Double> CalculatorDiscount(Map<ProductDTO, Integer> products);

    public Integer getDiscountID();
    public Integer getStoreID();
    public DiscountType getDiscountType();
    public Double getPercent();
    public Integer getProductID();
    public String getCategory();
    public Discount getDiscount1();
    public Discount getDiscount2();
    public Condition getCondition();
    public String toString();
    public String getType();



}

