package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;
import java.util.Map;

public class PolicyCondition implements Condition{
    private Condition condition1;
    private Condition condition2;
    private int conditionID;

    public PolicyCondition(Condition condition1, Condition condition2, int conditionID) {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.conditionID = conditionID;
    }

    @Override
    public boolean isValid(Map<ProductDTO, Integer> products) {
        if (condition1.isValid(products)){
            if(!condition2.isValid(products)){
                return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        return "(" + condition1.toString() + ") if (" + condition2.toString() + ")";
    }


        @Override
    public Integer getConditionID() {
        return conditionID;
    }

    @Override
    public Integer getProductID() {
        return null;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public Double getMinAmount() {
        return null;
    }

    @Override
    public Double getMaxAmount() {
        return null;
    }

    @Override
    public Double getAmount() {
        return null;
    }

    @Override
    public Condition getCondition1() {
        return condition1;
    }

    @Override
    public Condition getCondition2() {
        return condition2;
    }

    @Override
    public Boolean getPriceIndicator() {
        return null;
    }
}