package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.Map;

public interface Condition {
    boolean isValid(Map<ProductDTO,Integer> products);

    int getConditionID();
}
