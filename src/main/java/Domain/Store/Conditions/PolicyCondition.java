package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

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
    public boolean isValid(Map<ProductDTO,Integer> products) {
        if (condition1.isValid(products)){
            if(!condition2.isValid(products)){
                return false;
            }
        }
        return true;
    }


    @Override
    public int getConditionID() {
        return conditionID;
    }
}
