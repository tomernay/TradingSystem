package Domain.Store.conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;

public class OrCoditions implements Condition{
    private Condition condition1;
    private Condition condition2;

    public OrCoditions(Condition condition1, Condition condition2) {
        this.condition1 = condition1;
        this.condition2 = condition2;
    }

    public boolean isValid(List<ProductDTO> products) {
        return condition1.isValid(products) || condition2.isValid(products);
    }
}
