package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;
import java.util.Map;

public class XorCondition implements  Condition{
    private final Condition condition1;
    private final Condition condition2;
    private final Integer conditionID;

    public XorCondition(Condition condition1, Condition condition2, Integer conditionID) {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.conditionID = conditionID;
    }

    @Override
    public boolean isValid(Map<ProductDTO, Integer> products) {
        return condition1.isValid(products) ^ condition2.isValid(products);
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

    @Override
    public String toString() {
        return "(" + condition1.toString() + ") XOR (" + condition2.toString() + ")";
    }
}
