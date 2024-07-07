package Domain.Store.Conditions;

import Domain.Store.Discounts.TYPE;
import Domain.Store.Inventory.ProductDTO;

import java.util.Map;
import java.util.Objects;

public class SimpleCondition implements Condition{
    private final Integer conditionID;
    private final Double minAmount;
    private final Double maxAmount;
    private final Double amount;
    private final String productName;
    private final String value;
    private final TYPE type;


    public SimpleCondition(Integer conditionID, TYPE type,String value, Double amount, Double minAmount, Double maxAmount, String productName) {
        this.conditionID = conditionID;
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.productName = productName;
        this.value = value;
        this.type = type;
    }


    public Integer getConditionID() {
        return conditionID;
    }

    @Override
    public Integer getProductID() {
        return 0;
    }

    @Override
    public String getCategory() {
        return "";
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
            if((TYPE.PRODUCT.equals(type) && product.getProductID().toString().equals(value) ) || (type.equals(TYPE.CATEGORY) && product.getCategories().contains(value))){

                amount = amount + products.get(product);
            }
            if(type.equals(TYPE.PRICE)){
                amount = amount + product.getPrice()*products.get(product);
            }

        }
        return (minAmount == null || !(minAmount > amount)) && (maxAmount == null || !(maxAmount < amount));
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (TYPE.PRODUCT.equals(type)) {
            result.append("Product name: ").append(productName);
        }
        if (TYPE.CATEGORY.equals(type)){
            result.append("Category: ").append(value);
        }
        if (TYPE.PRICE.equals(type)) {
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
