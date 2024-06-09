package Domain.Store.Discounts;

import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.ProductDTO;
import Utilities.Response;

import java.util.List;

public class MaxDiscount implements Discount{
    private Discount discount1;
    private Discount discount2;

    public MaxDiscount(Discount discount1, Discount discount2) {
        this.discount1 = discount1;
        this.discount2 = discount2;
    }

    @Override
    public Response<String> CalculatorDiscount(List<ProductDTO> products) {
        Response<String> response1 = discount1.CalculatorDiscount(products);
        Response<String> response2 = discount2.CalculatorDiscount(products);
        if(!response1.isSuccess() || !response2.isSuccess()){
            return new Response<>(false, "Failed to calculate discount");
        }
        double discount1 = Double.parseDouble(response1.getData());
        double discount2 = Double.parseDouble(response2.getData());
        return new Response<>(true, String.valueOf(Math.max(discount1, discount2)));
    }


}
