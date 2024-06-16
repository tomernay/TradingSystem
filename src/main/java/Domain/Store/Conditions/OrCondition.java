package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.Map;

public class OrCondition implements Condition{
    private Condition condition1;
    private Condition condition2;
    private int conditionID;

    public OrCondition(Condition condition1, Condition condition2, int conditionID) {
        this.condition1 = condition1;
        this.condition2 = condition2;
    }

    public boolean isValid(Map<ProductDTO,Integer> products) {
        return condition1.isValid(products) || condition2.isValid(products);
    }

    @Override
    public int getConditionID() {
        return conditionID;
    }
}
