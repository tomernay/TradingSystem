package Domain.Store.conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;
import java.util.Objects;

public class SimpleCondition implements Condition{
    private String conditionID;
    private Integer productID;
    private String category;
    private Integer minAmount;
    private Integer maxAmount;
    private Double price;


    public SimpleCondition(String conditionID, Integer productID, String category, int minAmount, int maxAmount,double price) {
        this.conditionID = conditionID;
        this.productID = productID;
        this.category = category;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.price = price;
    }

    public String getConditionID() {
        return conditionID;
    }



    public Integer getProductID() {
        return productID;
    }

    public String getCategory() {
        return category;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public boolean isValid(List<ProductDTO> products) {
        double amount = 0;
        for(ProductDTO product : products){
            if(category != null){
                if(product.getCategories().contains(category)){
                    amount = amount + 1;
                }
            }
            if (productID != null){
                if (Objects.equals(product.getProductID(), productID)){
                    amount = amount + 1;
                }
            }
            if(price != null){
                amount = amount + product.getPrice();
            }
        }
        return (minAmount == null || !(minAmount > amount)) && (maxAmount == null || !(maxAmount < amount));
    }
}
