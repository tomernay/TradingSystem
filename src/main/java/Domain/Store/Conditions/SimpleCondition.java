package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.Map;
import java.util.Objects;

public class SimpleCondition implements Condition{
    private int conditionID;
    private Integer productID;
    private String category;
    private Integer minAmount;
    private Integer maxAmount;
    private Double price;


    public SimpleCondition(int conditionID, Integer productID, String category, int minAmount, int maxAmount,double price) {
        this.conditionID = conditionID;
        this.productID = productID;
        this.category = category;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.price = price;
    }

    public int getConditionID() {
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
    public boolean isValid(Map<ProductDTO,Integer> products) {
        double amount = 0;
        for(ProductDTO product : products.keySet()){
            if(category != null){
                if(product.getCategories().contains(category)){
                    amount = amount + products.get(product);
                }
            }
            if (productID != null){
                if (Objects.equals(product.getProductID(), productID)){
                    amount = amount + products.get(product);
                }
            }
            if(price != null){
                amount = amount + product.getPrice()*products.get(product);
            }
        }
        return (minAmount == null || !(minAmount > amount)) && (maxAmount == null || !(maxAmount < amount));
    }
}
