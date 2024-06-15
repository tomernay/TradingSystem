package Domain.Store.conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;

public class AndConition implements  Condition{

    private Condition condition1;
    private Condition condition2;
    private int conditionID;

    AndConition(Condition condition1, Condition condition2,int conditionID) {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.conditionID = conditionID;
    }

    @Override
    public boolean isValid(List<ProductDTO> products) {
        return condition1.isValid(products) && condition2.isValid(products);
    }


    @Override
    public int getConditionID() {
        return conditionID;
    }
}
