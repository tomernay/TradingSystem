package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.Map;
import java.util.Objects;

public class SimpleCondition implements Condition{
    private int conditionID;
    private Integer productID;
    private String category;
    private Double minAmount;
    private Double maxAmount;
    private Double amount;
    private Double price;


    public SimpleCondition(int conditionID, Integer productID, String category, Double amount, Double minAmount, Double maxAmount,Double price) {
        this.conditionID = conditionID;
        this.productID = productID;
        this.category = category;
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.price = price;
    }

    public String getPrice() {
        if (price == null){
            return null;
        }
        return String.valueOf(price);
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

    public String getMinAmount() {
        if (minAmount == null){
            return null;
        }
        return String.valueOf(minAmount);
    }

    public String getMaxAmount() {
        if (maxAmount == null){
            return null;
        }
        return String.valueOf(maxAmount);
    }

    @Override
    public String getAmount() {
        if (amount == null){
            return null;
        }
        return String.valueOf(amount);
    }

    @Override
    public Condition getCondition1() {
        return null;
    }

    @Override
    public Condition getCondition2() {
        return null;
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
