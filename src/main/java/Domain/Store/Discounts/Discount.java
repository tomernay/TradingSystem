package Domain.Store.Discounts;

import Domain.Store.Conditions.Condition;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;
import java.util.Map;

public interface Discount {




    public Response<String> CalculatorDiscount(Map<ProductDTO,Integer> products);

    public int getDiscountID();
    public String getStoreID();
    public DiscountType getDiscountType();
    public String getPercent();
    public String getProductID();
    public String getCategory();
    public Discount getDiscount1();
    public Discount getDiscount2();
    public Condition getCondition();



}

