package Domain.Store.Discounts;

import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;

public interface Discount {




    public Response<String> CalculatorDiscount(List<ProductDTO> products);

    public int getDiscountID();
    public String getStoreID();
    public DiscountType getDiscountType();
    public String getPercent();
    public String getProductID();
    public String getCategory();



}

