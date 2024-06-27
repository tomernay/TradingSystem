package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SimpleCondition implements Condition{
    private Integer conditionID;
    private Integer productID;
    private String category;
    private Double minAmount;
    private Double maxAmount;
    private Double amount;
    private Boolean price;


    public SimpleCondition(Integer conditionID, Integer productID, String category, Double amount, Double minAmount, Double maxAmount,Boolean price) {
        this.conditionID = conditionID;
        this.productID = productID;
        this.category = category;
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.price = price;
    }

    public Boolean getPriceIndicator() {
        if (price == null){
            return null;
        }
        return price;
    }

    public Integer getConditionID() {
        return conditionID;
    }



    public Integer getProductID() {
        return productID;
    }

    public String getCategory() {
        return category;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    @Override
    public Double getAmount() {
        return amount;
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
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (productID != null) {
            result.append("Product ID: ").append(productID);
        }
        if (category != null && !category.isEmpty()) {
            result.append("Category: ").append(category);
        }
        if (price != null) {
            result.append("Price ");
            if (amount != null) {
                result.append("is exactly: ").append(amount);
            }
            if (minAmount != null && maxAmount != null) {
                result.append("is between: ").append(minAmount).append(" and ").append(maxAmount);
            }
            if (maxAmount != null && minAmount == null) {
                result.append("is at most: ").append(maxAmount);
            }
            if (minAmount != null && maxAmount == null) {
                result.append("is at least: ").append(minAmount);
            }
        } else {
            if (amount != null) {
                result.append(" has a quantity of exactly: ").append(amount).append(" items");
            }
            if (minAmount != null && maxAmount != null) {
                result.append(" has a quantity between: ").append(minAmount).append(" and ").append(maxAmount).append(" items");
            }
            if (maxAmount != null && minAmount == null) {
                result.append(" has a quantity of at most: ").append(maxAmount).append(" items");
            }
            if (minAmount != null && maxAmount == null) {
                result.append(" has a quantity of at least: ").append(minAmount).append(" items");
            }
        }
        return result.toString();
    }

}
