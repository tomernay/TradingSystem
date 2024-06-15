package Domain.Store.Discounts;

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
        public Response<String> CalculatorDiscount(Map<ProductDTO,Integer> products) {
            Response<String> response1 = discount1.CalculatorDiscount(products);
            Response<String> response2 = discount2.CalculatorDiscount(products);
            if(!response1.isSuccess() || !response2.isSuccess()){
                return new Response<>(false, "Failed to calculate discount");
            }
            double discount1 = Double.parseDouble(response1.getData());
            double discount2 = Double.parseDouble(response2.getData());
            return new Response<>(true, String.valueOf(discount1 + discount2));
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
            return String.valueOf(discount1.getPercent()) + String.valueOf(discount2.getPercent());
        }
        @Override
        public String getProductID() {
            return String.valueOf(discount1.getProductID()) + String.valueOf(discount2.getProductID());
        }
        @Override
        public String getCategory() {
            return String.valueOf(discount1.getCategory()) + String.valueOf(discount2.getCategory());
        }
}
