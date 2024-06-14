package Domain.Store.conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;

public class AndConition implements  Condition{

    private Condition condition1;
    private Condition condition2;

    @Override
    public boolean isValid(List<ProductDTO> products) {
        return condition1.isValid(products) && condition2.isValid(products);
    }
}
